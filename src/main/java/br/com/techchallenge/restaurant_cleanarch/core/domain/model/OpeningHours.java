package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
public class OpeningHours {
    private final Long id;
    private final DayOfWeek dayOfDay;
    private final LocalTime openHour;
    private final LocalTime closeHour;

    public OpeningHours(Long id, DayOfWeek dayOfDay, LocalTime openHour, LocalTime closeHour) {
        this.id = id;
        this.dayOfDay = dayOfDay;
        this.openHour = openHour;
        this.closeHour = closeHour;
        validate();
    }

    public void validate() {
        if (dayOfDay == null) {
            throw new BusinessException("Dia da semana é obrigatório.");
        }
        if (openHour == null) {
            throw new BusinessException("Horário de abertura é obrigatório.");
        }
        if (closeHour == null) {
            throw new BusinessException("Horário de fechamento é obrigatório.");
        }
        if (openHour.isAfter(closeHour)) {
            throw new BusinessException("Horário de abertura não pode ser depois do horário de fechamento.");
        }
    }
}
