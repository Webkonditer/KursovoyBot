package com.example.kursovoybot.handrer.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CommandHandler111 {

  void handleCommand(Message message, String text) throws TelegramApiException;

  Command getCommand();
}
