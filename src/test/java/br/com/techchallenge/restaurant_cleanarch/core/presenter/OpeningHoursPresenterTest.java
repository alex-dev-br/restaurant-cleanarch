package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.OpeningHoursOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para OpeningHoursPresenter")
class OpeningHoursPresenterTest {

    @Test
    @DisplayName("Deve converter OpeningHours para OpeningHoursOutput corretamente")
    void shouldConvertOpeningHoursToOpeningHoursOutput() {
        Long id = 1L;
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        LocalTime openHour = LocalTime.of(9, 0);
        LocalTime closeHour = LocalTime.of(18, 0);

        OpeningHours openingHours = new OpeningHours(id, dayOfWeek, openHour, closeHour);

        OpeningHoursOutput output = OpeningHoursPresenter.toOutput(openingHours);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.dayOfWeek()).isEqualTo(dayOfWeek);
        assertThat(output.openHour()).isEqualTo(openHour);
        assertThat(output.closeHour()).isEqualTo(closeHour);
    }
}
