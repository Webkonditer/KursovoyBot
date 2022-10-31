package com.example.kursovoybot.service;

import com.example.kursovoybot.config.BotConfig;
import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.model.User;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    //Информация, выводимая при выборе в меню раздела help
    private static final String HELP_TEXT = "Этот бот предназначен для создания напоминалок. " +
            "Любой пользователь бота может установить в нем напоминания для себя. И бот в назначенное время " +
            "покажет именно этому пользователю его напоминание.\n\n" +
            "В меню доступны следующие команды:\n\n" +
            "Команда /start выводит приветственное сообщение и регистрирует пользователя в базе.\n\n" +
            "Команда /help выводит раздел помощи.\n\n" +
            "Команда /create_a_reminder создает новое напоминание.\n\n" +
            "Команда /show_my_reminders выводит все Ваши напоминания.\n\n" +
            "Команда /delete удаляет напоминание.";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    final BotConfig config;

    //Флаг добавления нового сообщения
    private boolean newMessageFlag = false;

    public TelegramBot(BotConfig config) {

        this.config = config;

        //Создание пунктов основного меню
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "регистрирует пользователя"));
        listOfCommands.add(new BotCommand("/help", "выводит справку по боту"));
        listOfCommands.add(new BotCommand("/create_a_reminder", "создает новое напоминание"));
        listOfCommands.add(new BotCommand("/show_my_reminders", "список Ваших напоминаний"));
        listOfCommands.add(new BotCommand("/delete", "удаляет напоминание"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            //Если вводится новое напоминание
            if (newMessageFlag) {

                newMessageFlag = false;
                try {
                    LocalDateTime.parse(messageText.substring(0, 16), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                } catch (DateTimeParseException e){
                    var textToSend = EmojiParser.parseToUnicode("Неправильный формат даты и времени " + ":confused:" + " Попробуйте еще раз!");
                    sendMessage(chatId, textToSend);
                    log.info("the user entered an incorrect date");
                }
                LocalDateTime dateTime = LocalDateTime.parse(messageText.substring(0, 16), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                String reminderText = messageText.substring(17);
                User user = userRepository.findById(chatId).orElseThrow();
                if(dateTime.isBefore(LocalDateTime.now())){
                    var textToSend = EmojiParser.parseToUnicode("Дата и время ранее текущего " + ":confused:" + " Попробуйте еще раз!");
                    sendMessage(user.getChatId(), textToSend);
                    log.info("the user entered a date earlier than the current one");
                } else {
                    registerNewReminder(user, reminderText, dateTime);
                    var textToSend = EmojiParser.parseToUnicode("Ваше напоминание успешно сохранено " + ":ok_hand:");
                    sendMessage(user.getChatId(), textToSend);
                }

            } else {

                //Обрабатываем входящие команды
                switch (messageText) {
                    case "/start":
                        try {
                            registerUser(update.getMessage());
                            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    case "/help":
                            sendMessage(chatId, HELP_TEXT);
                        break;

                    case "/create_a_reminder":
                        createNewReminder(chatId);
                        break;

                    case "/show_my_reminders":
                        showMyReminders(chatId);
                        break;

                    case "/delete":
                        delete(chatId);
                        break;

                    //Если введена неизвестная команда
                    default:
                        sendMessage(chatId, "К сожалению Ваша команда не распознана. Пожалуйста выберите команду из меню.");
                        log.info("the user entered an unknown command");
                }
            }
        } else if (update.hasCallbackQuery()) {//Если нажата кнопка.

            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callBackData.equals(YES_BUTTON)) {
                newMessageFlag = true;
                String text = "Отлично! Создайте Ваше новое напоминание, как на образце ниже:\n\n " +
                        "01.01.2022 20:00 Сделать домашнюю работу";
                executeEditMessageText(text, chatId, messageId);
            } else if (callBackData.equals(NO_BUTTON) || callBackData.equals("cancel")) {
                String text = "Хорошо! Выберите другое действие из меню.";
                executeEditMessageText(text, chatId, messageId);
            } else if (callBackData.contains("delete")) {
                notificationTaskRepository.deleteById(Long.parseLong(callBackData.substring(7)));
                String text = "Выбранное напоминание удалено.";
                executeEditMessageText(text, chatId, messageId);
                log.info("the reminder was removed at the request of the user");
            }

        }
    }

    //Обработка запроса на удаление напоминания
    private void delete(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите напоминание, которое необходимо удалить:");
        User user = userRepository.findById(chatId).orElseThrow();
        var reminders = user.getReminders();

        //Сортировка по дате и времени напоминания
        reminders.sort(Comparator.comparing(NotificationTask::getReminderTime));

        //Создание кнопок
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (NotificationTask reminder : reminders) {
            var button = new InlineKeyboardButton();
            var dateTime = reminder.getReminderTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
            button.setText(dateTime + ": " + reminder.getReminderText());
            button.setCallbackData("delete_" + reminder.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        var button = new InlineKeyboardButton();
        button.setText("Отмена");
        button.setCallbackData("cancel");
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        executeMessage(message);

    }

    //Вывод всех напоминаний пользователя
    private void showMyReminders(long chatId) {

        var reminders = notificationTaskRepository.findAllOrderByReminderTime();
        StringBuilder listOfReminders = new StringBuilder();
        for (NotificationTask reminder: reminders){
            if (reminder.getUser().getChatId() == chatId) {
                var dateTime = reminder.getReminderTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
                listOfReminders.append(dateTime).append(": ").append(reminder.getReminderText()).append("\n");
            }
        }
        sendMessage(chatId, listOfReminders.toString());
        log.info("all reminders have been issued at the user's request");

    }

    //Обработка запроса на создание нового напоминания
    private void createNewReminder(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы хотите создать новое напоминание?");

        //Добавление кнопок Да Нет
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData(YES_BUTTON);
        var noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData(NO_BUTTON);
        rowInline.add(yesButton);
        rowInline.add(noButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        executeMessage(message);
    }

    //Регистрация нового пользователя
    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("user saved: " + user);

        }
    }

    //Сохранение в базу нового напоминания
    private void registerNewReminder(User user, String reminderText, LocalDateTime dateTime) {

        NotificationTask reminder = new NotificationTask();
        reminder.setUser(user);
        reminder.setReminderText(reminderText);
        reminder.setReminderTime(dateTime);
        notificationTaskRepository.save(reminder);
        log.info("reminder saved: " + reminder);

    }

    //Ответ на команду start
    private void startCommandReceived(long chatId, String firstName) throws TelegramApiException {

        String answer = EmojiParser.parseToUnicode("Привет, " + firstName + ":blush:" + "! Пожалуйста выберите желаемое действие из меню ниже.");
        sendStartMessage(chatId, answer);
        log.info("Replaed to user " + firstName + " Id: " + chatId);

    }

    //Отправка сообщения пользователю
    private void sendMessage(long chatId, String textToSend){

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);

    }

    private void sendStartMessage(long chatId, String textToSend) throws TelegramApiException{

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        //Добавление клавиатуры к собщению.
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("/help");//Добавление кнопки
        row.add(" /create_a_reminder");
        keyboardRows.add(row);//Добавление строки кнопок
        row = new KeyboardRow();
        row.add("/show_my_reminders");
        row.add("/delete");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);//Формирование клавиатуры
        message.setReplyMarkup(keyboardMarkup);//Добавление клавиатуры
        //----------------------------------------

        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId){

        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int)messageId);

        try{
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }

    }

    private void executeMessage(SendMessage message){

        try{
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }

    }

    //Отправка напоминаний по расписанию
    @Scheduled(cron = "${cron.scheduler}")
    private void sendReminders() throws TelegramApiException {

        var reminders = notificationTaskRepository.findAll();
        for (NotificationTask reminder: reminders){
            if(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).equals(reminder.getReminderTime().truncatedTo(ChronoUnit.MINUTES))){
                sendMessage(reminder.getUser().getChatId(), reminder.getReminderText());
                notificationTaskRepository.deleteById(reminder.getId());
            }
        }
        log.info("scheduled messages sent");

    }

}
