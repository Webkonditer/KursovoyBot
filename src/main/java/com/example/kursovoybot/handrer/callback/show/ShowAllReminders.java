package com.example.kursovoybot.handrer.callback.show;

import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.service.SendingMessages;
import com.example.kursovoybot.service.UserManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class ShowAllReminders {

    private final String FORMATTER = "dd.MM.yyyy HH:mm";
    private final UserManagement userManagement;
    private final SendingMessages sendingMessages;
    private final NotificationTaskRepository notificationTaskRepository;

    public ShowAllReminders(UserManagement userManagement, SendingMessages sendingMessages, NotificationTaskRepository notificationTaskRepository) {
        this.userManagement = userManagement;
        this.sendingMessages = sendingMessages;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    /**
     *Вывод всех напоминаний пользователя.
     *
     * @param chatId  id текущего чата
     */
    public void showMyReminders(long chatId) {

        var reminders = notificationTaskRepository.findAllByUserId(chatId);
        StringBuilder listOfReminders = new StringBuilder();
        for (NotificationTask reminder: reminders){
            int userUtc = reminder.getUser().getUserUtc();
            var dateTime =
                reminder.getReminderTime().minusHours(3).plusHours(userUtc).format(DateTimeFormatter.ofPattern(FORMATTER));
            listOfReminders.append(dateTime).append(": ").append(reminder.getReminderText()).append("\n");
        }
        sendingMessages.sendMessageWithMenu(chatId, listOfReminders.toString());
        log.info("all reminders have been issued at the user's " + chatId + " request");

    }

    public String getFORMATTER() {
        return FORMATTER;
    }
}


