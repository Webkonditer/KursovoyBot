package com.example.kursovoybot.handrer.command;

import com.example.kursovoybot.handrer.callback.about.AboutBot;
import com.example.kursovoybot.handrer.callback.help.Help;
import com.example.kursovoybot.handrer.callback.UnknownCommand;
import com.example.kursovoybot.handrer.callback.delete.DeleteReminder;
import com.example.kursovoybot.handrer.callback.newreminder.CreateNewReminder;
import com.example.kursovoybot.handrer.callback.setutc.SetUtc;
import com.example.kursovoybot.handrer.callback.show.ShowAllReminders;
import com.example.kursovoybot.handrer.callback.start.Start;
import com.example.kursovoybot.bot.TelegramBot;
import com.example.kursovoybot.handrer.callback.unsubscribe.UnsubscribeUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@Slf4j
public class CommandHandler {

    private final TelegramBot telegramBot;
    private final DeleteReminder deleteReminder;
    private final CreateNewReminder createNewReminder;
    private final UnknownCommand unknownCommand;
    private final ShowAllReminders showAllReminders;
    private final Start start;
    private final Help help;
    private final AboutBot aboutBot;
    private final SetUtc setUtc;
    private final UnsubscribeUser unsubscribeUser;

    public CommandHandler(TelegramBot telegramBot, DeleteReminder newReminder, DeleteReminder deleteReminder, CreateNewReminder createNewReminder, Help help, UnknownCommand unknownCommand, ShowAllReminders showAllReminders, Start start, AboutBot aboutBot, SetUtc setUtc, UnsubscribeUser unsubscribeUser) {
        this.telegramBot = telegramBot;
        this.deleteReminder = deleteReminder;
        this.createNewReminder = createNewReminder;
        this.help = help;
        this.unknownCommand = unknownCommand;
        this.showAllReminders = showAllReminders;
        this.start = start;
        this.aboutBot = aboutBot;
        this.setUtc = setUtc;
        this.unsubscribeUser = unsubscribeUser;
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
        if(command.isPresent()) {
            switch (command.get()) {
                case START_COMAND -> start.startCallBack(chatId, update);
                case HELP_COMAND -> help.helpCallBack(chatId);
                case CREATE_COMAND -> createNewReminder.preparingToCreateNewReminder(chatId);
                case SHOW_COMAND -> showAllReminders.showMyReminders(chatId);
                case DELETE_COMAND -> deleteReminder.preparingForDeletion(chatId);
                case ABOUT_COMAND -> aboutBot.aboutCallBack(chatId);
                case SET_UTS -> setUtc.preparingForSetUtc(chatId);
                case UNSUBSCRIBE_USER -> unsubscribeUser.preparingUnsubscribe(chatId);
            }
        } else{
            unknownCommand.unknownCommandCallBack(chatId);//Если введена неизвестная команда
        }
    }

}
