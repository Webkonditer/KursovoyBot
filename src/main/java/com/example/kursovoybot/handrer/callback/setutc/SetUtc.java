package com.example.kursovoybot.handrer.callback.setutc;

import com.example.kursovoybot.handrer.callback.show.ShowAllReminders;
import com.example.kursovoybot.handrer.command.Command;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.service.SendingMessages;
import com.example.kursovoybot.service.UserManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SetUtc {

    private final SendingMessages sendingMessages;
    private final NotificationTaskRepository notificationTaskRepository;
    private final UserManagement userManagement;
    private static final String CURRENT_TIME_ZONE_START = "\uD83D\uDD52️ Установленный часовой пояс:";
    private static final String CURRENT_TIME_ZONE_END = "Выбери свой ниже:";
    private static final String GET_ALL_TIME_ZONE = "Больше";
    private static final String TIME_ZONE_NOW = "Установлен часовой пояс:";

    public SetUtc(SendingMessages sendingMessages, NotificationTaskRepository notificationTaskRepository, ShowAllReminders showAllReminders, UserManagement userManagement) {
        this.sendingMessages = sendingMessages;
        this.notificationTaskRepository = notificationTaskRepository;
        this.userManagement = userManagement;
    }

    /**
     *Обработка запроса на изменение часового пояса.
     *
     * @param chatId  id текущего чата
     */
    public void preparingForSetUtc(long chatId) {
        SetUtcCallBack(chatId, 7, -1);
    }

    public void getAll(long chatId, long messageId) {
        SetUtcCallBack(chatId, 25, messageId);
    }

    public void SetUtcCallBack(long chatId, int numberOfButtons, long messageId) {

        int userUtc = userManagement.getUserUtc(chatId);
        Optional<TimeZone> timeZone = TimeZone.parseTimeZone(userUtc);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String text = CURRENT_TIME_ZONE_START + "\n" +
                        timeZone.get().getName() + " " + timeZone.get().getDesc() + "\n" +
                        CURRENT_TIME_ZONE_END;
        message.setText(text);

        //Создание кнопок
        List<TimeZone> timeZones = Arrays.stream(TimeZone.values()).limit(numberOfButtons).toList();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (TimeZone tZone : timeZones) {
            var button = new InlineKeyboardButton();
            button.setText(tZone.getName() + " " + tZone.getDesc());
            button.setCallbackData("setUtc_" + tZone.getUtc());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        if(messageId == -1) {
            var button = new InlineKeyboardButton();
            button.setText(GET_ALL_TIME_ZONE);
            button.setCallbackData("setUtcAll");
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            message.setReplyMarkup(markupInline);
            sendingMessages.executeMessage(message);
        } else{
            markupInline.setKeyboard(rowsInline);
            sendingMessages.executeEditMessageText(text, chatId, messageId, markupInline);
        }

    }

    public void sendReply(long chatId, long messageId) {
        int userUtc = userManagement.getUserUtc(chatId);
        Optional<TimeZone> timeZone = TimeZone.parseTimeZone(userUtc);
        String text = TIME_ZONE_NOW + "\n" +
                      timeZone.get().getName() + " " + timeZone.get().getDesc() + "\n";
        sendingMessages.executeEditMessageText(text, chatId, messageId, null);
    }
}
