package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserTest {

    @Test
    @DisplayName("Deve criar User válido como dono de restaurante")
    void deveCriarUserValidoComoDonoDeRestaurante() {
        // Arrange
        Set<Role> roles = Set.of(new Role(null, "ADMIN"));
        UserType ownerType = new UserType(null, "Dono de Restaurante", roles);
        var userBuilder = new UserBuilder().withUserType(ownerType);

        // Act
//        user.validate();

        // Assert
        var user = assertDoesNotThrow(userBuilder::build);
        assertThat(user.isRestaurantOwner()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem tipo de usuário")
    void deveLancarExcecaoSemTipoUsuario() {
        // Arrange
        var invalidBuilder = new UserBuilder().withUserType(null);

        // Act & Assert
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User type cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException com email inválido")
    void deveLancarExcecaoEmailInvalido() {
        // Arrange
        var invalidBuilder = new UserBuilder().withEmail("inválido");

        // Act & Assert
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email inválido.");
    }

    @Test
    @DisplayName("Deve verificar que User não é dono de restaurante")
    void deveVerificarNaoEDono() {
        // Arrange
        Set<Role> roles = Set.of(new Role(null, "ADMIN"));
        UserType clienteType = new UserType(1L, "Cliente", roles);
        User user = new UserBuilder().withUserType(clienteType).build();

        // Act
        boolean isOwner = user.isRestaurantOwner();

        // Assert
        assertThat(isOwner).isFalse();
    }


}
