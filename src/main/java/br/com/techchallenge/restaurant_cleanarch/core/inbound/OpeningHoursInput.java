package br.com.techchallenge.restaurant_cleanarch.core.inbound;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OpeningHoursInput (
    DayOfWeek dayOfWeek,
    LocalTime openHour,
    LocalTime closeHour
) {}
