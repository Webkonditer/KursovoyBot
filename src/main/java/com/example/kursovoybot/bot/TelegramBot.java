package com.example.kursovoybot.bot;

import com.example.kursovoybot.handrer.callback.UnknownCommand;
import com.example.kursovoybot.handrer.callback.show.ShowAllReminders;
import com.example.kursovoybot.handrer.command.ButtonHandler;
import com.example.kursovoybot.handrer.command.Command;
import com.example.kursovoybot.handrer.command.CommandHandler;
import com.example.kursovoybot.repository.NotificationTaskRepository;
import com.example.kursovoybot.repository.UserRepository;
import com.example.kursovoybot.service.CheckingAndSavingNewReminder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final CommandHandler commandHandler;
    private final CheckingAndSavingNewReminder newReminderCreate;
    private final ButtonHandler buttonHandler;
    private final UnknownCommand unknownCommand;
    private final BotConfig config;

    private final Map<Long,Boolean> newMessageFlag = new HashMap<>(); //Флаг добавления нового сообщения

    public TelegramBot(BotConfig config,
                       @Lazy CheckingAndSavingNewReminder newReminderCreate,
                       @Lazy CommandHandler commandHandler,
                       @Lazy ButtonHandler buttonHandler,
                       @Lazy UnknownCommand unknownCommand)
    {
        this.commandHandler = commandHandler;
        this.config = config;
        this.newReminderCreate = newReminderCreate;
        this.buttonHandler = buttonHandler;
        this.unknownCommand = unknownCommand;
        setupTextMenu();
    }

    /**
     *Создание текстового меню.
     *
     */
    private void setupTextMenu() {
        try {
            List<BotCommand> commands = Arrays.stream(Command.values())
                            .map(c -> BotCommand.builder().command(c.getName()).description(c.getDesc()).build())
                            .collect(Collectors.toList());
            execute(SetMyCommands.builder().commands(commands).build());
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     *Обработка поступающих запросов.
     *
     * @param update  объект запроса
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {                 //Если сообщение содержит текст
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (newMessageFlag.get(chatId) != null && newMessageFlag.get(chatId)) { //Если вводится новое напоминание
                newMessageFlag.put(chatId, false);
                newReminderCreate.createNewReminder(chatId, messageText);
            } else {                                                                //Основные команды
                commandHandler.commandProcessing(update, chatId, messageText);
            }
        } else if (update.hasCallbackQuery()) {                                     //Если нажата кнопка.
            buttonHandler.processingOfButtons(update);
        } else{
            unknownCommand.unknownCommandCallBack(update.getMessage().getChatId());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    public Map<Long, Boolean> getNewMessageFlag() {return newMessageFlag;}

}
