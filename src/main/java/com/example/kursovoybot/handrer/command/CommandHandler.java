package com.example.kursovoybot.handrer.command;

import com.example.kursovoybot.handrer.callback.help.Help;
import com.example.kursovoybot.handrer.callback.UnknownCommand;
import com.example.kursovoybot.handrer.callback.start.Start;
import com.example.kursovoybot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@Slf4j
public class CommandHandler {

    private final TelegramBot telegramBot;
    private final Help help;
    private final UnknownCommand unknownCommand;
    private final Start start;

    public CommandHandler(TelegramBot telegramBot, Help help, UnknownCommand unknownCommand, Start start) {
        this.telegramBot = telegramBot;
        this.help = help;
        this.unknownCommand = unknownCommand;
        this.start = start;
    }

    /**
     *Обработка входящих команд.
     *
     * @param update  объект запроса
     * @param chatId  id чата
     * @param messageText  текст запроса
     */
    public void commandProcessing(Update update, long chatId, String messageText){
        Optional<Command> command = Command.parseCommand(messageText);
        switch (command.get()) {
            case START_COMAND:
                start.startCallBack(chatId, update);
                break;

            case HELP_COMAND:
                help.helpCallBack(chatId);
                break;

            case CREATE_COMAND:
                telegramBot.createNewReminder(chatId);
                break;

            case SHOW_COMAND:
                telegramBot.showMyReminders(chatId);
                break;

            case DELETE_COMAND:
                telegramBot.delete(chatId);
                break;

            //Если введена неизвестная команда
            default:
                unknownCommand.unknownCommandCallBack(chatId);
        }
    }

}
