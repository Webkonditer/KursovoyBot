package com.example.kursovoybot.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum PushButtonMenu {
  HOME("Домой \uD83C\uDFE1"),
  SETTING("Settings \uD83D\uDEE0"),
  INFO("Info \uD83D\uDCD6"),
  STATISTIC("Statistic \uD83D\uDCCA");

  private final String label;

  public static Optional<PushButtonMenu> parse(String name) {
    return Arrays.stream(values())
        .filter(b -> b.name().equalsIgnoreCase(name) || b.label.equalsIgnoreCase(name))
        .findFirst();
  }
}
