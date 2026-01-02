package br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import java.time.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes para OpeningHours")
public class OpeningHoursTest {

    private static Long ID = 1L;
    private static DayOfWeek DAY = DayOfWeek.MONDAY;
    private static LocalTime OPEN = LocalTime.of(9, 0);
    private static LocalTime CLOSE = LocalTime.of(18, 0);

    @Test
    @DisplayName("Deve criar OpeningHours válido sem lançar exceção")
    void shoulCreateValidOpeningHours() {
        // Act
        OpeningHours openingHours = new OpeningHours(ID, DAY, OPEN, CLOSE);

        // Assert
        assertThat(openingHours).isNotNull();
        assertThat(openingHours.getId()).isEqualTo(ID);
        assertThat(openingHours.getDayOfDay()).isEqualTo(DAY);
        assertThat(openingHours.getOpenHour()).isEqualTo(OPEN);
        assertThat(openingHours.getCloseHour()).isEqualTo(CLOSE);

    }

    @Test
    @DisplayName("Deve lançar exceção quando dayOfWeek for nulo")
    void shouldThrowExceptionWhenDayOfWeekIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new OpeningHours(ID, null, OPEN, CLOSE))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Dia da semana é obrigatório.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando openHour for nulo")
    void shouldThrowExceptionWhenOpenHourIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new OpeningHours(ID, DAY, null, CLOSE))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Horário de abertura é obrigatório.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando closeHour for nulo")
    void shouldThrowExceptionWhenCloseHourIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new OpeningHours(ID, DAY, OPEN, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Horário de fechamento é obrigatório.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando openHour for após closeHour")
    void shouldThrowExceptionWhenOpenAfterClose() {
        LocalTime invalidOpen = LocalTime.of(19, 0); // Após CLOSE (18:00)

        // Act & Assert
        assertThatThrownBy(() -> new OpeningHours(ID, DAY, invalidOpen, CLOSE))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Horário de abertura não pode ser depois do horário de fechamento.");
    }

    @Test
    @DisplayName("Deve verificar igualdade quando objetos são idênticos")
    void shouldBeEqualWhenObjectsAreIdentical() {
        OpeningHours oh1 = new OpeningHours(ID, DAY, OPEN, CLOSE);
        OpeningHours oh2 = new OpeningHours(ID, DAY, OPEN, CLOSE);

        // Assert
        assertThat(oh1).isEqualTo(oh2);
        assertThat(oh1.hashCode()).isEqualTo(oh2.hashCode());
    }

    @Test
    @DisplayName("Deve verificar desigualdade quando dias são diferentes")
    void shouldNotBeEqualWhenDaysAreDifferent() {
        OpeningHours oh1 = new OpeningHours(ID, DAY, OPEN, CLOSE);
        OpeningHours oh2 = new OpeningHours(ID, DayOfWeek.TUESDAY, OPEN, CLOSE);

        // Assert
        assertThat(oh1).isNotEqualTo(oh2);
        assertThat(oh1.hashCode()).isNotEqualTo(oh2.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString correto")
    void shouldGenerateCorrectToString() {
        OpeningHours openingHours = new OpeningHours(ID, DAY, OPEN, CLOSE);

        // Assert
        assertThat(openingHours.toString())
                .contains("id=" + ID)
                .contains("dayOfDay=" + DAY)
                .contains("openHour=" + OPEN)
                .contains("closeHour=" + CLOSE);
    }

}
