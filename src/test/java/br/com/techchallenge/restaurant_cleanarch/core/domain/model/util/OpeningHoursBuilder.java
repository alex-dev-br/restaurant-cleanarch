package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.OpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateOpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.OpeningHoursOutput;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class OpeningHoursBuilder {

    private Long id;
    private DayOfWeek dayOfDay; // (sim, o input tem esse nome)
    private LocalTime openHour;
    private LocalTime closeHour;

    public OpeningHoursBuilder() {
        withDefaults();
    }

    public OpeningHoursBuilder withDefaults() {
        this.id = 1L;
        this.dayOfDay = DayOfWeek.MONDAY;
        this.openHour = LocalTime.of(10, 0);
        this.closeHour = LocalTime.of(22, 0);
        return this;
    }

    public OpeningHoursBuilder copy() {
        var b = new OpeningHoursBuilder().withDefaults();
        b.id = this.id;
        b.dayOfDay = this.dayOfDay;
        b.openHour = this.openHour;
        b.closeHour = this.closeHour;
        return b;
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

    // alias pra evitar confusão com o domínio (que usa dayOfWeek)
    public OpeningHoursBuilder withDayOfWeek(DayOfWeek dayOfWeek) {
        return withDayOfDay(dayOfWeek);
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

    public UpdateOpeningHoursInput buildUpdateInput() {
        return new UpdateOpeningHoursInput(id, dayOfDay, openHour, closeHour);
    }

    public OpeningHoursOutput buildOutput() {
        return new OpeningHoursOutput(id, dayOfDay, openHour, closeHour);
    }
}
