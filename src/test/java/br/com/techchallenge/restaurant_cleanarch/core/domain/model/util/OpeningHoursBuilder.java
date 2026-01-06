package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.OpeningHoursInput;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class OpeningHoursBuilder {
    
    private Long id;
    private DayOfWeek dayOfDay;
    private LocalTime openHour;
    private LocalTime closeHour;

    public OpeningHoursBuilder() {
        this.id = 1L;
        this.dayOfDay = DayOfWeek.FRIDAY;
        this.openHour = LocalTime.of(0, 0);
        this.closeHour = LocalTime.of(23, 59);
    }

    public OpeningHoursBuilder withoutId() {
        this.id = null;
        return this;
    }

    public OpeningHoursBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OpeningHoursBuilder withDayOfDay(DayOfWeek dayOfDay) {
        this.dayOfDay = dayOfDay;
        return this;
    }

    public OpeningHoursBuilder withOpenHour(LocalTime openHour) {
        this.openHour = openHour;
        return this;
    }

    public OpeningHoursBuilder withCloseHour(LocalTime closeHour) {
        this.closeHour = closeHour;
        return this;
    }

    public OpeningHours build() {
        return new OpeningHours(id, dayOfDay, openHour, closeHour);
    }

    public OpeningHoursInput buildInput() {
        return new OpeningHoursInput(dayOfDay, openHour, closeHour);
    }
}
