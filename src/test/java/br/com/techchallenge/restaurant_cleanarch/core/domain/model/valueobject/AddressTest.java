package br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AddressTest {

    @Test
    @DisplayName("Deve criar Address válido sem lançar exceção")
    void deveCriarAddressValido() {
        // Arrange
        var addressBuilder = new AddressBuilder();

        // Act & Assert
        assertDoesNotThrow(addressBuilder::build);
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem rua")
    void deveLancarExcecaoSemRua() {
        // Arrange
        var addressBuilder = new AddressBuilder().withStreet("");

        // Act & Assert
        assertThatThrownBy(addressBuilder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Rua é obrigatória.");
    }
}
