package com.example.kursovoybot.handrer.command;

import com.example.kursovoybot.bot.TelegramBot;
import com.example.kursovoybot.handrer.callback.delete.DeleteReminder;
import com.example.kursovoybot.handrer.callback.setutc.SetUtc;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.service.SendingMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.kursovoybot.handrer.callback.delete.DeleteReminder.CANCEL_BUTTON;

@Component
@Slf4j
public class ButtonHandler {

    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;
    private final SetUtc setUtc;
    private final DeleteReminder deleteReminder;

    public ButtonHandler(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot, SendingMessages sendingMessages, SetUtc setUtc, DeleteReminder deleteReminder) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
        this.setUtc = setUtc;
        this.deleteReminder = deleteReminder;
    }

    public void processingOfButtons(Update update){
        String callBackData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (callBackData.contains("delete")) {
            deleteReminder.delete(callBackData, chatId, messageId);
        } else if (callBackData.equals(CANCEL_BUTTON)) {
            deleteReminder.cancelDelete(callBackData, chatId, messageId);
        }else if (callBackData.equals("setUtcAll")) {
            setUtc.getAll(chatId);
        }
    }
}
