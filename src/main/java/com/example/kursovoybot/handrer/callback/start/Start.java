package com.example.kursovoybot.handrer.callback.start;

import com.example.kursovoybot.service.SendingMessages;
import com.example.kursovoybot.service.UserManagement;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class Start {

    private UserManagement userManagement;
    private final SendingMessages sendingMessages;

    public Start(UserManagement userManagement, SendingMessages sendingMessages) {
        this.userManagement = userManagement;
        this.sendingMessages = sendingMessages;
    }

    /**
     *Обработка команды /start.
     *
     * @param chatId  id текущего чата
     * @param update  объект сообщения
     */
    public void startCallBack(long chatId, Update update){

            userManagement.registerUser(update.getMessage());
            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());

    }

    public void startCommandReceived(long chatId, String firstName) {

        String answer = EmojiParser.parseToUnicode("Привет, " + firstName + ":blush:" + "! Пожалуйста выберите желаемое действие из меню ниже.");
        sendingMessages.sendStartMessage(chatId, answer);
        log.info("A welcome message has been sent to the user " + firstName + ", Id: " + chatId);

    }

}


