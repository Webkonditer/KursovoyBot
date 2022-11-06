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
  START_COMAND("/start", "регистрирует пользователя"),
  HELP_COMAND("/help", "выводит справку по боту"),
  CREATE_COMAND("/create_a_reminder", "создает новое напоминание"),
  SHOW_COMAND("/show_my_reminders", "выводит список Ваших напоминаний"),
  DELETE_COMAND("/delete", "удаляет напоминание"),

  ABOUT_COMAND("/about", "информация о боте");

  private final String name;
  private final String desc;

  public static Optional<Command> parseCommand(String command) {
    if (StringUtil.isBlank(command)) {
      return Optional.empty();
    }
    String formatName = StringUtil.trim(command).toLowerCase();
    return Stream.of(values()).filter(c -> c.name.equalsIgnoreCase(formatName)).findFirst();
  }
}
