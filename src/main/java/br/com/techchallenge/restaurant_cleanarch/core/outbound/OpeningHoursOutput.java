package br.com.techchallenge.restaurant_cleanarch.core.outbound;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OpeningHoursOutput(
    Long id,
    DayOfWeek dayOfDay,
    LocalTime openHour,
    LocalTime closeHour
) {}
