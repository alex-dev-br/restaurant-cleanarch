package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para User")
class UserTest {

    @Test
    @DisplayName("Deve lançar NullPointerException quando name for nulo")
    void deveLancarExcecaoQuandoNameForNulo() {
        var invalidBuilder = new UserBuilder().withName(null);
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando name for vazio ou blank")
    void deveLancarExcecaoQuandoNameForBlank() {
        assertThatThrownBy(() -> new UserBuilder().withName("").build())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Name cannot be blank.");

        assertThatThrownBy(() -> new UserBuilder().withName("   ").build())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Name cannot be blank.");
    }

    @Test
    @DisplayName("Deve criar User válido como dono de restaurante")
    void deveCriarUserValidoComoDonoDeRestaurante() {
        Set<Role> roles = Set.of(new Role(null, "ADMIN"));
        UserType ownerType = new UserType(null, "Dono de Restaurante", roles);

        Address address = new AddressBuilder().build();

        var userBuilder = new UserBuilder()
                .withName("Maria Oliveira")
                .withEmail("maria@restaurante.com")
                .withAddress(address)
                .withUserType(ownerType);

        User user = userBuilder.build();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull(); // Ainda não persistido
        assertThat(user.getName()).isEqualTo("Maria Oliveira");
        assertThat(user.getEmail()).isEqualTo("maria@restaurante.com");
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getUserType()).isEqualTo(ownerType);
        assertThat(user.isRestaurantOwner()).isTrue();
        assertThat(user.canOwnRestaurant()).isFalse(); // Role não é RESTAURANT_OWNER, mas isso é OK por enquanto
    }

    @Test
    @DisplayName("Deve lançar NullPointerException sem tipo de usuário")
    void deveLancarExcecaoSemTipoUsuario() {
        var invalidBuilder = new UserBuilder().withUserType(null);
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User type cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException com email inválido")
    void deveLancarExcecaoEmailInvalido() {
        var invalidBuilder = new UserBuilder().withEmail("inválido");
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email inválido.");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando email for nulo")
    void deveLancarExcecaoQuandoEmailForNulo() {
        var invalidBuilder = new UserBuilder().withEmail(null);
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Email cannot be null.");
    }

    @Test
    @DisplayName("Deve permitir address nulo (comportamento atual)")
    void devePermitirAddressNulo() {
        Set<Role> roles = Set.of(new Role(null, "ADMIN"));
        UserType ownerType = new UserType(null, "Dono de Restaurante", roles);

        var userBuilder = new UserBuilder()
                .withUserType(ownerType)
                .withAddress(null);

        var user = userBuilder.build();

        assertThat(user.getAddress()).isNull();
        assertThat(user.isRestaurantOwner()).isTrue();
    }

    @Test
    @DisplayName("Deve verificar que User não é dono de restaurante")
    void deveVerificarNaoEDono() {
        Set<Role> roles = Set.of(new Role(null, "ADMIN"));
        UserType clienteType = new UserType(1L, "Cliente", roles);
        User user = new UserBuilder().withUserType(clienteType).build();

        assertThat(user.isRestaurantOwner()).isFalse();
        assertThat(user.canOwnRestaurant()).isFalse();
    }

    @Test
    @DisplayName("Deve considerar usuários iguais quando ids são iguais")
    void deveSerIgualQuandoIdsForeamIguais() {
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
                .withId(sharedId)
                .withName("Maria")
                .withEmail("maria@example.com")
                .withAddress(address)
                .withUserType(ownerType)
                .build();

        assertThat(user1).isEqualTo(user2).hasSameHashCodeAs(user2);
    }

    @Test
    @DisplayName("Deve considerar usuários diferentes quando ids são diferentes")
    void deveSerDiferenteQuandoIdsForeamDiferentes() {
        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));
        Address address = new AddressBuilder().build();

        User user1 = new UserBuilder()
                .withId(UUID.randomUUID())
                .withUserType(ownerType)
                .withAddress(address)
                .build();

        User user2 = new UserBuilder()
                .withId(UUID.randomUUID())
                .withUserType(ownerType)
                .withAddress(address)
                .build();

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    @DisplayName("Deve respeitar contrato equals/hashCode com id nulo")
    void deveRespeitarContratoComIdNulo() {
        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));

        User user1 = new UserBuilder().withUserType(ownerType).withoutId().build();
        User user2 = new UserBuilder().withUserType(ownerType).withoutId().build();

        assertThat(user1).isNotEqualTo(user2);
        assertThat(user1.equals(user1)).isTrue();
    }

    @Test
    @DisplayName("Deve considerar diferentes quando um tem id nulo e outro não")
    void deveSerDiferenteQuandoUmIdNuloEOutroNao() {
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

        assertThat(userWithId).isNotEqualTo(userWithoutId);
        assertThat(userWithoutId).isNotEqualTo(userWithId);

        assertThat(userWithId.hashCode()).isNotEqualTo(userWithoutId.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false em equals quando objeto não é instancia de User")
    void deveRetornarFalseQuandoObjetoNaoForUser() {
        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));
        User user = new UserBuilder().withUserType(ownerType).build();

        Object notUser = "String qualquer";

        assertThat(user.equals(notUser)).isFalse();
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Deve verificar canOwnRestaurant baseado em role")
    void deveVerificarCanOwnRestaurant() {
        Set<Role> rolesWithOwner = Set.of(new Role(null, "RESTAURANT_OWNER"));
        UserType ownerType = new UserType(null, "Dono de Restaurante", rolesWithOwner);

        User userWithRole = new UserBuilder().withUserType(ownerType).build();

        assertThat(userWithRole.canOwnRestaurant()).isTrue();

        Set<Role> rolesWithoutOwner = Set.of(new Role(null, "ADMIN"));
        UserType clienteType = new UserType(null, "Cliente", rolesWithoutOwner);
        User userWithoutRole = new UserBuilder().withUserType(clienteType).build();

        assertThat(userWithoutRole.canOwnRestaurant()).isFalse();
    }
}