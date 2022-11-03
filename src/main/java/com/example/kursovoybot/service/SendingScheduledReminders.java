package com.example.kursovoybot.service;

import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class SendingScheduledReminders {

    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    public SendingScheduledReminders(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    /**
     * Отправляет напоминания по расписанию.
     *
     */
    @Scheduled(cron = "${cron.scheduler}")
    private void sendReminders() throws TelegramApiException {

        var reminders = notificationTaskRepository.findTheCurrentOnesForTheMoment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        for (NotificationTask reminder: reminders){
            telegramBot.sendMessage(reminder.getUser().getChatId(), reminder.getReminderText());
            notificationTaskRepository.deleteById(reminder.getId());
        }
        log.info("scheduled messages sent");

    }
}
