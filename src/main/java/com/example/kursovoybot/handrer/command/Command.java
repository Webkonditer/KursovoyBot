package com.example.kursovoybot.handrer.command;

import com.example.kursovoybot.util.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;


@Getter
@RequiredArgsConstructor
public enum Command {
  START_COMAND("/start", "Регистрация пользователя", "Домой \uD83C\uDFE1"),
  HELP_COMAND("/help", "Справка по боту", "❓ Справка"),
  CREATE_COMAND("/create_a_reminder", "Создание нового напоминания", "✍️ Создать новое"),
  SHOW_COMAND("/show_my_reminders", "Все напоминания", "\uD83D\uDCD9 Показать все мои напоминания"),
  DELETE_COMAND("/delete", "Удаление напоминания", "⛔️ Удалить"),
  ABOUT_COMAND("/about", "Информация о боте", "\uD83D\uDCD4 О боте"),
  SET_UTS("/set_utc", "Изменение часового пояса", "\uD83D\uDD58 Изменить часовой пояс");

  private final String name;
  private final String desc;
  private final String label;

  public static Optional<Command> parseCommand(String command) {
    if (StringUtil.isBlank(command)) {
      return Optional.empty();
    }
    String formatName = StringUtil.trim(command).toLowerCase();
    return Stream.of(values()).filter(c -> c.name.equalsIgnoreCase(formatName) || c.label.equalsIgnoreCase(formatName))
            .findFirst();
  }
}
