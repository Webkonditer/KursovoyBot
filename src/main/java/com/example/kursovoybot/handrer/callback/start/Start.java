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

    private static final String GREETING = "\uD83D\uDE00 Привет ";

    private static final String WELCOME_MESSAGE = "\n\nЯ - твоя вторая память. Можешь смело доверить мне напоминать тебе о важных событиях. \n\n" +
            "Если твой часовой пояс отличается от Московского, нажми сюда /set_utc \n\n" +
            "Теперь давай создадим твое первое напоминание. \nНажми кнопку \"Создать новое\" в меню ниже.";

    private final UserManagement userManagement;
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

    /**
     *Отправка приветственного сообщения и выбор часового пояса.
     *
     * @param chatId  id текущего чата
     * @param firstName  имя пользователя
     */
    public void startCommandReceived(long chatId, String firstName) {

        String message = GREETING + firstName + "! " + WELCOME_MESSAGE;
        sendingMessages.sendMessageWithMenu(chatId, message);
        log.info("A welcome message has been sent to the user " + firstName + ", Id: " + chatId);

    }

}


