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
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.MenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.OpeningHoursInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateRestaurantUseCase")
class CreateRestaurantUseCaseTest {

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private CreateRestaurantUseCase createRestaurantUseCase;

    @Captor
    private ArgumentCaptor<Restaurant> restaurantCaptor;

    private UUID ownerId;
    private Long restaurantId;
    private AddressInput addressInput;
    private Address address;
    private Set<OpeningHours> openingHours;
    private Set<OpeningHoursInput> openingHoursInput;
    private Set<MenuItem> menuItems;
    private Set<MenuItemInput> menuItemsInput;
    private User owner;
    private UUID employeeUuid;
    private User employee;
    private Set<User> employees;
    private String restaurantName;
    private String cuisineType;


    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        restaurantId = 1L;
        var addressBuilder = new AddressBuilder();
        var openingHoursBuilder = new OpeningHoursBuilder();

        addressInput = addressBuilder.buildInput();
        address = addressBuilder.build();

        OpeningHoursInput openingHoursFridayInput = openingHoursBuilder.buildInput();
        OpeningHours openingHoursFriday = openingHoursBuilder.build();
        OpeningHoursInput openingHoursTuesdayInput = openingHoursBuilder.withDayOfDay(DayOfWeek.TUESDAY).buildInput();
        OpeningHours openingHoursTuesday = openingHoursBuilder.build();
        openingHours = Set.of(openingHoursTuesday, openingHoursFriday);
        openingHoursInput = Set.of(openingHoursTuesdayInput, openingHoursFridayInput);

        var menuItemBuilder = new MenuItemBuilder();
        var menuItemInput = menuItemBuilder.buildInput();
        var menuItem = menuItemBuilder.build();
        menuItems = Set.of(menuItem);
        menuItemsInput = Set.of(menuItemInput);

        owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).withRole(RestaurantRoles.CREATE_RESTAURANT).build();
        employeeUuid = UUID.randomUUID();
        employee = new UserBuilder().withId(employeeUuid).withRole(RestaurantRoles.CREATE_RESTAURANT).build();
        employees = Set.of(employee);

        restaurantName = "My Restaurant";
        cuisineType = "Italian";
    }

    @Test
    @DisplayName("Deve criar restaurante com sucesso")
    void shouldCreateRestaurantSuccessfully() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeUuid),
                ownerId
        );

        Restaurant expectedRestaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withName(input.name())
                .withAddress(address)
                .withCuisineType(input.cuisineType())
                .withOpeningHours(openingHours)
                .withMenu(menuItems)
                .withOwner(owner)
                .withEmployee(employees)
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(employeeUuid)).willReturn(Optional.of(employee));
        given(restaurantGateway.existsRestaurantWithName(input.name())).willReturn(false);
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(expectedRestaurant);

        // When
        Restaurant result = createRestaurantUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(restaurantId);
        assertThat(result.getName()).isEqualTo(input.name());
        assertThat(result.getAddress()).isEqualTo(address);
        assertThat(result.getCuisineType()).isEqualTo(input.cuisineType());
        assertThat(result.getOpeningHours()).hasSize(2).containsExactlyInAnyOrderElementsOf(openingHours);
        assertThat(result.getMenu()).hasSize(1).containsExactlyInAnyOrderElementsOf(menuItems);
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getEmployees()).hasSize(1).containsExactlyInAnyOrderElementsOf(employees);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should().findById(ownerId);
        then(restaurantGateway).should().existsRestaurantWithName(input.name());
        then(userGateway).should().findById(employeeUuid);
        then(restaurantGateway).should().save(restaurantCaptor.capture());

        Restaurant capturedRestaurant = restaurantCaptor.getValue();
        assertThat(capturedRestaurant).isNotNull();
        assertThat(capturedRestaurant.getId()).isNull();
        assertThat(capturedRestaurant)
                .usingRecursiveComparison()
                .ignoringFields("id", "menu.id", "openingHours.id")
                .isEqualTo(expectedRestaurant);
    }

    @Test
    @DisplayName("Deve criar restaurante com apenas propriedades obrigatoria com sucesso")
    void shouldCreateRestaurantWithMandatoryPropertiesSuccessfully() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                addressInput,
                cuisineType,
                null,
                null,
                null,
                ownerId
        );

        Restaurant expectedRestaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withName(input.name())
                .withAddress(address)
                .withCuisineType(input.cuisineType())
                .withOpeningHours(Set.of())
                .withMenu(Set.of())
                .withOwner(owner)
                .withEmployee(Set.of())
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.existsRestaurantWithName(input.name())).willReturn(false);
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(expectedRestaurant);

        // When
        Restaurant result = createRestaurantUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(restaurantId);
        assertThat(result.getName()).isEqualTo(input.name());
        assertThat(result.getAddress()).isEqualTo(address);
        assertThat(result.getCuisineType()).isEqualTo(input.cuisineType());
        assertThat(result.getOpeningHours()).isEmpty();
        assertThat(result.getMenu()).isEmpty();
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getEmployees()).isEmpty();

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should().findById(ownerId);
        then(restaurantGateway).should().existsRestaurantWithName(input.name());
        then(userGateway).should(never()).findById(employeeUuid);
        then(restaurantGateway).should().save(restaurantCaptor.capture());

        Restaurant capturedRestaurant = restaurantCaptor.getValue();
        assertThat(capturedRestaurant).isNotNull();
        assertThat(capturedRestaurant.getId()).isNull();
        assertThat(capturedRestaurant)
                .usingRecursiveComparison()
                .ignoringFields("id", "menu.id", "openingHours.id")
                .isEqualTo(expectedRestaurant);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        CreateRestaurantInput input = mock(CreateRestaurantInput.class);
        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should(never()).findById(any());
        then(restaurantGateway).should(never()).existsRestaurantWithName(input.name());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando dono não é encontrado")
    void shouldThrowExceptionWhenOwnerNotFound() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeUuid),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findById(ownerId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Owner not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should().findById(ownerId);
        then(userGateway).should(never()).findById(employeeUuid);
        then(restaurantGateway).should(never()).existsRestaurantWithName(input.name());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando employee não é encontrado")
    void shouldThrowExceptionWhenEmployeeNotFound() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeUuid),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(employeeUuid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee " + employeeUuid + " not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should().findById(ownerId);
        then(userGateway).should().findById(employeeUuid);
        then(restaurantGateway).should().existsRestaurantWithName(input.name());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não pode ser dono de restaurante")
    void shouldThrowExceptionWhenUserCannotBeOwner() {
        var ordinaryUserUuid = UUID.randomUUID();
        var ordinaryUser = new UserBuilder().build();
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeUuid),
                ordinaryUserUuid
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findById(ordinaryUserUuid)).willReturn(Optional.of(ordinaryUser));

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class)
                .hasMessageContaining("User cannot be restaurant owner");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should().findById(ordinaryUserUuid);
        then(userGateway).should((never())).findById(employeeUuid);
        then(restaurantGateway).should(never()).existsRestaurantWithName(input.name());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do restaurante já existe")
    void shouldThrowExceptionWhenRestaurantNameAlreadyExists() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeUuid),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.existsRestaurantWithName(input.name())).willReturn(true);

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class)
                .hasMessageContaining("Restaurant name is already in use");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should().findById(ownerId);
        then(restaurantGateway).should().existsRestaurantWithName(input.name());
        then(userGateway).should(never()).findById(employeeUuid);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando input é nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> createRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(userGateway).should(never()).findById(ownerId);
        then(userGateway).should(never()).findById(employeeUuid);
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any());
    }
}
