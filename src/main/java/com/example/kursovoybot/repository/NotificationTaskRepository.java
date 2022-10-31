package com.example.kursovoybot.repository;

import com.example.kursovoybot.model.NotificationTask;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationTaskRepository extends CrudRepository<NotificationTask, Long> {

    @Query(value = "SELECT * FROM notification_task  ORDER BY reminder_time", nativeQuery = true)
    public List<NotificationTask> findAllOrderByReminderTime();

    @Query(value = "SELECT * FROM notification_task WHERE user_id = ?1 ORDER BY reminder_time", nativeQuery = true)
    public List<NotificationTask> findAllByUserId(long chatId);
}
