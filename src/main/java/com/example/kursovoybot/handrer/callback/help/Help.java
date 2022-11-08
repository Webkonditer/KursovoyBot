package com.example.kursovoybot.handrer.callback.help;

import com.example.kursovoybot.handrer.command.Command;
import com.example.kursovoybot.service.SendingMessages;
import com.example.kursovoybot.bot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Help {

    private Command command;

    private TelegramBot telegramBot;
    private SendingMessages sendingMessages;

    public Help(TelegramBot telegramBot, SendingMessages sendingMessages) {
        this.telegramBot = telegramBot;
        this.sendingMessages = sendingMessages;
    }

    private static final String HELP_TEXT = """
            Этот бот предназначен для создания напоминалок. Любой пользователь бота может установить в нем напоминания для себя. И бот в назначенное время покажет именно этому пользователю его напоминание.

            В меню доступны следующие команды:

            Команда  %s выводит приветственное сообщение и регистрирует пользователя в базе.

            Команда %s выводит раздел помощи.

            Команда %s создает новое напоминание.

            Команда %s выводит все Ваши напоминания.

            Команда %s удаляет напоминание.
                        
            Команда %s выводит сведения о боте.
            """.formatted(
                                Command.START_COMAND.getName(),
                                Command.HELP_COMAND.getName(),
                                Command.CREATE_COMAND.getName(),
                                Command.SHOW_COMAND.getName(),
                                Command.DELETE_COMAND.getName(),
                                Command.ABOUT_COMAND.getName()
                         );

    public void helpCallBack(long chatId){
        sendingMessages.sendMessage(chatId, HELP_TEXT);
    }

}


