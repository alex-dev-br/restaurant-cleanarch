package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.valueobject.Address;
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
        UserType ownerType = UserType.builder().name("Dono de Restaurante").build();
        User owner = User.builder().userType(ownerType).build();
        Restaurant restaurant = Restaurant.builder()
                .name("Restaurante Exemplo")
                .address(new Address("Rua Teste", "30", "São Paulo", "SP", "12345-678"))
                .cuisineType("Italiana")
                .openingHours(Set.of(new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(22, 0))))
                .owner(owner)
                .build();

        // Act & Assert
        assertDoesNotThrow(restaurant::validate);
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono válido")
    void deveLancarExcecaoSemDonoValido() {
        // Arrange
        UserType clientType = UserType.builder().name("Cliente").build();
        User invalidOwner = User.builder().userType(clientType).build();
        Restaurant invalid = Restaurant.builder().owner(invalidOwner).build();

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O restaurante deve ter um dono válido.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono")
    void deveLancarExcecaoSemDono() {
        // Arrange
        Restaurant invalid = Restaurant.builder().build();  // Sem owner

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O restaurante deve ter um dono válido.");
    }
}
