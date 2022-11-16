package com.example.kursovoybot.handrer.callback.unsubscribe;

import com.example.kursovoybot.handrer.callback.setutc.TimeZone;
import com.example.kursovoybot.handrer.callback.show.ShowAllReminders;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.service.SendingMessages;
import com.example.kursovoybot.service.UserManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UnsubscribeUser {

    private final SendingMessages sendingMessages;
    private final UserManagement userManagement;
    private static final String EXACTLY_UNSUBSCRIBE = "\uD83D\uDE41️ Ты точно хочешь со мной расстаться?";
    private static final String UNSUBSCRIBE_YES_TEXT = "Да";
    private static final String UNSUBSCRIBE_NO_TEXT = "Нет";
    private static final String NOT_UNSUBSCRIBE = "\uD83D\uDE44 Уфф! Я так испугался!";
    private static final String UNSUBSCRIBE_END = "\uD83D\uDE44 Мне будет тебя не хватать! Возвращайся, когда сможешь!";

    public UnsubscribeUser(SendingMessages sendingMessages, NotificationTaskRepository notificationTaskRepository, ShowAllReminders showAllReminders, UserManagement userManagement) {
        this.sendingMessages = sendingMessages;
        this.userManagement = userManagement;
    }

    /**
     *Обработка запроса на удаление пользователя.
     *
     * @param chatId  id текущего чата
     */
    public void preparingUnsubscribe(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String text = EXACTLY_UNSUBSCRIBE;
        message.setText(text);

        //Создание кнопок
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        var button = new InlineKeyboardButton();
        button.setText(UNSUBSCRIBE_YES_TEXT);
        button.setCallbackData("unsubscribeYes");
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(button);
        button = new InlineKeyboardButton();
        button.setText(UNSUBSCRIBE_NO_TEXT);
        button.setCallbackData("unsubscribeNo");
        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        sendingMessages.executeMessage(message);
    }

    public void doNotUnsubscribe(long chatId, long messageId) {
        sendingMessages.executeEditMessageText(EXACTLY_UNSUBSCRIBE, chatId, messageId, null);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String text = NOT_UNSUBSCRIBE;
        message.setText(text);
        sendingMessages.executeMessage(message);
    }

    public void Unsubscribe(long chatId, long messageId) {
        sendingMessages.executeEditMessageText(EXACTLY_UNSUBSCRIBE, chatId, messageId, null);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String text = UNSUBSCRIBE_END;
        message.setText(text);
        sendingMessages.executeMessage(message);
    }
//        if(messageId == -1) {
//            var button = new InlineKeyboardButton();
//            button.setText(GET_ALL_TIME_ZONE);
//            button.setCallbackData("setUtcAll");
//            List<InlineKeyboardButton> rowInline = new ArrayList<>();
//            rowInline.add(button);
//            rowsInline.add(rowInline);
//            markupInline.setKeyboard(rowsInline);
//            message.setReplyMarkup(markupInline);
//            sendingMessages.executeMessage(message);
//        } else{
//            markupInline.setKeyboard(rowsInline);
//            sendingMessages.executeEditMessageText(text, chatId, messageId, markupInline);
//        }
//
//    }
//
//    public void sendReply(long chatId, long messageId) {
//        int userUtc = userManagement.getUserUtc(chatId);
//        Optional<TimeZone> timeZone = TimeZone.parseTimeZone(userUtc);
//        String text = TIME_ZONE_NOW + "\n" +
//                      timeZone.get().getName() + " " + timeZone.get().getDesc() + "\n";
//        sendingMessages.executeEditMessageText(text, chatId, messageId, null);
//    }


}
