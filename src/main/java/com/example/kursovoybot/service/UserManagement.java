package com.example.kursovoybot.service;

import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.model.User;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class UserManagement {

        private final UserRepository userRepository;
        private final NotificationTaskRepository notificationTaskRepository;

    public UserManagement(UserRepository userRepository, NotificationTaskRepository notificationTaskRepository) {
        this.userRepository = userRepository;
        this.notificationTaskRepository = notificationTaskRepository;
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
            user.setUserUtc(3);
            user.setRegisteredAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("user saved: " + user);

        }
    }

    /**
     *Получение часового пояса пользователя.
     *
     * @param chatId  объект сообщения
     */
    public Integer getUserUtc(long chatId) {
        User user = userRepository.findById(chatId).orElseThrow();
        return user.getUserUtc();
    }

    /**
     *Изменение часового пояса пользователя.
     *Смена времени у всех напоминаний под новый часовой пояс
     *
     * @param chatId  объект сообщения
     * @param callBackData  строка данных запроса
     */
    public void setUtc(long chatId, String callBackData) {
        User user = userRepository.findById(chatId).orElseThrow();
        int oldUserUtc = user.getUserUtc();
        int newUserUtc = Integer.parseInt(callBackData.substring(7));
        user.setUserUtc(newUserUtc);
        userRepository.save(user);
        log.info("The user " + chatId + " had UTC" + newUserUtc + " set.");
        var reminders = notificationTaskRepository.findAllByUserId(chatId);
        for (NotificationTask reminder: reminders){
            LocalDateTime reminderTime = reminder.getReminderTime();
            LocalDateTime newReminderTime = reminderTime.plusHours(oldUserUtc).minusHours(newUserUtc);
            reminder.setReminderTime(newReminderTime);
            notificationTaskRepository.save(reminder);
        }
        log.info("The dates of all user " + chatId + " reminders have been changed.");
    }

    /**
     Уудаление пользователя и всех его напоминаний.
     *
     * @param chatId  объект сообщения
     */
    public void deleteUser(long chatId) {
        userRepository.deleteById(chatId);
        log.info("User " + chatId + " and all his reminders have been deleted.");
    }
}
