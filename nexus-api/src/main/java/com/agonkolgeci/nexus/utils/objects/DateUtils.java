package com.agonkolgeci.nexus.utils.objects;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;

public class DateUtils {

    public static final DateTimeFormatter PRECISE_FORMATTER_LONG_Y = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");
    public static final DateTimeFormatter PRECISE_FORMATTER_SHORT_Y = DateTimeFormatter.ofPattern("dd/MM/yy 'à' HH:mm");

    public static boolean isEffective(@NotNull LocalDateTime localDateTime, @NotNull TemporalAmount temporalAmount) {
        return localDateTime.plus(temporalAmount).isAfter(LocalDateTime.now());
    }

    public static boolean isEffective(@NotNull LocalDateTime localDateTime) {
        return isEffective(localDateTime, Duration.ZERO);
    }


}
