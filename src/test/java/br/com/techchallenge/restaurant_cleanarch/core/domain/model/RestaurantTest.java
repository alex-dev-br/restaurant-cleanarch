package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para Restaurant")
class RestaurantTest {

    @Test
    @DisplayName("Deve criar Restaurant válido com campos preenchidos")
    void deveCriarRestaurantValido() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build(); // id null, tem role RESTAURANT_OWNER
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        // Act
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", openingHours, menu, owner);

        // Assert
        assertThat(restaurant).isNotNull();
        assertThat(restaurant.getName()).isEqualTo("Restaurante Teste");
        assertThat(restaurant.getCuisineType()).isEqualTo("Italiana");
        assertThat(restaurant.getOwner()).isNotNull().isEqualTo(owner);
        assertThat(restaurant.getOwner().getId()).isNotNull();
        assertThat(restaurant.getOwner().getName()).isNotNull().isEqualTo(owner.getName());
        assertThat(restaurant.getOwner().getEmail()).isNotNull().isEqualTo(owner.getEmail());
        assertThat(restaurant.getOpeningHours()).hasSize(6).containsExactlyInAnyOrderElementsOf(openingHours);
        assertThat(restaurant.getMenu()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono válido")
    void deveLancarExcecaoSemDonoValido() {
        // Arrange: usuário sem a role RESTAURANT_OWNER
        Set<Role> clientRoles = Set.of(new Role(null, "CLIENT")); // ou vazio
        UserType clienteType = new UserType(null, "Cliente", clientRoles);
        User invalidOwner = new UserBuilder().withUserType(clienteType).build();

        var builder = new RestaurantBuilder().withOwner(invalidOwner);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O restaurante deve ter um dono com permissão de proprietário."); // ← Mensagem atualizada
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono")
    void deveLancarExcecaoSemDono() {
        // Arrange
        var builder = new RestaurantBuilder().withOwner(null);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O dono do restaurante não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando name for nulo")
    void deveLancarExcecaoQuandoNameForNulo() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        // Act & Assert
        assertThatThrownBy(() -> new Restaurant(1L, null, address, "Italiana", openingHours, menu, owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O nome do restaurante não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando name for blank")
    void deveLancarExcecaoQuandoNameForBlank() {
        // Arrange
        var owner = new UserBuilder().build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        // Act & Assert
        assertThatThrownBy(() -> new Restaurant(1L, "  ", address, "Italiana", openingHours, menu, owner))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O nome do restaurante não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando address for nulo")
    void deveLancarExcecaoQuandoAddressForNulo() {
        // Arrange
        var owner = new UserBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        // Act & Assert
        assertThatThrownBy(() -> new Restaurant(1L, "Macarronada com massa da casa", null, "Italiana", openingHours, menu, owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O endereço não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando cuisineType for nulo")
    void deveLancarExcecaoQuandoCuisineTypeNull() {
        // Arrange - caso nulo
        var owner = new UserBuilder().build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();

        var menu = getMenuItems();

        // Act & Assert - caso nulo
        assertThatThrownBy(() -> new Restaurant(1L, "Restaurante Teste", address, null, openingHours, menu, owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O tipo de cozinha não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando cuisineType for nulo ou blank")
    void deveLancarExcecaoQuandoCuisineTypeEspacoEmBranco() {
        // Arrange - caso nulo
        var owner = new UserBuilder().build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        // Act & Assert - caso blank
        assertThatThrownBy(() -> new Restaurant(1L, "Restaurante Teste", address, " ", openingHours, menu, owner))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O tipo de cozinha não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve permitir coleções nulas ou vazias")
    void devePermitirColecoesNulasOuVazias() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var address = new AddressBuilder().build();

        // Act
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", Set.of(), Set.of(), owner);

        // Assert
        assertThat(restaurant.getOpeningHours()).isNotNull().isEmpty();
        assertThat(restaurant.getMenu()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve retornar true para equals com mesmo objeto")
    void deveSerIgualParaMesmoObjeto() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var address = new AddressBuilder().build();

        // Act
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", Set.of(), Set.of(), owner);

        // Act & Assert
        assertThat(restaurant).isEqualTo(restaurant);
    }

    @Test
    @DisplayName("Deve retornar false para equals com objeto de outra classe")
    void deveSerDiferenteParaOutraClasse() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", openingHours, menu, owner);

        // Act & Assert
        assertThat(restaurant)
                .isNotEqualTo("Outra coisa")
                .isNotEqualTo(null);
    }

    @Test
    @DisplayName("Deve retornar true para equals quando ids são iguais")
    void deveSerIgualQuandoIdsIguais() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        var r1 = new Restaurant(1L, "Restaurante Teste", address, "Italiana", openingHours, menu, owner);
        var r2 = new Restaurant(1L, "Restaurante Teste Renomeado", address, "Italiana", openingHours, menu, owner);

        // Act & Assert
        assertThat(r1).isEqualTo(r2).hasSameHashCodeAs(r2);
    }

    @Test
    @DisplayName("Deve retornar false para equals quando ids diferentes ou mistos")
    void deveSerDiferenteQuandoIdsDiferentesOuMistos() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        var r1 = new Restaurant(1L, "Restaurante Teste", address, "Italiana", openingHours, menu, owner);
        var r2 = new Restaurant(2L, "Restaurante Teste II", address, "Italiana", openingHours, menu, owner);
        var r3 = new Restaurant(null,"Restaurante Salvo ainda", address, "Italiana", openingHours, menu, owner);

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2).isNotEqualTo(r3);
        assertThat(r2).isNotEqualTo(r1).isNotEqualTo(r3);
    }

    @Test
    @DisplayName("Deve respeitar contrato equals com id nulo")
    void deveRespeitarContratoComIdNulo() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        var r1 = new Restaurant(null, "Restaurante Teste", address, "Italiana", openingHours, menu, owner);
        var r2 = new Restaurant(null, "Restaurante Teste II", address, "Italiana", openingHours, menu, owner);

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2).isEqualTo(r1);
    }

    @Test
    @DisplayName("Deve considerar diferentes quando um tem id nulo e outro não")
    void deveSerDiferenteQuandoUmIdNuloEOutroNao() {
        // Arrange
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var address = new AddressBuilder().build();
        var openingHours = getOpeningHours();
        var menu = getMenuItems();

        var restaurantWithId = new Restaurant(1L, "Restaurante Teste", address, "Italiana", openingHours, menu, owner);
        var restaurantWithoutId = new Restaurant(null, "Restaurante Teste II", address, "Italiana", openingHours, menu, owner);
        // Act & Assert
        assertThat(restaurantWithId).isNotEqualTo(restaurantWithoutId);
        assertThat(restaurantWithoutId).isNotEqualTo(restaurantWithId);

        // hashCode deve ser diferente (um usa id, outro usa 0)
        assertThat(restaurantWithId.hashCode()).isNotEqualTo(restaurantWithoutId.hashCode());
    }

    private static @NonNull Set<MenuItem> getMenuItems() {
        return Set.of(new MenuItem(1L, "Macarronada", "Macarronada com massa da casa", new BigDecimal("95"), false, "/macarronada-casa.jpg"));
    }

    private static @NonNull Set<OpeningHours> getOpeningHours() {
        return Set.of(
                new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(20, 30)),
                new OpeningHours(2L, DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(3L, DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(4L, DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(5L, DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(22, 0)),
                new OpeningHours(6L, DayOfWeek.SATURDAY, LocalTime.of(11, 0), LocalTime.of(22, 0))
        );
    }
}