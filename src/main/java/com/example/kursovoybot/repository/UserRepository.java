package com.example.kursovoybot.repository;

import com.example.kursovoybot.model.NotificationTask;
import com.example.kursovoybot.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query(value = "SELECT id, reminder_text, reminder_time, user_id from users_data_table " +
            "join notification_task nt on users_data_table.chat_id = nt.user_id  where chat_id = ?1 " +
            "order by reminder_time", nativeQuery = true)
    public List<NotificationTask> findAllUsersReminders(long chatId);

}
