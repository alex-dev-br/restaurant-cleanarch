package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class OpeningHoursRequest {
    @NotNull
    @DateTimeFormat(pattern = "EEEE")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private final DayOfWeek dayOfWeek;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private final LocalTime openHour;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private final LocalTime closeHour;
}
