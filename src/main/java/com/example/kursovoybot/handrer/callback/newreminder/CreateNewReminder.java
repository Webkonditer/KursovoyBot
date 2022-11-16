package com.example.kursovoybot.handrer.callback.newreminder;

import com.example.kursovoybot.service.SendingMessages;
import com.example.kursovoybot.bot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateNewReminder {

    private static final String INVITING_MESSAGE = "\uD83D\uDC4D Отлично! \nДавай создадим новое напоминание, как на образце ниже:\n\n " +
            "01.01.2023 12:00 С Новым годом меня любимого!";
    private final TelegramBot telegramBot;
    private SendingMessages sendingMessages;

    public CreateNewReminder(TelegramBot telegramBot, SendingMessages sendingMessages) {
        this.telegramBot = telegramBot;
        this.sendingMessages = sendingMessages;
    }

    public void preparingToCreateNewReminder(long chatId){
        telegramBot.getNewMessageFlag().put(chatId,true);
        sendingMessages.sendMessage(chatId, INVITING_MESSAGE);
    }
}
