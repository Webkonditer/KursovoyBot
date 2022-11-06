package com.example.kursovoybot.handrer.callback.help;

import com.example.kursovoybot.handrer.command.Command;
import com.example.kursovoybot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.lang.String.*;

@Component
@Slf4j
public class Help {

    private Command command;

    private TelegramBot telegramBot;

    public Help(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    private static final String HELP_TEXT = """
            Этот бот предназначен для создания напоминалок. Любой пользователь бота может установить в нем напоминания для себя. И бот в назначенное время покажет именно этому пользователю его напоминание.

            В меню доступны следующие команды:

            Команда  %s выводит приветственное сообщение и регистрирует пользователя в базе.

            Команда %s выводит раздел помощи.

            Команда /create_a_reminder создает новое напоминание.

            Команда /show_my_reminders выводит все Ваши напоминания.

            Команда /delete удаляет напоминание.
                        
            Команда /about выводит сведения о боте.
            """.formatted(Command.START_COMAND.getName(),
                                Command.HELP_COMAND.getName());

    public void helpCallBack(long chatId){
        telegramBot.sendMessage(chatId, HELP_TEXT);
    }

}


