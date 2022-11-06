package com.example.kursovoybot.service;

import com.example.kursovoybot.config.BotConfig;
import com.example.kursovoybot.handrer.command.Command;
import com.example.kursovoybot.handrer.command.CommandHandler;
import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.model.User;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final CommandHandler commandHandler;

    private final UserRepository userRepository;

    private final NotificationTaskRepository notificationTaskRepository;

    private final NewReminderCreate newReminderCreate;

    //Информация, выводимая при выборе в меню раздела help

    private static final String YES_BUTTON = "YES_BUTTON";
    private static final String NO_BUTTON = "NO_BUTTON";
    private static final String CANCEL_BUTTON = "cancel";

    private final String FORMATTER = "dd.MM.yyyy HH:mm";

    private final BotConfig config;

    //Флаг добавления нового сообщения
    private final Map<Long,Boolean> newMessageFlag = new HashMap<>();

    public TelegramBot(BotConfig config,
                       UserRepository userRepository,
                       NotificationTaskRepository notificationTaskRepository,
                       @Lazy NewReminderCreate newReminderCreate,
                       @Lazy CommandHandler commandHandler)
    {
        this.commandHandler = commandHandler;
        this.config = config;
        this.userRepository = userRepository;
        this.notificationTaskRepository = notificationTaskRepository;
        this.newReminderCreate = newReminderCreate;
        setupCommands();
    }

    /**
     *Создание меню.
     *
     */
    private void setupCommands() {
        try {
            List<BotCommand> commands = Arrays.stream(Command.values())
                            .map(c -> BotCommand.builder().command(c.getName()).description(c.getDesc()).build())
                            .collect(Collectors.toList());
            execute(SetMyCommands.builder().commands(commands).build());
        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    public String getFORMATTER() {return FORMATTER;}

    /**
     *Обработка поступающих запросов.
     *
     * @param update  объект запроса
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            //Если вводится новое напоминание
            if (newMessageFlag.get(chatId) != null && newMessageFlag.get(chatId)) {
                newMessageFlag.put(chatId, false);
                newReminderCreate.createNewReminder(chatId, messageText);
            } else if (messageText.trim().startsWith("/")) {
                commandHandler.commandProcessing(update, chatId, messageText);
            }
        } else if (update.hasCallbackQuery()) {//Если нажата кнопка.

            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callBackData.equals(YES_BUTTON)) {
                newMessageFlag.put(chatId,true);
                String text = "Отлично! Создайте Ваше новое напоминание, как на образце ниже:\n\n " +
                        "01.01.2023 12:00 С Новым годом меня любимого!";
                executeEditMessageText(text, chatId, messageId);
            } else if (callBackData.equals(NO_BUTTON) || callBackData.equals(CANCEL_BUTTON)) {
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

    /**
     *Обработка запроса на удаление напоминания.
     *
     * @param chatId  id текущего чата
     */
    public void delete(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите напоминание, которое необходимо удалить:");
        List<NotificationTask> reminders = notificationTaskRepository.findAllByUserId(chatId);

        //Создание кнопок
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (NotificationTask reminder : reminders) {
            var button = new InlineKeyboardButton();
            var dateTime = reminder.getReminderTime().format(DateTimeFormatter.ofPattern(FORMATTER));
            button.setText(dateTime + ": " + reminder.getReminderText());
            button.setCallbackData("delete_" + reminder.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        var button = new InlineKeyboardButton();
        button.setText("Отмена");
        button.setCallbackData(CANCEL_BUTTON);
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        executeMessage(message);

    }

    /**
     *Вывод всех напоминаний пользователя.
     *
     * @param chatId  id текущего чата
     */
    public void showMyReminders(long chatId) {

        var reminders = notificationTaskRepository.findAllByUserId(chatId);
        StringBuilder listOfReminders = new StringBuilder();
        for (NotificationTask reminder: reminders){
            var dateTime = reminder.getReminderTime().format(DateTimeFormatter.ofPattern(FORMATTER));
            listOfReminders.append(dateTime).append(": ").append(reminder.getReminderText()).append("\n");
        }
        sendMessage(chatId, listOfReminders.toString());
        log.info("all reminders have been issued at the user's request");

    }

    /**
     *Обработка запроса на создание нового напоминания.
     *
     * @param chatId  id текущего чата
     */
    public void createNewReminder(long chatId) {

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

    /**
     *Ррегистрирует нового пользователя в БД.
     *
     * @param msg  объект сообщения
     */
    public void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("user saved: " + user);

        }
    }

    /**
     *Обработка команды /start.
     *
     * @param chatId  id текущего чата
     * @param firstName  имя пользователя
     */
    public void startCommandReceived(long chatId, String firstName) throws TelegramApiException {

        String answer = EmojiParser.parseToUnicode("Привет, " + firstName + ":blush:" + "! Пожалуйста выберите желаемое действие из меню ниже.");
        sendStartMessage(chatId, answer);
        log.info("Replaed to user " + firstName + " Id: " + chatId);

    }

    /**
     *Подготовка сообщения пользователю.
     *
     * @param chatId  id текущего чата
     * @param textToSend  текст сообщения
     */
    public void sendMessage(long chatId, String textToSend){

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);

    }

    /**
     *Отправка приветственного сообщения пользователю и формирование нижнего меню.
     *
     * @param chatId  id текущего чата
     * @param textToSend  текст сообщения
     */
    public void sendStartMessage(long chatId, String textToSend) throws TelegramApiException{

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        //Добавление клавиатуры к собщению.
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
//        row.add(HELP_COMAND);//Добавление кнопки
//        row.add(CREATE_COMAND);
//        keyboardRows.add(row);//Добавление строки кнопок
//        row = new KeyboardRow();
//        row.add(SHOW_COMAND);
//        row.add(DELETE_COMAND);
//        keyboardRows.add(row);
//        keyboardMarkup.setKeyboard(keyboardRows);//Формирование клавиатуры
//        message.setReplyMarkup(keyboardMarkup);//Добавление клавиатуры
        //----------------------------------------

        executeMessage(message);
    }


    /**
     *Отправка измененного сообщения пользователю.
     *
     * @param text  текст сообщения
     * @param chatId  id текущего чата
     * @param messageId  id изменяемого сообщения
     */
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

    /**
     *Отправка сообщения пользователю.
     *
     * @param message  объект сообщения
     */
    private void executeMessage(SendMessage message){

        try{
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }

    }
}
