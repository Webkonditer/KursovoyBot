package com.example.kursovoybot.handrer.callback.about;

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
public class AboutBot {

    private final SendingMessages sendingMessages;

    private static final String ABOUT_MESSAGE = "Бот создан в 2022 году командой разработчиков студии VilasSoftware." +
            "\n\nЗаказать подобный бот или любой другой можно здесь: @AlexanderVilas";

    public AboutBot(SendingMessages sendingMessages) {
        this.sendingMessages = sendingMessages;
    }

    public void aboutCallBack(long chatId) {
        sendingMessages.sendMessageWithMenu(chatId, ABOUT_MESSAGE);
    }
}
