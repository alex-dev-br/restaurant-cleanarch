package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RestaurantTest {

    @Test
    @DisplayName("Deve criar Restaurant válido sem lançar exceção")
    void deveCriarRestaurantValido() {
        // Arrange
//        UserType ownerType = new UserType(1L,"Dono de Restaurante");
        var restaurantBuilder = new RestaurantBuilder();

        // Act & Assert
        assertDoesNotThrow(restaurantBuilder::build);
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono válido")
    void deveLancarExcecaoSemDonoValido() {
        // Arrange
        var invalidOwner = new UserBuilder().withUserType(new UserType(1L, "Cliente")).build();
        var invalidRestaurantBuilder = new RestaurantBuilder().withOwner(invalidOwner);

        // Act & Assert
        assertThatThrownBy(invalidRestaurantBuilder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O restaurante deve ter um dono válido.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono")
    void deveLancarExcecaoSemDono() {
        // Arrange
        var invalidBuilder = new RestaurantBuilder().withOwner(null);  // Sem owner

        // Act & Assert
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O restaurante deve ter um dono válido.");
    }
}
