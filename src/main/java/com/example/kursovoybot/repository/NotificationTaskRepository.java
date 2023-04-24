package com.example.kursovoybot.repository;

import com.example.kursovoybot.model.NotificationTask;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends CrudRepository<NotificationTask, Long> {

    @Query(value = "SELECT * FROM notification_task  ORDER BY reminder_time", nativeQuery = true)
    List<NotificationTask> findAllOrderByReminderTime();

    @Query(value = "SELECT * FROM notification_task WHERE user_id = ?1 ORDER BY reminder_time", nativeQuery = true)
    List<NotificationTask> findAllByUserId(long chatId);

    @Query(value = "SELECT * FROM notification_task WHERE reminder_time = ?1 ", nativeQuery = true)
    List<NotificationTask> findTheCurrentOnesForTheMoment(LocalDateTime dateTime);

    @Query(value = "DELETE FROM notification_task WHERE user_id = ?1", nativeQuery = true)
    void deleteAllByUserId(long chatId);
}
