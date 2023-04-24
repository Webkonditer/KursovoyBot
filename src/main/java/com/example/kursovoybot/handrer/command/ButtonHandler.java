package com.example.kursovoybot.handrer.command;

import com.example.kursovoybot.bot.TelegramBot;
import com.example.kursovoybot.handrer.callback.delete.DeleteReminder;
import com.example.kursovoybot.handrer.callback.setutc.SetUtc;
import com.example.kursovoybot.handrer.callback.unsubscribe.UnsubscribeUser;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.service.SendingMessages;
import com.example.kursovoybot.service.UserManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.kursovoybot.handrer.callback.delete.DeleteReminder.CANCEL_BUTTON;

@Component
@Slf4j
public class ButtonHandler {

    private final UserManagement userManagement;
    private final SetUtc setUtc;
    private final DeleteReminder deleteReminder;
    private final UnsubscribeUser unsubscribeUser;

    public ButtonHandler(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot, SendingMessages sendingMessages, UserManagement userManagement, SetUtc setUtc, DeleteReminder deleteReminder, UnsubscribeUser unsubscribeUser) {
        this.userManagement = userManagement;
        this.setUtc = setUtc;
        this.deleteReminder = deleteReminder;
        this.unsubscribeUser = unsubscribeUser;
    }

    public void processingOfButtons(Update update){
        String callBackData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (callBackData.contains("delete")) {
            deleteReminder.delete(callBackData, chatId, messageId);
        } else if (callBackData.equals(CANCEL_BUTTON)) {
            deleteReminder.cancelDelete(chatId, messageId);
        }else if (callBackData.equals("setUtcAll")) {
            setUtc.getAll(chatId, messageId);
        }else if (callBackData.startsWith("setUtc_")) {
            userManagement.setUtc(chatId, callBackData);
            setUtc.sendReply(chatId, messageId);
        }else if (callBackData.equals("unsubscribeNo")) {
            unsubscribeUser.doNotUnsubscribe(chatId, messageId);
        }else if (callBackData.equals("unsubscribeYes")) {
            userManagement.deleteUser(chatId);
            unsubscribeUser.Unsubscribe(chatId, messageId);
        }
    }
}
