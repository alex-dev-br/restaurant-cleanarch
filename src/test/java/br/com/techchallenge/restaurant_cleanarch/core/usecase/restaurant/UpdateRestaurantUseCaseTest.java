package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.OpeningHoursBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateOpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateRestaurantUseCase")
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
    private ArgumentCaptor<Restaurant> restaurantCaptor;

    @Test
    @DisplayName("Deve atualizar restaurante e substituir coleções quando fornecidas")
    void shouldUpdateRestaurantReplacingCollectionsWhenProvided() {
        // Arrange
        Long restaurantId = 1L;

        Address oldAddress = new AddressBuilder().build();

        User oldOwner = new UserBuilder()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        UUID oldEmployeeId = UUID.randomUUID();
        User oldEmployee = new UserBuilder()
                .withId(oldEmployeeId)
                .build();

        Restaurant current = new RestaurantBuilder()
                .withId(restaurantId)
                .withName("Old Name")
                .withAddress(oldAddress)
                .withCuisineType("Old Cuisine")
                .withOwner(oldOwner)
                .withEmployee(Set.of(oldEmployee))
                .build();

        UUID newOwnerId = UUID.randomUUID();
        User newOwner = new UserBuilder()
                .withId(newOwnerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        UUID newEmployeeId = UUID.randomUUID();
        User newEmployee = new UserBuilder()
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
        given(restaurantGateway.existsRestaurantWithName("New Name")).willReturn(false);
        given(userGateway.findById(newOwnerId)).willReturn(Optional.of(newOwner));
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
    }

    @Test
    @DisplayName("Deve manter employees atuais quando employees no input for null")
    void shouldKeepEmployeesWhenEmployeesIsNull() {
        // Arrange
        Long restaurantId = 1L;

        User owner = new UserBuilder()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        UUID employeeId = UUID.randomUUID();
        User employee = new UserBuilder()
                .withId(employeeId)
                .build();

        Restaurant current = new RestaurantBuilder()
                .withId(restaurantId)
                .withName("Old Name")
                .withAddress(new AddressBuilder().build())
                .withCuisineType("Old Cuisine")
                .withOwner(owner)
                .withEmployee(Set.of(employee))
                .build();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Old Name",
                new AddressBuilder().buildInput(),
                "Old Cuisine",
                null,
                null,
                null, // employees null => mantém
                owner.getId()
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(userGateway.findById(owner.getId())).willReturn(Optional.of(owner));

        // Act
        updateRestaurantUseCase.execute(input);

        // Assert
        then(userGateway).should(times(1)).findById(owner.getId());
        then(userGateway).shouldHaveNoMoreInteractions();

        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getEmployees()).containsExactlyInAnyOrder(employee);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando employee não existir e não salvar")
    void shouldThrowWhenEmployeeNotFound() {
        // Arrange
        Long restaurantId = 1L;

        UUID ownerId = UUID.randomUUID();
        User owner = new UserBuilder()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        Restaurant current = new RestaurantBuilder()
                .withId(restaurantId)
                .withName("Old Name")
                .withAddress(new AddressBuilder().build())
                .withCuisineType("Old Cuisine")
                .withOwner(owner)
                .withEmployee(Set.of())
                .build();

        UUID missingEmployeeId = UUID.randomUUID();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Old Name",
                new AddressBuilder().buildInput(),
                "Old Cuisine",
                null,
                null,
                Set.of(missingEmployeeId),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(missingEmployeeId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee " + missingEmployeeId + " not found.");

        then(restaurantGateway).should().findById(restaurantId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        // Arrange
        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(
                new UpdateRestaurantInput(1L, "x", null, "y", null, null, null, null)
        ))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action.");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve permitir owner estar em employees e evitar dupla consulta via cache")
    void shouldAllowOwnerToBeInEmployees() {
        // Arrange
        Long restaurantId = 1L;

        UUID ownerId = UUID.randomUUID();
        User owner = new UserBuilder()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        Restaurant current = new RestaurantBuilder()
                .withId(restaurantId)
                .withName("Old Name")
                .withAddress(new AddressBuilder().build())
                .withCuisineType("Old Cuisine")
                .withOwner(owner)
                .withEmployee(Set.of()) // tanto faz aqui
                .build();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                Set.of(ownerId), // owner tb em employees
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));

        // Act
        updateRestaurantUseCase.execute(input);

        // Assert
        then(userGateway).should(times(1)).findById(ownerId); // cache evita 2x
        then(userGateway).shouldHaveNoMoreInteractions();

        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getEmployees()).contains(owner);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for null (UseCaseBase)")
    void shouldThrowWhenInputIsNull() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }
}
