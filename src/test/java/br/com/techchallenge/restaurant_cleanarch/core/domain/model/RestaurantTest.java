package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para Restaurant")
class RestaurantTest {

    private Long restaurantId;
    private String restaurantName;
    private String cuisineType;
    private User owner;
    private Address address;
    private Set<OpeningHours> openingHours;
    private Set<MenuItem> menu;
    private User employee;

    @BeforeEach
    void setUp() {
        restaurantId = 14L;
        restaurantName = "Restaurante Teste";
        cuisineType = "Italiana";
        owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        address = new AddressBuilder().build();
        openingHours = buildSetOfOpeningHours();
        menu = buildSetOfMenuItems();
        employee = new UserBuilder().build();
    }

    @Test
    @DisplayName("Deve criar Restaurant válido com campos preenchidos")
    void deveCriarRestaurantValido() {
        // Act
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItems(menu);
        restaurant.addEmployee(employee);

        // Assert
        assertThat(restaurant.getId()).isNotNull().isEqualTo(restaurantId);
        assertThat(restaurant.getName()).isNotNull().isEqualTo(restaurantName);
        assertThat(restaurant.getCuisineType()).isNotNull().isEqualTo(cuisineType);
        assertThat(restaurant.getOwner()).isNotNull().isEqualTo(owner);
        assertThat(restaurant.getOwner().getName()).isNotNull().isEqualTo(owner.getName());
        assertThat(restaurant.getOwner().getEmail()).isNotNull().isEqualTo(owner.getEmail());
        assertThat(restaurant.getOpeningHours()).isNotNull().hasSize(6).containsExactlyInAnyOrderElementsOf(openingHours);
        assertThat(restaurant.getMenuItems()).isNotNull().hasSize(1);
        assertThat(restaurant.getEmployees()).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve criar Restaurant válido e adicionar um OpenHours")
    void deveCriarRestaurantEAdicionaOpenHours() {
        // Act
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        restaurant.addOpeningHours(openingHours);
        // Assert
        assertThat(restaurant.getId()).isNotNull().isEqualTo(restaurantId);
        assertThat(restaurant.getName()).isNotNull().isEqualTo(restaurantName);
        assertThat(restaurant.getCuisineType()).isNotNull().isEqualTo(cuisineType);
        assertThat(restaurant.getOwner()).isNotNull().isEqualTo(owner);
        assertThat(restaurant.getOwner().getName()).isNotNull().isEqualTo(owner.getName());
        assertThat(restaurant.getOwner().getEmail()).isNotNull().isEqualTo(owner.getEmail());
        assertThat(restaurant.getOpeningHours()).isNotNull().hasSize(6).containsExactlyInAnyOrderElementsOf(openingHours);
    }

    @Test
    @DisplayName("Deve criar Restaurant válido e adicionar MenuItems")
    void deveCriarRestaurantEAdicionaMenuItems() {
        // Act
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        restaurant.addMenuItems(menu);
        // Assert
        assertThat(restaurant.getId()).isNotNull().isEqualTo(restaurantId);
        assertThat(restaurant.getName()).isNotNull().isEqualTo(restaurantName);
        assertThat(restaurant.getCuisineType()).isNotNull().isEqualTo(cuisineType);
        assertThat(restaurant.getOwner()).isNotNull().isEqualTo(owner);
        assertThat(restaurant.getOwner().getName()).isNotNull().isEqualTo(owner.getName());
        assertThat(restaurant.getOwner().getEmail()).isNotNull().isEqualTo(owner.getEmail());
        assertThat(restaurant.getMenuItems()).isNotNull().hasSize(1).containsExactlyInAnyOrderElementsOf(menu);
    }

    @Test
    @DisplayName("Deve criar Restaurant válido e adicionar Employee")
    void deveCriarRestaurantAdicionaEmployee() {
        // Act
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        restaurant.addEmployee(employee);
        // Assert
        assertThat(restaurant.getId()).isNotNull().isEqualTo(restaurantId);
        assertThat(restaurant.getName()).isNotNull().isEqualTo(restaurantName);
        assertThat(restaurant.getCuisineType()).isNotNull().isEqualTo(cuisineType);
        assertThat(restaurant.getOwner()).isNotNull().isEqualTo(owner);
        assertThat(restaurant.getOwner().getName()).isNotNull().isEqualTo(owner.getName());
        assertThat(restaurant.getOwner().getEmail()).isNotNull().isEqualTo(owner.getEmail());
        assertThat(restaurant.getEmployees()).isNotNull().hasSize(1).containsExactlyInAnyOrder(employee);
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono válido")
    void deveLancarExcecaoSemDonoValido() {
        UserType clienteType = new UserType(1L, "Cliente", Set.of(new Role(1L, RestaurantRoles.VIEW_RESTAURANT.getRoleName())));
        User invalidOwner = new UserBuilder().withUserType(clienteType).build();

        // Act & Assert
        assertThatThrownBy(() -> new Restaurant(restaurantId, restaurantName, address, cuisineType, invalidOwner))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class)
                .hasMessageContaining("User cannot be restaurant owner");
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono")
    void deveLancarExcecaoSemDono() {
        // Act & Assert
        assertThatThrownBy(() -> new Restaurant(restaurantId, restaurantName, address, cuisineType, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O dono do restaurante não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando name for nulo")
    void deveLancarExcecaoQuandoNameForNulo() {
        // Act & Assert
        assertThatThrownBy(() -> new Restaurant(restaurantId, null, address, cuisineType, owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O nome do restaurante não pode ser nulo");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("Deve lançar exceção quando name for blank")
    void deveLancarExcecaoQuandoNameForBlank(String name) {
        // Act & Assert
        assertThatThrownBy(() -> new Restaurant(restaurantId, name, address, cuisineType, owner))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O nome do restaurante não pode ser vazio");
    }

    @Test
    @DisplayName("Deve lançar exceção quando address for nulo")
    void deveLancarExcecaoQuandoAddressForNulo() {
        assertThatThrownBy(() -> new Restaurant(restaurantId, restaurantName, null, cuisineType, owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O endereço não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando cuisineType for nulo")
    void deveLancarExcecaoQuandoCuisineTypeNull() {
        // Act & Assert - caso nulo
        assertThatThrownBy(() -> new Restaurant(restaurantId, restaurantName, address, null, owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O tipo de cozinha não pode ser nulo");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("Deve lançar exceção quando cuisineType for vazio ou espaço em branco")
    void deveLancarExcecaoQuandoCuisineTypeEspacoEmBranco(String emptyCuisineType) {
        assertThatThrownBy(() -> new Restaurant(restaurantId, restaurantName, address, emptyCuisineType, owner))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O tipo de cozinha não pode ser vazio");
    }

    @Test
    @DisplayName("Deve permitir coleções nulas ou vazias")
    void devePermitirColecoesNulasOuVazias() {
        Restaurant restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);

        assertThat(restaurant.getOpeningHours()).isNotNull().isEmpty();
        assertThat(restaurant.getMenuItems()).isNotNull().isEmpty();
        assertThat(restaurant.getEmployees()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve retornar true para equals com mesmo objeto")
    void deveSerIgualParaMesmoObjeto() {
        var baseRestaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        var firstRestaurant = baseRestaurant;
        var secondRestaurant = baseRestaurant;

        // Act & Assert
        assertThat(firstRestaurant).isEqualTo(secondRestaurant).hasSameHashCodeAs(secondRestaurant);
    }

    @Test
    @DisplayName("Deve retornar false para equals com objeto de outra classe")
    void deveSerDiferenteParaOutraClasse() {
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);

        // Act & Assert
        assertThat(restaurant).isNotEqualTo(new Object());
    }

    @Test
    @DisplayName("Deve retornar true para equals quando ids são iguais")
    void deveSerIgualQuandoIdsIguais() {
        var r1 = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        var r2 = new Restaurant(restaurantId, restaurantName.concat("II"), address, cuisineType, owner);

        // Act & Assert
        assertThat(r1).isEqualTo(r2).hasSameHashCodeAs(r2);
    }

    @Test
    @DisplayName("Deve retornar false para equals quando ids diferentes ou mistos")
    void deveSerDiferenteQuandoIdsDiferentesOuMistos() {
        var r1 = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        var r2 = new Restaurant(restaurantId+1, restaurantName, address, cuisineType, owner);
        var r3 = new Restaurant(null, restaurantName, address, cuisineType, owner);

        assertThat(r1).isNotEqualTo(r2).isNotEqualTo(r3);
        assertThat(r2).isNotEqualTo(r1).isNotEqualTo(r3);
    }

    @Test
    @DisplayName("Deve respeitar contrato equals com id nulo")
    void deveRespeitarContratoComIdNulo() {
        var r1 = new Restaurant(null, restaurantName, address, cuisineType, owner);
        var r2 = new Restaurant(null, restaurantName.concat(" II"), address, cuisineType, owner);

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2).doesNotHaveSameHashCodeAs(r2);
    }

    private Set<MenuItem> buildSetOfMenuItems() {
        return Set.of(
                new MenuItemBuilder()
                        .withId(1L)
                        .withName("Macarronada")
                        .withDescription("Macarronada com massa da casa")
                        .withPrice(new BigDecimal("95"))
                        .withRestaurantOnly(false)
                        .withPhotoPath("/macarronada-casa.jpg")
                        .build()
        );
    }

    private Set<OpeningHours> buildSetOfOpeningHours() {
        return Set.of(
                new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(20, 30)),
                new OpeningHours(2L, DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(3L, DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(4L, DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(5L, DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(22, 0)),
                new OpeningHours(6L, DayOfWeek.SATURDAY, LocalTime.of(11, 0), LocalTime.of(22, 0))
        );
    }


    @Test
    @DisplayName("canBeManagedBy deve retornar false quando currentUser for null")
    void canBeManagedBy_deveRetornarFalse_quandoCurrentUserForNull() {
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);

        assertThat(restaurant.canBeManagedBy(null)).isFalse();
    }

    @Test
    @DisplayName("canBeManagedBy deve retornar true quando currentUser for o owner")
    void canBeManagedBy_deveRetornarTrue_quandoCurrentUserForOwner() {
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);

        assertThat(restaurant.canBeManagedBy(owner)).isTrue();
    }

    @Test
    @DisplayName("canBeManagedBy deve retornar true quando currentUser estiver na lista de employees")
    void canBeManagedBy_deveRetornarTrue_quandoCurrentUserForEmployee() {
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        restaurant.addEmployee(employee);

        assertThat(restaurant.canBeManagedBy(employee)).isTrue();
    }

    @Test
    @DisplayName("canBeManagedBy deve retornar false quando currentUser não for owner nem employee")
    void canBeManagedBy_deveRetornarFalse_quandoCurrentUserNaoTiverPermissao() {
        var restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        var outsider = new UserBuilder().build();

        assertThat(restaurant.canBeManagedBy(outsider)).isFalse();
    }

    @Test
    @DisplayName("equals deve retornar false quando this.id for null e other.id não for null (lado inverso)")
    void equals_deveRetornarFalse_quandoThisIdNullEThatIdNaoNull() {
        var withId = new Restaurant(restaurantId, restaurantName, address, cuisineType, owner);
        var withoutId = new Restaurant(null, restaurantName, address, cuisineType, owner);

        assertThat(withoutId).isNotEqualTo(withId); // <-- lado inverso que costuma faltar
    }
}