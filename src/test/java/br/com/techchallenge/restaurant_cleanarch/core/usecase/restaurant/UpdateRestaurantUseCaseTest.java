package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.AddressInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateOpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateRestaurantUseCase")
class UpdateRestaurantUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private RestaurantGateway restaurantGateway;
    @Mock private UserGateway userGateway;

    @InjectMocks
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Captor
    private ArgumentCaptor<Restaurant> restaurantCaptor;

    private Long restaurantId;

    private UUID ownerId;
    private User owner;

    private UUID oldEmployeeId;
    private User oldEmployee;

    private Restaurant current;

    @BeforeEach
    void setUp() {
        // Arrange
        restaurantId = 1L;

        ownerId = UUID.randomUUID();
        owner = new UserBuilder()
                .withDefaults()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        oldEmployeeId = UUID.randomUUID();
        oldEmployee = new UserBuilder()
                .withDefaults()
                .withId(oldEmployeeId)
                .build();

        current = new RestaurantBuilder()
                .withDefaults()
                .withId(restaurantId)
                .withName("Old Name")
                .withAddress(new AddressBuilder().build())
                .withCuisineType("Old Cuisine")
                .withOwner(owner)
                .withEmployees(Set.of(oldEmployee))
                .build();

        // deixa o restaurante “realista” (não é obrigatório para o use case, mas ajuda)
        current.addOpeningHours(new OpeningHoursBuilder().build());
        current.addMenuItem(new MenuItemBuilder().build());
    }

    @Test
    @DisplayName("Deve atualizar restaurante e substituir coleções quando fornecidas")
    void shouldUpdateRestaurantReplacingCollectionsWhenProvided() {
        // Arrange
        UUID newOwnerId = UUID.randomUUID();
        User newOwner = new UserBuilder()
                .withDefaults()
                .withId(newOwnerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        UUID newEmployeeId = UUID.randomUUID();
        User newEmployee = new UserBuilder()
                .withDefaults()
                .withId(newEmployeeId)
                .build();

        UpdateOpeningHoursInput openingHoursInput = new OpeningHoursBuilder().buildUpdateInput();
        UpdateMenuItemInput menuItemInput = new MenuItemBuilder().buildUpdateInput();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "New Name",
                new AddressBuilder().buildInput(),
                "New Cuisine",
                Set.of(openingHoursInput),
                Set.of(menuItemInput),
                Set.of(newEmployeeId),
                newOwnerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner); // pode gerenciar (é dono)
        given(userGateway.findById(newOwnerId)).willReturn(Optional.of(newOwner));
        given(restaurantGateway.existsRestaurantWithName("New Name")).willReturn(false);
        given(userGateway.findById(newEmployeeId)).willReturn(Optional.of(newEmployee));

        // Act
        updateRestaurantUseCase.execute(input);

        // Assert
        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getId()).isEqualTo(restaurantId);
        assertThat(saved.getName()).isEqualTo("New Name");
        assertThat(saved.getCuisineType()).isEqualTo("New Cuisine");
        assertThat(saved.getOwner()).isEqualTo(newOwner);

        assertThat(saved.getEmployees()).containsExactlyInAnyOrder(newEmployee);
        assertThat(saved.getOpeningHours()).hasSize(1);
        assertThat(saved.getMenuItems()).hasSize(1);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(userGateway).should().findById(newOwnerId);
        then(restaurantGateway).should().existsRestaurantWithName("New Name");
        then(userGateway).should().findById(newEmployeeId);
    }

    @Test
    @DisplayName("Deve manter employees atuais quando employees no input for null (PATCH semantics)")
    void shouldKeepEmployeesWhenEmployeesIsNull() {
        // Arrange
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                null, // employees null => mantém
                null  // ownerId null => mantém ownerId atual
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));

        // Act
        updateRestaurantUseCase.execute(input);

        // Assert
        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getEmployees()).containsExactlyInAnyOrder(oldEmployee);
        assertThat(saved.getOwner()).isEqualTo(owner);

        // nome não mudou -> NÃO deve consultar existsRestaurantWithName
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());

        // employees null -> NÃO consulta employee no gateway
        then(userGateway).should(times(1)).findById(ownerId);
        then(userGateway).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando employee não existir e não salvar")
    void shouldThrowWhenEmployeeNotFound() {
        // Arrange
        UUID missingEmployeeId = UUID.randomUUID();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                Set.of(missingEmployeeId),
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(missingEmployeeId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee " + missingEmployeeId + " not found.");

        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        // Arrange
        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(false);

        // Act & Assert
        var updateRestaurantInput = new UpdateRestaurantInput(restaurantId, "x", null, "y", null, null, null, null);
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(updateRestaurantInput))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("permission to perform this action");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário logado não é ownerId nem employee")
    void shouldThrowOperationNotAllowedWhenCurrentUserCannotManage() {
        // Arrange
        User outsider = new UserBuilder().withDefaults().withId(UUID.randomUUID()).build();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Any",
                null,
                null,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(outsider);

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("permission to perform this action");

        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar RestaurantNameIsAlreadyInUseException quando nome mudar e já existir")
    void shouldThrowWhenRestaurantNameAlreadyInUse() {
        // Arrange
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "New Name",
                null,
                null,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.existsRestaurantWithName("New Name")).willReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class);

        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando novo ownerId não for encontrado")
    void shouldThrowWhenNewOwnerNotFound() {
        // Arrange
        UUID newOwnerId = UUID.randomUUID();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                null,
                newOwnerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(newOwnerId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Owner not found.");

        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar UserCannotBeRestaurantOwnerException quando novo ownerId não pode ser dono")
    void shouldThrowWhenUserCannotBeOwner() {
        // Arrange
        UUID newOwnerId = UUID.randomUUID();
        User newOwner = new UserBuilder().withDefaults().withId(newOwnerId).build(); // sem role de ownerId

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                null,
                newOwnerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(newOwnerId)).willReturn(Optional.of(newOwner));

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class);

        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for null (UseCaseBase)")
    void shouldThrowWhenInputIsNull() {
        // Arrange / Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não for encontrado (cobre lambda do orElseThrow)")
    void shouldThrowWhenRestaurantNotFound() {
        // Arrange
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Any",
                null,
                null,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurant not found.");

        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve reutilizar cache quando ownerId também está na lista de employees (não consulta userGateway 2x)")
    void shouldNotQueryUserGatewayTwiceWhenOwnerIsAlsoEmployee() {
        // Arrange
        UUID sameOwnerId = owner.getId();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                Set.of(sameOwnerId), // employee == ownerId (cache)
                sameOwnerId          // novo ownerId == ownerId atual
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(sameOwnerId)).willReturn(Optional.of(owner)); // só 1 vez

        // Act
        updateRestaurantUseCase.execute(input);

        // Assert
        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getOwner().getId()).isEqualTo(sameOwnerId);
        assertThat(saved.getEmployees()).contains(owner); // agora employee inclui ownerId

        then(userGateway).should(times(1)).findById(sameOwnerId);
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
    }

    @Test
    @DisplayName("Deve retornar null nos métodos build* quando input for null (cobre branches)")
    void shouldReturnNullWhenCallingPrivateBuildersWithNullInput() throws Exception {
        // Arrange
        Method buildAddress = UpdateRestaurantUseCase.class.getDeclaredMethod("buildAddress", AddressInput.class);
        buildAddress.setAccessible(true);

        Method buildMenu = UpdateRestaurantUseCase.class.getDeclaredMethod("buildMenu", UpdateMenuItemInput.class);
        buildMenu.setAccessible(true);

        Method buildOpeningHours = UpdateRestaurantUseCase.class.getDeclaredMethod("buildOpeningHours", UpdateOpeningHoursInput.class);
        buildOpeningHours.setAccessible(true);

        // Act
        Address address = (Address) buildAddress.invoke(updateRestaurantUseCase, new Object[]{null});
        MenuItem menuItem = (MenuItem) buildMenu.invoke(updateRestaurantUseCase, new Object[]{null});
        OpeningHours openingHours = (OpeningHours) buildOpeningHours.invoke(updateRestaurantUseCase, new Object[]{null});

        // Assert
        assertThat(address).isNull();
        assertThat(menuItem).isNull();
        assertThat(openingHours).isNull();
    }

    @Test
    @DisplayName("Deve construir objetos corretamente nos métodos build* via reflection (cobre branches não-nulos)")
    void shouldBuildObjectsWhenCallingPrivateBuildersWithNonNullInput() throws Exception {
        // Arrange
        AddressInput addressInput = new AddressBuilder().withStreet("Street X").buildInput();
        UpdateMenuItemInput menuInput = new MenuItemBuilder().withId(99L).withName("Dish X").buildUpdateInput();
        UpdateOpeningHoursInput hoursInput = new OpeningHoursBuilder().withId(77L).buildUpdateInput();

        Method buildAddress = UpdateRestaurantUseCase.class.getDeclaredMethod("buildAddress", AddressInput.class);
        buildAddress.setAccessible(true);

        Method buildMenu = UpdateRestaurantUseCase.class.getDeclaredMethod("buildMenu", UpdateMenuItemInput.class);
        buildMenu.setAccessible(true);

        Method buildOpeningHours = UpdateRestaurantUseCase.class.getDeclaredMethod("buildOpeningHours", UpdateOpeningHoursInput.class);
        buildOpeningHours.setAccessible(true);

        // Act
        Address address = (Address) buildAddress.invoke(updateRestaurantUseCase, addressInput);
        MenuItem menuItem = (MenuItem) buildMenu.invoke(updateRestaurantUseCase, menuInput);
        OpeningHours openingHours = (OpeningHours) buildOpeningHours.invoke(updateRestaurantUseCase, hoursInput);

        // Assert
        assertThat(address).isNotNull();
        assertThat(address.getStreet()).isEqualTo("Street X");

        assertThat(menuItem).isNotNull();
        assertThat(menuItem.getId()).isEqualTo(99L);
        assertThat(menuItem.getName()).isEqualTo("Dish X");

        assertThat(openingHours).isNotNull();
        assertThat(openingHours.getId()).isEqualTo(77L);
        assertThat(openingHours.getDayOfWeek()).isEqualTo(hoursInput.dayOfWeek());
    }
}
