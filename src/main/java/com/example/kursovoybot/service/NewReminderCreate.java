package com.example.kursovoybot.service;

import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.model.User;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class NewReminderCreate {

    private final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private final TelegramBot telegramBot;

    private final UserRepository userRepository;

    private final NotificationTaskRepository notificationTaskRepository;

    public NewReminderCreate(TelegramBot telegramBot, UserRepository userRepository, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    /**
     * Создание нового напоминания.
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
            var textToSend = EmojiParser.parseToUnicode("Неправильный формат напоминания " + ":confused:" + " Попробуйте еще раз, точно, как в образце!");
            telegramBot.sendMessage(chatId, textToSend);
            throw new RuntimeException("the user entered an incorrect reminder");
        }
        LocalDateTime dateTime = parseDateTime(chatId, date);
        User user = userRepository.findById(chatId).orElseThrow();
        if(dateTime.isBefore(LocalDateTime.now())){
            var textToSend = EmojiParser.parseToUnicode("Дата и время ранее текущего " + ":confused:" + " Попробуйте еще раз!");
            telegramBot.sendMessage(user.getChatId(), textToSend);
            log.info("the user entered a date earlier than the current one");
        } else {
            registerNewReminder(user, reminderText, dateTime);
            var textToSend = EmojiParser.parseToUnicode("Ваше напоминание успешно сохранено " + ":ok_hand:");
            telegramBot.sendMessage(user.getChatId(), textToSend);
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
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(telegramBot.getFORMATTER()));
        } catch (DateTimeParseException e){
            var textToSend = EmojiParser.parseToUnicode("Неправильный формат даты и времени " + ":confused:" + " Попробуйте еще раз!");
            telegramBot.sendMessage(chatId, textToSend);
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
