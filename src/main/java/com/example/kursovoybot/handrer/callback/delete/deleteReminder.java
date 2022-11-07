package com.example.kursovoybot.handrer.callback.delete;

import com.example.kursovoybot.service.SendingMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class deleteReminder {

    private final SendingMessages sendingMessages;

    private static final String NEW_Reminder_TEXT = "Создать новое напоминание?";

    public deleteReminder(SendingMessages sendingMessages) {
        this.sendingMessages = sendingMessages;
    }

    /**
     *Обработка запроса на создание нового напоминания.
     *
     * @param chatId  id текущего чата
     */
    public void createNewReminder(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(NEW_Reminder_TEXT);

        //Добавление кнопок Да Нет
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText(NewReminderButton.YES_BUTTON.getLabel());
        yesButton.setCallbackData(NewReminderButton.YES_BUTTON.getData());
        var noButton = new InlineKeyboardButton();
        noButton.setText(NewReminderButton.NO_BUTTON.getLabel());
        noButton.setCallbackData(NewReminderButton.NO_BUTTON.getData());
        rowInline.add(yesButton);
        rowInline.add(noButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        sendingMessages.executeMessage(message);
    }

}
