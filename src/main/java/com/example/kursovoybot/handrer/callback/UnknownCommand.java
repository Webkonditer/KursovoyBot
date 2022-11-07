package com.example.kursovoybot.handrer.callback;

import com.example.kursovoybot.service.SendingMessages;
import com.example.kursovoybot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UnknownCommand {

    private final SendingMessages sendingMessages;

    private static final String ERRONEOUS_COMMAND = "К сожалению Ваша команда не распознана. Пожалуйста выберите команду из меню.";

    public UnknownCommand(SendingMessages sendingMessages) {
        this.sendingMessages = sendingMessages;
    }

    public void unknownCommandCallBack(long chatId){
        sendingMessages.sendMessage(chatId, ERRONEOUS_COMMAND);
        log.info("User " + chatId + " entered an unknown command");
    }

}
