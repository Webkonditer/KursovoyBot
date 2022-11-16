package com.example.kursovoybot.handrer.callback.setutc;

import com.example.kursovoybot.util.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum TimeZone {
    UTC("GMT", "Лондон, Дакар, Лиссабон", 0),
    UTC1("GMT+1", "Париж, Берлин, Рим", 1),
    UTC2("GMT+2", "Калининград, Киев, Каир", 2),
    UTC3("GMT+3", "Москва, Минск, Анкара", 3),
    UTC4("GMT+4", "Самара, Тбилиси, Дубай", 4),
    UTC5("GMT+5", "Екатеринбург, Нью-Дели, Исламабад", 5),
    UTC6("GMT+6", "Омск, Астана, Дакка", 6),
    UTC7("GMT+7", "Красноярск, Бангкок, Ханой", 7),
    UTC8("GMT+8", "Иркутск, Пекин, Сингапур", 8),
    UTC9("GMT+9", "Якутск, Сеул, Токио", 9),
    UTC10("GMT+10", "Владивосток, Гуам, Брисбен", 10),
    UTC11("GMT+11", "Магадан, Сидней, Хобарт", 11),
    UTC12("GMT+12", "Анадырь, Маршалловы острова", 12),
    UTC13("GMT+13", "Окленд, Фиджи, Тонга", 13),
    UTC_11("GMT-11", "Острова Мидуэй", -11),
    UTC_10("GMT-10", "Гавайи", -10),
    UTC_9("GMT-9", "Аляска", -9),
    UTC_8("GMT-8", "Тихуана", -8),
    UTC_7("GMT-7", "Аризона, Чиуауа", -7),
    UTC_6("GMT-6", "Мехико, Саскачеван", -6),
    UTC_5("GMT-5", "Вашингтон, Богота", -5),
    UTC_4("GMT-4", "Венесуэла, Барбадос, Канада", -4),
    UTC_3("GMT-3", "Буэнос-Айрес, Монтевидео, Сантьяго", -3),
    UTC_2("GMT-2", "Среднеатлантический", -2),
    UTC_1("GMT-1", "Азорские острова, Острова зеленого мыса", -1);

    private final String name;
    private final String desc;
    private final Integer utc;

    public static Optional<TimeZone> parseTimeZone(Integer userUtc) {
        return Stream.of(values()).filter(c -> c.utc.equals(userUtc))
                .findFirst();
    }

}