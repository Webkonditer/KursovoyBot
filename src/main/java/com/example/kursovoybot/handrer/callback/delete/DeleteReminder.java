package com.example.kursovoybot.handrer.callback.delete;

import com.example.kursovoybot.handrer.callback.show.ShowAllReminders;
import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.service.SendingMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DeleteReminder {

    private final SendingMessages sendingMessages;
    private final NotificationTaskRepository notificationTaskRepository;
    private final ShowAllReminders showAllReminders;
    private static final String DELETE_REQUEST = "☠️ Какое напоминание удалим?";
    private static final String CANCEL_TEXT = "Отмена";
    public static final String CANCEL_BUTTON = "cancel";
    public static final String DELETE_REPLAY = "\uD83E\uDEE3 Все. Удалил. Насовсем!";
    private static final String DELETE_CANCEL_REQUEST = "\uD83E\uDEE4 Пусть еще поживут!";

    public DeleteReminder(SendingMessages sendingMessages, NotificationTaskRepository notificationTaskRepository, ShowAllReminders showAllReminders) {
        this.sendingMessages = sendingMessages;
        this.notificationTaskRepository = notificationTaskRepository;
        this.showAllReminders = showAllReminders;
    }

    /**
     *Обработка запроса на удаление напоминания.
     *
     * @param chatId  id текущего чата
     */
    public void preparingForDeletion(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(DELETE_REQUEST);
        List<NotificationTask> reminders = notificationTaskRepository.findAllByUserId(chatId);

        //Создание кнопок
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (NotificationTask reminder : reminders) {
            var button = new InlineKeyboardButton();
            var dateTime = reminder.getReminderTime().format(DateTimeFormatter.ofPattern(showAllReminders.getFORMATTER()));
            button.setText(dateTime + ": " + reminder.getReminderText());
            button.setCallbackData("delete_" + reminder.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }
        var button = new InlineKeyboardButton();
        button.setText(CANCEL_TEXT);
        button.setCallbackData(CANCEL_BUTTON);
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        sendingMessages.executeMessage(message);

    }

    public void delete(String callBackData, long chatId, long messageId) {
        notificationTaskRepository.deleteById(Long.parseLong(callBackData.substring(7)));
        sendingMessages.executeEditMessageText(DELETE_REQUEST, chatId, messageId);
        sendingMessages.sendMessage(chatId, DELETE_REPLAY);
        log.info("The reminder " + messageId + " was removed at the request of the user " + chatId);
    }

    public void cancelDelete(String callBackData, long chatId, long messageId) {
        String text = "Хорошо! Выберите другое действие из меню.";
        sendingMessages.executeEditMessageText(DELETE_REQUEST, chatId, messageId);
        sendingMessages.sendMessage(chatId, DELETE_CANCEL_REQUEST);
    }
}
