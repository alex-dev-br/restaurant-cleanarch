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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRestaurantUseCaseTest {

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Captor
    private ArgumentCaptor<Restaurant> updateRestaurantInputCaptor;

    private UUID employeeId;
    private UUID ownerUuid;
    private Long restaurantId;
    private Restaurant oldRestaurant;
    private User owner;
    private Set<UpdateMenuItemInput> menuItemsInput;
    private Set<UpdateOpeningHoursInput> openingHoursInput;
    private Set<OpeningHours> openingHours;
    private Set<MenuItem> menuItems;
    private User employee;
    private AddressInput addressInput;
    private Address address;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        ownerUuid = UUID.randomUUID();
        restaurantId = 1L;

        var addressBuilder = new AddressBuilder();
        var openingHoursBuilder = new OpeningHoursBuilder();
        var menuItemBuilder = new MenuItemBuilder();
        var employeeBuilder = new UserBuilder().withId(employeeId);

        address = addressBuilder.build();
        addressInput = addressBuilder.buildInput();

        openingHours = Set.of(openingHoursBuilder.build());
        openingHoursInput = Set.of(openingHoursBuilder.buildUpdateInput());
        menuItems = Set.of(menuItemBuilder.build());
        menuItemsInput = Set.of(menuItemBuilder.buildUpdateInput());

        employee = employeeBuilder.build();
        owner = new UserBuilder().withId(ownerUuid).withRole(UserRoles.RESTAURANT_OWNER).build();

        oldRestaurant = new Restaurant(restaurantId, "Old Name", address, "Old Cuisine", owner);
        oldRestaurant.addOpeningHours(openingHoursBuilder.build());
        oldRestaurant.addMenuItem(menuItemBuilder.build());
        oldRestaurant.addEmployee(employeeBuilder.build());
    }

    @Test
    @DisplayName("Deve atualizar restaurante com sucesso")
    void shouldUpdateRestaurantSuccessfully() {
        // Given
        String newRestaurantName = "New Name";
        String newRestaurantCuisineType = "New Cuisine";

        var input = new UpdateRestaurantInput(
                restaurantId,
                newRestaurantName,
                addressInput,
                newRestaurantCuisineType,
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                owner.getId()
        );

        when(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).thenReturn(true);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(oldRestaurant));
        when(userGateway.findById(ownerUuid)).thenReturn(Optional.of(owner));
        when(userGateway.findById(employeeId)).thenReturn(Optional.of(employee));

        // When
        updateRestaurantUseCase.execute(input);

        // Then
        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(userGateway).should().findById(ownerUuid);
        then(userGateway).should().findById(employeeId);
        then(restaurantGateway).should().save(updateRestaurantInputCaptor.capture());

        var updatedRestaurant = updateRestaurantInputCaptor.getValue();

        assertThat(updatedRestaurant).isNotNull();
        assertThat(updatedRestaurant.getId()).isNotNull().isEqualTo(restaurantId);
        assertThat(updatedRestaurant.getName()).isEqualTo(newRestaurantName);
        assertThat(updatedRestaurant.getAddress()).isEqualTo(address);
        assertThat(updatedRestaurant.getCuisineType()).isEqualTo(newRestaurantCuisineType);
        assertThat(updatedRestaurant.getOpeningHours()).hasSize(1).containsExactlyInAnyOrderElementsOf(openingHours);
        assertThat(updatedRestaurant.getMenu()).hasSize(1).containsExactlyInAnyOrderElementsOf(menuItems);
        assertThat(updatedRestaurant.getEmployees()).hasSize(1).containsExactlyInAnyOrder(employee);
        assertThat(updatedRestaurant.getOwner()).isEqualTo(owner);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        var input = new UpdateRestaurantInput(
                restaurantId,
                "New Name",
                addressInput,
                "New Cuisine",
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                owner.getId()
        );
        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should(never()).findById(anyLong());
        then(userGateway).should(never()).findById(any(UUID.class));
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando restaurante não é encontrado")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        long invalidRestaurant = 100000L;
        var input = new UpdateRestaurantInput(
                invalidRestaurant,
                "New Name",
                addressInput,
                "New Cuisine",
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                owner.getId()
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(invalidRestaurant)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurant not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(anyLong());
        then(userGateway).should(never()).findById(any(UUID.class));
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any());
    }
    @Test
    @DisplayName("Deve lançar exceção quando dono não é encontrado")
    void shouldThrowExceptionWhenNewOwnerNotFound() {
        UUID newOwnerUuid = UUID.randomUUID();
        String newRestaurantName = "New Name";
        var input = new UpdateRestaurantInput(
                restaurantId,
                newRestaurantName,
                addressInput,
                "New Cuisine",
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                newOwnerUuid
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(oldRestaurant));
        given(userGateway.findById(newOwnerUuid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Owner not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(userGateway).should().findById(newOwnerUuid);
        then(restaurantGateway).should(never()).existsRestaurantWithName(newRestaurantName);
        then(userGateway).should(never()).findById(employeeId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando funcionário não é encontrado")
    void shouldThrowExceptionWhenEmployeeNotFound() {
        String newRestaurantName = "New Name";
        var input = new UpdateRestaurantInput(
                restaurantId,
                newRestaurantName,
                addressInput,
                "New Cuisine",
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                owner.getId()
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(oldRestaurant));
        given(userGateway.findById(ownerUuid)).willReturn(Optional.of(owner));
        given(restaurantGateway.existsRestaurantWithName(newRestaurantName)).willReturn(false);
        given(userGateway.findById(employeeId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee "+ employeeId +" not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(restaurantGateway).should().existsRestaurantWithName(newRestaurantName);
        then(userGateway).should().findById(ownerUuid);
        then(userGateway).should().findById(employeeId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quanto tenta alterar o nome para que já está sendo utilizado")
    void shouldThrowExceptionWhenRestaurantNameAlreadyInUseByOtherId() {
        String restaurantName = "New Restaurant Name";
        var input = new UpdateRestaurantInput(
                restaurantId,
                restaurantName,
                addressInput,
                "New Cuisine",
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                owner.getId()
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(userGateway.findById(ownerUuid)).willReturn(Optional.of(owner));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(oldRestaurant));
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(true);

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class)
                .hasMessageContaining("Restaurant name is already in use");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(userGateway).should().findById(ownerUuid);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should(never()).findById(employeeId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Não Deve chamar validar nome do restaurante se nome não for alterado")
    void shouldNotCallTheGatewayToValidateTheRestaurantNameIfTheNameHasNotBeenChanged() {
        String restaurantName = oldRestaurant.getName();
        var input = new UpdateRestaurantInput(
                restaurantId,
                restaurantName,
                addressInput,
                "New Cuisine",
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                owner.getId()
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(oldRestaurant));
        given(userGateway.findById(ownerUuid)).willReturn(Optional.of(owner));
        given(userGateway.findById(employeeId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee "+ employeeId +" not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(userGateway).should().findById(ownerUuid);
        then(userGateway).should().findById(employeeId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Dele lançar exceção quando o dono sendo alterado não tem role de dono")
    void shouldThrowExceptionWhenUserCannotBeOwner() {
        UUID newOwnerUuid = UUID.randomUUID();
        var newOwner = new UserBuilder().withId(newOwnerUuid).build();

        String newRestaurantName = "New Name";
        var input = new UpdateRestaurantInput(
                restaurantId,
                newRestaurantName,
                addressInput,
                "New Cuisine",
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                newOwnerUuid
        );

        when(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).thenReturn(true);
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(oldRestaurant));
        when(restaurantGateway.existsRestaurantWithName(newRestaurantName)).thenReturn(false);
        when(userGateway.findById(newOwnerUuid)).thenReturn(Optional.of(newOwner));

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class)
                .hasMessageContaining("User cannot be restaurant owner");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(userGateway).should().findById(newOwnerUuid);
        then(restaurantGateway).should().existsRestaurantWithName(newRestaurantName);
        then(userGateway).should(never()).findById(employeeId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando input é nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(restaurantGateway).should(never()).findById(anyLong());
        then(userGateway).should(never()).findById(any(UUID.class));
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any());
    }
}
