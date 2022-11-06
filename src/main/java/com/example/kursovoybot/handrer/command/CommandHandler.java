package com.example.kursovoybot.handrer.command;

import com.example.kursovoybot.handrer.callback.help.Help;
import com.example.kursovoybot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
@Slf4j
public class CommandHandler {

    private final TelegramBot telegramBot;

    private final Help help;

    public CommandHandler(TelegramBot telegramBot, Help help) {
        this.telegramBot = telegramBot;
        this.help = help;
    }

    public void commandProcessing(Update update, long chatId, String messageText){
        Optional<Command> comand = Command.parseCommand(messageText);
        //Обрабатываем входящие команды
                    switch (comand.get()) {
            case START_COMAND:
                try {
                    telegramBot.registerUser(update.getMessage());
                    telegramBot.startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
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
                telegramBot.sendMessage(chatId, "К сожалению Ваша команда не распознана. Пожалуйста выберите команду из меню.");
                log.info("the user entered an unknown command");
        }
    }

}
