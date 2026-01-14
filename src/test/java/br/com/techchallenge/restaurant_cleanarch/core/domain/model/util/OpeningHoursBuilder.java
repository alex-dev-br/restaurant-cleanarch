package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.OpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateOpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.OpeningHoursOutput;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class OpeningHoursBuilder {
    
    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime openHour;
    private LocalTime closeHour;

    public OpeningHoursBuilder() {
        this.id = 1L;
        this.dayOfWeek = DayOfWeek.FRIDAY;
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

    public OpeningHoursBuilder withDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
        return new OpeningHours(id, dayOfWeek, openHour, closeHour);
    }

    public OpeningHoursInput buildInput() {
        return new OpeningHoursInput(dayOfWeek, openHour, closeHour);
    }

    public UpdateOpeningHoursInput buildUpdateInput() {
        return new UpdateOpeningHoursInput(id, dayOfWeek, openHour, closeHour);
    }

    public OpeningHoursOutput buildOutput() {
        return new OpeningHoursOutput(id, dayOfWeek, openHour, closeHour);
    }
}
