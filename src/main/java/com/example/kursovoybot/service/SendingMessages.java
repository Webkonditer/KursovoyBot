package com.example.kursovoybot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.example.kursovoybot.handrer.command.Command;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SendingMessages {

    private final TelegramBot telegramBot;
    private Command command;

    public SendingMessages(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     *Подготовка сообщения пользователю.
     *
     * @param chatId  id текущего чата
     * @param textToSend  текст сообщения
     */
    public void sendMessage(long chatId, String textToSend){

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);

    }

    /**
     *Отправка приветственного сообщения пользователю и формирование нижнего меню.
     *
     * @param chatId  id текущего чата
     * @param textToSend  текст сообщения
     */
    public void sendStartMessage(long chatId, String textToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        //Добавление клавиатуры к собщению.
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(Command.SHOW_COMAND.getLabel());
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add(command.CREATE_COMAND.getLabel());
        row.add(Command.DELETE_COMAND.getLabel());
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add(command.HELP_COMAND.getLabel());
        row.add(command.ABOUT_COMAND.getLabel());
        keyboardRows.add(row);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboardRows);//Формирование клавиатуры
        message.setReplyMarkup(keyboardMarkup);//Добавление клавиатуры
        //----------------------------------------

        executeMessage(message);
    }


    /**
     *Отправка измененного сообщения пользователю.
     *
     * @param text  текст сообщения
     * @param chatId  id текущего чата
     * @param messageId  id изменяемого сообщения
     */
    private void executeEditMessageText(String text, long chatId, long messageId){

        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int)messageId);
        try{
            telegramBot.execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }

    }

    /**
     *Отправка сообщения пользователю.
     *
     * @param message  объект сообщения
     */
    void executeMessage(SendMessage message){

        try{
            telegramBot.execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }

    }

}
