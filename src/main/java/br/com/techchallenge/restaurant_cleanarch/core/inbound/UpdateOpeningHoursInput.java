package br.com.techchallenge.restaurant_cleanarch.core.inbound;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record UpdateOpeningHoursInput(
    Long id,
    DayOfWeek dayOfWeek,
    LocalTime openHour,
    LocalTime closeHour
) {}
