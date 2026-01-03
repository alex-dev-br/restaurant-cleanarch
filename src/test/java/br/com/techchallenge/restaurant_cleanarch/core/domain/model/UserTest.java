package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Deve lançar NullPointerException quando name for nulo")
    void deveLancarExcecaoQuandoNameForNulo() {
        // Arrange
        var invalidBuilder = new UserBuilder().withName(null);

        // Act & Assert
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando name for vazio ou blank")
    void deveLancarExcecaoQuandoNameForBlank() {
        // Arrange + Act + Assert - caso string vazia
        assertThatThrownBy(() -> new UserBuilder().withName("").build())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Name cannot be blank.");

        // Arrange + Act + Assert - caso apenas espaços
        assertThatThrownBy(() -> new UserBuilder().withName("   ").build())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Name cannot be blank.");
    }

    @Test
    @DisplayName("Deve criar User válido como dono de restaurante")
    void deveCriarUserValidoComoDonoDeRestaurante() {
        // Arrange
        Set<Role> roles = Set.of(new Role(null, "ADMIN"));
        UserType ownerType = new UserType(null, "Dono de Restaurante", roles);

        Address address = new AddressBuilder().build(); // endereço válido padrão

        var userBuilder = new UserBuilder()
                .withName("Maria Oliveira")           // explícito, evita dependência de defaults
                .withEmail("maria@restaurante.com")   // explícito
                .withAddress(address)
                .withUserType(ownerType);

        // Act
        User user = userBuilder.build(); // não precisa de assertDoesNotThrow se esperamos sucesso

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull(); // ainda não persistido
        assertThat(user.getName()).isEqualTo("Maria Oliveira");
        assertThat(user.getEmail()).isEqualTo("maria@restaurante.com");
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getUserType()).isEqualTo(ownerType);
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
    @DisplayName("Deve lançar NullPointerException quando email for nulo")
    void deveLancarExcecaoQuandoEmailForNulo() {
        // Arrange
        var invalidBuilder = new UserBuilder().withEmail(null);

        // Act & Assert
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Email cannot be null.");
    }

    @Test
    @DisplayName("Deve permitir address nulo (comportamento atual)")
    void devePermitirAddressNulo() {
        // Arrange
        Set<Role> roles = Set.of(new Role(null, "ADMIN"));
        UserType ownerType = new UserType(null, "Dono de Restaurante", roles);

        var userBuilder = new UserBuilder()
                .withUserType(ownerType)
                .withAddress(null);  // explicitamente null

        // Act
        var user = userBuilder.build();

        // Assert
        assertThat(user.getAddress()).isNull();
        assertThat(user.isRestaurantOwner()).isTrue();
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

    @Test
    @DisplayName("Deve considerar usuários iguais quando ids são iguais")
    void deveSerIgualQuandoIdsForeamIguais() {
        // Arrange
        UUID sharedId = UUID.randomUUID();

        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));
        Address address = new AddressBuilder().build();

        User user1 = new UserBuilder()
                .withId(sharedId)
                .withName("João")
                .withEmail("joao@example.com")
                .withAddress(address)
                .withUserType(ownerType)
                .build();

        User user2 = new UserBuilder()
                .withId(sharedId)  // mesmo id
                .withName("Maria") // outros campos diferentes
                .withEmail("maria@example.com")
                .withAddress(address)
                .withUserType(ownerType)
                .build();

        // Act & Assert
        assertThat(user1)
                .isEqualTo(user2)
                .hasSameHashCodeAs(user2);
    }

    @Test
    @DisplayName("Deve considerar usuários diferentes quando ids são diferentes")
    void deveSerDiferenteQuandoIdsForeamDiferentes() {
        // Arrange
        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));
        Address address = new AddressBuilder().build();

        User user1 = new UserBuilder()
                .withId(UUID.randomUUID())
                .withUserType(ownerType)
                .withAddress(address)
                .build();

        User user2 = new UserBuilder()
                .withId(UUID.randomUUID())  // id diferente
                .withUserType(ownerType)
                .withAddress(address)
                .build();

        // Act & Assert
        assertThat(user1).isNotEqualTo(user2);
        // hashCode pode coincidir por colisão, mas é raro — não exigimos desigualdade
    }

    @Test
    @DisplayName("Deve respeitar contrato equals/hashCode com id nulo")
    void deveRespeitarContratoComIdNulo() {
        // Arrange
        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));

        User user1 = new UserBuilder().withUserType(ownerType).withoutId().build();
        User user2 = new UserBuilder().withUserType(ownerType).withoutId().build();

        // Act & Assert
        assertThat(user1).isNotEqualTo(user2); // objetos diferentes, mesmo sem id
        assertThat(user1.equals(user1)).isTrue(); // reflexividade
    }

    @Test
    @DisplayName("Deve considerar diferentes quando um tem id nulo e outro não")
    void deveSerDiferenteQuandoUmIdNuloEOutroNao() {
        // Arrange
        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));

        UUID someId = UUID.randomUUID();

        User userWithId = new UserBuilder()
                .withId(someId)
                .withUserType(ownerType)
                .build();

        User userWithoutId = new UserBuilder()
                .withUserType(ownerType)
                .withoutId()
                .build();

        // Act & Assert
        assertThat(userWithId).isNotEqualTo(userWithoutId);
        assertThat(userWithoutId).isNotEqualTo(userWithId);

        // hashCode deve ser diferente (um usa id, outro usa super.hashCode())
        assertThat(userWithId.hashCode()).isNotEqualTo(userWithoutId.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false em equals quando objeto não é instancia de User")
    void deveRetornarFalseQuandoObjetoNaoForUser() {
        // Arrange
        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));
        User user = new UserBuilder().withUserType(ownerType).build();

        Object notUser = "String qualquer";  // objeto de outra classe

        // Act & Assert
        assertThat(user.equals(notUser)).isFalse();
        assertThat(user.equals(null)).isFalse();  // cobre o caso null também
    }



}
