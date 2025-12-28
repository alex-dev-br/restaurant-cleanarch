package br.com.techchallenge.restaurant_cleanarch.core.domain.valueobject;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AddressTest {

    @Test
    @DisplayName("Deve criar Address válido sem lançar exceção")
    void deveCriarAddressValido() {
        // Arrange
        Address address = Address.builder()
                .street("Rua Exemplo")
                .number("123")
                .city("São Paulo")
                .state("SP")
                .zipCode("01000-000")
                .build();

        // Act & Assert
        assertDoesNotThrow(address::validateAddress);
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem rua")
    void deveLancarExcecaoSemRua() {
        // Arrange
        Address invalid = Address.builder().street("").build();

        // Act & Assert
        assertThatThrownBy(invalid::validateAddress)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Rua é obrigatória.");
    }
}
