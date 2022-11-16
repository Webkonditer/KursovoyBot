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
            Я напомню тебе о самых важных событиях в твоей жизни. 
            Запланируй мероприятия, сохрани дни рождения друзей и близких или другие события.
            Я пришлю тебе напоминание в назначенный день и час.

            В моем меню доступны следующие команды:

            Команда  %s выводит приветственное сообщение и подключает тебя к боту.

            Команда %s выводит раздел помощи.

            Команда %s создает новое напоминание.

            Команда %s выводит все твои напоминания.

            Команда %s удаляет напоминание.
                        
            Команда %s выводит сведения о боте.
            
            Команда %s позволяет установить часовой пояс.
            
            Команда %s отписывает тебя от бота.
            """.formatted(
                                Command.START_COMAND.getName(),
                                Command.HELP_COMAND.getName(),
                                Command.CREATE_COMAND.getName(),
                                Command.SHOW_COMAND.getName(),
                                Command.DELETE_COMAND.getName(),
                                Command.ABOUT_COMAND.getName(),
                                Command.SET_UTS.getName(),
                                Command.UNSUBSCRIBE_USER.getName()
                         );

    public void helpCallBack(long chatId){
        sendingMessages.sendMessageWithMenu(chatId, HELP_TEXT);
    }

}


