package com.example.kursovoybot.handrer.callback.delete;

import com.example.kursovoybot.util.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.stream.Stream;


@Getter
@RequiredArgsConstructor
public enum NewReminderButton {
  YES_BUTTON("newReminderYes", "Да"),
  NO_BUTTON("newReminderNo", "Нет");

  private final String data;
  private final String label;

  public static Optional<NewReminderButton> parseCommand(String command) {
    if (StringUtil.isBlank(command)) {
      return Optional.empty();
    }
    String formatName = StringUtil.trim(command).toLowerCase();
    return Stream.of(values()).filter(c -> c.data.equalsIgnoreCase(formatName) || c.label.equalsIgnoreCase(formatName))
            .findFirst();
  }
}
