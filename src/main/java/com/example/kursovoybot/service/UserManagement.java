package com.example.kursovoybot.service;

import com.example.kursovoybot.model.User;
import com.example.kursovoybot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;

@Component
@Slf4j
public class UserManagement {

        private final UserRepository userRepository;

    public UserManagement(UserRepository userRepository) {
        this.userRepository = userRepository;
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

}
