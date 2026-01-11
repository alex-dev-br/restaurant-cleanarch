package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para User")
class UserTest {

    private UserType restauranteOwnerUserType;

    @BeforeEach
    void setUp() {
        restauranteOwnerUserType = new UserType(
            1L,
            "Dono de Restaurante",
            Set.of(new Role(1L, UserRoles.RESTAURANT_OWNER.getRoleName()))
        );
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando name for nulo")
    void deveLancarExcecaoQuandoNameForNulo() {
        var invalidBuilder = new UserBuilder().withName(null);
        assertThatThrownBy(invalidBuilder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Name cannot be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("Deve lançar BusinessException quando name for vazio ou blank")
    void deveLancarExcecaoQuandoNameForBlank(String name) {
        UUID uuid = UUID.randomUUID();
        Address address = new AddressBuilder().build();
        assertThatThrownBy(() -> new User(uuid, name, "email@mail.com", address, restauranteOwnerUserType, "secret"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Name cannot be blank");
    }

    @Test
    @DisplayName("Deve criar User válido como dono de restaurante")
    void deveCriarUserValidoComoDonoDeRestaurante() {
        Address address = new AddressBuilder().build();
        String userName = "tester";
        String emailAddress = "email@mail.com";
        String password = "secret";
        User user = new User(null, userName, emailAddress, address, restauranteOwnerUserType, password);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo(userName);
        assertThat(user.getPasswordHash()).isEqualTo(password);
        assertThat(user.getEmail()).isEqualTo(emailAddress);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getUserType()).isEqualTo(restauranteOwnerUserType);
        assertThat(user.canOwnRestaurant()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException sem tipo de usuário")
    void deveLancarExcecaoSemTipoUsuario() {
        UUID uuid = UUID.randomUUID();
        Address address = new AddressBuilder().build();
        assertThatThrownBy(() -> new User(uuid, "tester", "email@mail.com", address, null, "secret"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("User type cannot be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid-email", "email@", "@domain.com", "email@domain", "email@.com"})
    @DisplayName("Deve lançar BusinessException com email inválido")
    void deveLancarExcecaoEmailInvalido(String invalidEmail) {
        UUID uuid = UUID.randomUUID();
        Address address = new AddressBuilder().build();
        assertThatThrownBy(() -> new User(uuid, "tester", invalidEmail, address, restauranteOwnerUserType, "secret"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email inválido");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando email for nulo")
    void deveLancarExcecaoQuandoEmailForNulo() {
        UUID uuid = UUID.randomUUID();
        Address address = new AddressBuilder().build();
        assertThatThrownBy(() -> new User(uuid, "tester", null, address, restauranteOwnerUserType, "secret"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    @DisplayName("Deve permitir address nulo (comportamento atual)")
    void devePermitirAddressNulo() {
        UUID uuid = UUID.randomUUID();
        var user = new User(uuid, "tester", "email@email.com", null, restauranteOwnerUserType, "secret");

        assertThat(user.getAddress()).isNull();
        assertThat(user.canOwnRestaurant()).isTrue();
    }

    @Test
    @DisplayName("Deve verificar que User não é dono de restaurante")
    void deveVerificarNaoEDono() {
        UserType clienteType = new UserType(1L, "Cliente", Set.of(new Role(1L, RestaurantRoles.VIEW_RESTAURANT.getRoleName())));

        UUID uuid = UUID.randomUUID();
        var address = new AddressBuilder().build();
        var user = new User(uuid, "tester", "email@email.com", address, clienteType, "secret");

        assertThat(user.canOwnRestaurant()).isFalse();
    }

    @Test
    @DisplayName("Deve considerar usuários iguais quando ids são iguais")
    void deveSerIgualQuandoIdsForeamIguais() {
        UUID sharedId = UUID.randomUUID();
        Address address = new AddressBuilder().build();

        var ana = new User(sharedId, "Ana", "ana.maria@email.com", address, restauranteOwnerUserType, "secret");
        var anaMaria = new User(sharedId, "Ana Maria", "ana.maria@email.com", address, restauranteOwnerUserType, "secret");

        assertThat(ana).isEqualTo(anaMaria).hasSameHashCodeAs(anaMaria);
    }

    @Test
    @DisplayName("Deve considerar usuários diferentes quando ids são diferentes")
    void deveSerDiferenteQuandoIdsForeamDiferentes() {
        Address address = new AddressBuilder().build();
        var ana = new User(UUID.randomUUID(), "Ana", "ana.maria@email.com", address, restauranteOwnerUserType, "secret");
        var anaMaria = new User(UUID.randomUUID(), "Ana", "ana.maria@email.com", address, restauranteOwnerUserType, "secret");

        assertThat(ana).isNotEqualTo(anaMaria).doesNotHaveSameHashCodeAs(anaMaria);
    }

    @Test
    @DisplayName("Deve respeitar contrato equals/hashCode com id nulo")
    void deveRespeitarContratoComIdNulo() {
        Address address = new AddressBuilder().build();

        var ana = new User(null, "Ana", "ana.maria@email.com", address, restauranteOwnerUserType, "secret");
        var anaMaria = new User(null, "Ana Maria", "ana.maria@email.com", address, restauranteOwnerUserType, "secret");

        assertThat(ana).isNotEqualTo(anaMaria).doesNotHaveSameHashCodeAs(anaMaria);
    }

    @Test
    @DisplayName("Deve considerar diferentes quando um tem id nulo e outro não")
    void deveSerDiferenteQuandoUmIdNuloEOutroNao() {
        Address address = new AddressBuilder().build();
        var ana = new User(UUID.randomUUID(), "Ana", "ana.maria@email.com", address, restauranteOwnerUserType, "secret");
        var anaMaria = new User(null, "Ana Maria", "ana.maria@email.com", address, restauranteOwnerUserType, "secret");

        assertThat(anaMaria).isNotEqualTo(ana).doesNotHaveSameHashCodeAs(ana);
    }

    @Test
    @DisplayName("Deve retornar false em equals quando objeto não é instancia de User")
    void deveRetornarFalseQuandoObjetoNaoForUser() {
        var uuid = UUID.randomUUID();
        var address = new AddressBuilder().build();
        var user = new User(uuid, "tester", "email@mail.com", address, restauranteOwnerUserType, "secret");
        Object notUser = "String qualquer";

        assertThat(user).isNotEqualTo(notUser);
    }
}