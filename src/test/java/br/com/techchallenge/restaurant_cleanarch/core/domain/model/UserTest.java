package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserTest {

    @Test
    @DisplayName("Deve criar User válido como dono de restaurante")
    void deveCriarUserValidoComoDonoDeRestaurante() {
        // Arrange
        UserType ownerType = UserType.builder()
                .name("Dono de Restaurante")
                .build();

        Address address = Address.builder().
                street("Rua Exemplo")
                .number("123")
                .city("São Paulo")
                .state("SP")
                .zipCode("01000-000")
                .build();

        User user = User.builder()
                .name("João Silva")
                .email("joao@example.com")
                .address(address)
                .userType(ownerType)
                .build();

        // Act
        user.validate();

        // Assert
        assertDoesNotThrow(user::validate);
        assertThat(user.isRestaurantOwner()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem tipo de usuário")
    void deveLancarExcecaoSemTipoUsuario() {
        // Arrange
        User invalid = User.builder().name("Maria").email("maria@exemple.com").build();

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Tipo de usuário é obrigatório.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException com email inválido")
    void deveLancarExcecaoEmailInvalido() {
        // Arrange
        UserType type = UserType.builder().name("Cliente").build();
        User invalid = User.builder().name("Pedro").email("inválido").userType(type).build();

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email inválido.");
    }

    @Test
    @DisplayName("Deve verificar que User não é dono de restaurante")
    void deveVerificarNaoEDono() {
        // Arrange
        UserType clienteType = UserType.builder().name("Cliente").build();
        User user = User.builder().userType(clienteType).build();

        // Act
        boolean isOwner = user.isRestaurantOwner();

        // Assert
        assertThat(isOwner).isFalse();
    }


}
