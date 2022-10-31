package com.example.kursovoybot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "notification_task")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String reminderText;

    private LocalDateTime reminderTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
