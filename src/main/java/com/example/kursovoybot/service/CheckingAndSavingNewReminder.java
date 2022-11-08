package com.example.kursovoybot.service;

import com.example.kursovoybot.bot.TelegramBot;
import com.example.kursovoybot.handrer.callback.show.ShowAllReminders;
import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.model.User;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CheckingAndSavingNewReminder {

    private final static Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([0-9\\W\\,\\.\\:+]+)");
    private final static String INCORRECT_FORMAT = "\uD83D\uDE32 Неправильный формат напоминания. \nНажми снова \"Создать новое\". Попробуй еще раз, точно, как в образце: дата время напоминание.";
    private final static String DATE_EARLIER_THAN_CURRENT = "\uD83D\uDE32 Истекшие дата и время. \nНажми снова \"Создать новое\". \nЗапиши напоминание с корректной датой.";
    private final static String INCORRECT_DATE_AND_TIME_FORMAT = "\uD83D\uDE32 Неправильный формат даты и времени. \nПопробуй еще раз! \nНажми снова \"Создать новое\". \nЗапиши напоминание с корректной датой и временем.";
    private final static String SUCCESSFULLY_SAVED = "\uD83D\uDC4C Супер! \nНапоминание успешно сохранено.";

    private final TelegramBot telegramBot;

    private final UserRepository userRepository;
    private final ShowAllReminders showAllReminders;

    private final NotificationTaskRepository notificationTaskRepository;

    private final SendingMessages sendingMessages;

    public CheckingAndSavingNewReminder(TelegramBot telegramBot, UserRepository userRepository, ShowAllReminders showAllReminders, NotificationTaskRepository notificationTaskRepository, SendingMessages sendingMessages) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
        this.showAllReminders = showAllReminders;
        this.notificationTaskRepository = notificationTaskRepository;
        this.sendingMessages = sendingMessages;
    }

    /**
     * Проверяет и создает новое напоминания.
     *
     * @param chatId  id текущего чата
     * @param messageText  текст напоминания
     */
    public void createNewReminder(long chatId, String messageText) {

        Matcher matcher = PATTERN.matcher(messageText);
        String date;
        String reminderText;
        if (matcher.matches()) {
            date = matcher.group(1);
            reminderText = matcher.group(3);
        } else{
            sendingMessages.sendMessageWithMenu(chatId, INCORRECT_FORMAT);
            throw new RuntimeException("the user entered an incorrect reminder");
        }
        LocalDateTime dateTime = parseDateTime(chatId, date);
        User user = userRepository.findById(chatId).orElseThrow();
        if(dateTime.isBefore(LocalDateTime.now())){
            sendingMessages.sendMessage(user.getChatId(), DATE_EARLIER_THAN_CURRENT);
            log.info("the user entered a date earlier than the current one");
        } else {
            registerNewReminder(user, reminderText, dateTime);
            sendingMessages.sendMessage(user.getChatId(), SUCCESSFULLY_SAVED);
        }

    }

    /**
     * Парсит объект LocalDateTime из строки.
     *
     * @param chatId  id текущего чата
     * @param date  строка с датой и временем напоминания
     */
    private LocalDateTime parseDateTime(long chatId, String date){

        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(showAllReminders.getFORMATTER()));
        } catch (DateTimeParseException e){
            sendingMessages.sendMessage(chatId, INCORRECT_DATE_AND_TIME_FORMAT);
            throw new RuntimeException("the user entered an incorrect date");
        }

    }

    /**
     * Сохраняет в БД новое напоминание.
     *
     * @param user  объект пользователя, создающего напоминание
     * @param reminderText  текст напоминания
     * @param dateTime  дата и время сохраняемого напоминания
     */
    private void registerNewReminder(User user, String reminderText, LocalDateTime dateTime) {

        NotificationTask reminder = new NotificationTask();
        reminder.setUser(user);
        reminder.setReminderText(reminderText);
        reminder.setReminderTime(dateTime);
        notificationTaskRepository.save(reminder);
        log.info("reminder saved: " + reminder);

    }
}
