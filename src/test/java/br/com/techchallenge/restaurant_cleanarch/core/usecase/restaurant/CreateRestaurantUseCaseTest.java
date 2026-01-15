package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.OpeningHoursBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateRestaurantUseCase")
class CreateRestaurantUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private RestaurantGateway restaurantGateway;
    @Mock private UserGateway userGateway;

    @InjectMocks
    private CreateRestaurantUseCase createRestaurantUseCase;

    @Captor
    private ArgumentCaptor<Restaurant> restaurantCaptor;

    private UUID ownerId;
    private UUID employeeId;
    private User owner;
    private User employee;
    private Address address;
    private String restaurantName;
    private String cuisineType;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        employeeId = UUID.randomUUID();

        address = new AddressBuilder().build();
        restaurantName = "My Restaurant";
        cuisineType = "Italian";

        owner = new UserBuilder()
                .withDefaults()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        employee = new UserBuilder()
                .withDefaults()
                .withId(employeeId)
                .build();
    }

    @Test
    @DisplayName("Deve criar restaurante com sucesso (com openingHours, menu e employees)")
    void shouldCreateRestaurantSuccessfully() {
        // Arrange
        OpeningHoursInput ohFridayInput = new OpeningHoursBuilder().buildInput();
        OpeningHours ohFriday = new OpeningHoursBuilder().build();

        OpeningHoursInput ohTuesdayInput = new OpeningHoursBuilder()
                .withDayOfDay(DayOfWeek.TUESDAY)
                .buildInput();
        OpeningHours ohTuesday = new OpeningHoursBuilder()
                .withDayOfDay(DayOfWeek.TUESDAY)
                .build();

        Set<OpeningHoursInput> openingHoursInput = Set.of(ohTuesdayInput, ohFridayInput);
        Set<OpeningHours> openingHours = Set.of(ohTuesday, ohFriday);

        MenuItemInput menuItemInput = new MenuItemBuilder().buildInput();
        MenuItem menuItem = new MenuItemBuilder().build();
        Set<MenuItemInput> menuItemsInput = Set.of(menuItemInput);
        Set<MenuItem> menuItems = Set.of(menuItem);

        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                openingHoursInput,
                menuItemsInput,
                Set.of(employeeId),
                ownerId
        );

        Restaurant savedFromGateway = new RestaurantBuilder()
                .withDefaults()
                .withId(1L)
                .withName(restaurantName)
                .withAddress(address)
                .withCuisineType(cuisineType)
                .withOwner(owner)
                .withEmployees(Set.of(employee)) // ✅ aqui
                .withOpeningHours(openingHours)
                .withMenu(menuItems)
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(employeeId)).willReturn(Optional.of(employee));
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(savedFromGateway);

        // Act
        Restaurant result = createRestaurantUseCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should().findById(ownerId);
        then(userGateway).should().findById(employeeId);
        then(restaurantGateway).should().save(restaurantCaptor.capture());

        Restaurant captured = restaurantCaptor.getValue();
        assertThat(captured).isNotNull();
        assertThat(captured.getId()).isNull(); // novo restaurante

        assertThat(captured)
                .usingRecursiveComparison()
                .ignoringFields("id", "menu.id", "openingHours.id")
                .isEqualTo(savedFromGateway);
    }

    @Test
    @DisplayName("Deve criar restaurante apenas com propriedades obrigatórias (sem coleções) com sucesso")
    void shouldCreateRestaurantWithMandatoryPropertiesSuccessfully() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                null,
                ownerId
        );

        Restaurant savedFromGateway = new RestaurantBuilder()
                .withDefaults()
                .withId(1L)
                .withName(restaurantName)
                .withAddress(address)
                .withCuisineType(cuisineType)
                .withOwner(owner)
                .withEmployees(Set.of()) // ✅ aqui
                .withOpeningHours(Set.of())
                .withMenu(Set.of())
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(savedFromGateway);

        // Act
        Restaurant result = createRestaurantUseCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmployees()).isEmpty();
        assertThat(result.getOpeningHours()).isEmpty();
        assertThat(result.getMenuItems()).isEmpty();

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should(times(1)).findById(ownerId);
        then(restaurantGateway).should().save(restaurantCaptor.capture());

        Restaurant captured = restaurantCaptor.getValue();
        assertThat(captured.getId()).isNull();
        assertThat(captured.getEmployees()).isEmpty();
        assertThat(captured.getOpeningHours()).isEmpty();
        assertThat(captured.getMenuItems()).isEmpty();
    }

    @Test
    @DisplayName("Deve criar restaurante com employees vazio (Set.of()) sem consultar employees")
    void shouldCreateRestaurantWithEmptyEmployeesSetSuccessfully() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(), // vazio explícito
                ownerId
        );

        Restaurant savedFromGateway = new RestaurantBuilder()
                .withDefaults()
                .withId(1L)
                .withName(restaurantName)
                .withAddress(address)
                .withCuisineType(cuisineType)
                .withOwner(owner)
                .withEmployees(Set.of()) // ✅ aqui
                .withOpeningHours(Set.of())
                .withMenu(Set.of())
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(savedFromGateway);

        // Act
        Restaurant result = createRestaurantUseCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmployees()).isEmpty();

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should(times(1)).findById(ownerId);
        then(userGateway).shouldHaveNoMoreInteractions();

        then(restaurantGateway).should().save(restaurantCaptor.capture());
        assertThat(restaurantCaptor.getValue().getEmployees()).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir owner estar em employees e evitar dupla consulta via cache")
    void shouldAllowOwnerToBeInEmployeesAndAvoidDoubleLookup() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(ownerId), // owner tb em employees
                ownerId
        );

        Restaurant savedFromGateway = new RestaurantBuilder()
                .withDefaults()
                .withId(1L)
                .withName(restaurantName)
                .withAddress(address)
                .withCuisineType(cuisineType)
                .withOwner(owner)
                .withEmployees(Set.of(owner)) // ✅ aqui
                .withOpeningHours(Set.of())
                .withMenu(Set.of())
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(savedFromGateway);

        // Act
        Restaurant result = createRestaurantUseCase.execute(input);

        // Assert
        assertThat(result.getEmployees()).contains(owner);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should(times(1)).findById(ownerId); // cache evita 2x
        then(userGateway).shouldHaveNoMoreInteractions();

        then(restaurantGateway).should().save(restaurantCaptor.capture());
        assertThat(restaurantCaptor.getValue().getEmployees()).contains(owner);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role (UseCaseBase)")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        // Arrange
        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(mock(CreateRestaurantInput.class)))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action.");

        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar RestaurantNameIsAlreadyInUseException quando nome já existe (não consulta userGateway)")
    void shouldThrowExceptionWhenRestaurantNameAlreadyExists() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(employeeId),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class)
                .hasMessageContaining("Restaurant name is already in use");

        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando owner não é encontrado")
    void shouldThrowExceptionWhenOwnerNotFound() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(employeeId),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Owner not found.");

        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should().findById(ownerId);
        then(userGateway).shouldHaveNoMoreInteractions();
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UserCannotBeRestaurantOwnerException quando owner não pode ser dono")
    void shouldThrowExceptionWhenUserCannotBeOwner() {
        // Arrange
        UUID ordinaryUserId = UUID.randomUUID();

        User ordinaryUser = new UserBuilder()
                .withDefaults()
                .withId(ordinaryUserId)
                .build();

        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(employeeId),
                ordinaryUserId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ordinaryUserId)).willReturn(Optional.of(ordinaryUser));

        // Act / Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class)
                .hasMessageContaining("User cannot be restaurant owner");

        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should().findById(ordinaryUserId);
        then(userGateway).shouldHaveNoMoreInteractions();
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando employee não é encontrado")
    void shouldThrowExceptionWhenEmployeeNotFound() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(employeeId),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(employeeId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee " + employeeId + " not found.");

        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should().findById(ownerId);
        then(userGateway).should().findById(employeeId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (UseCaseBase)")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> createRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando ownerId é nulo (precisa passar pela role antes)")
    void shouldThrowExceptionWhenOwnerIdIsNull() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Owner id cannot be null.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando nome do restaurante é nulo (precisa passar pela role antes)")
    void shouldThrowExceptionWhenRestaurantNameIsNull() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                null,
                new AddressBuilder().buildInput(),
                cuisineType,
                null,
                null,
                null,
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Restaurant name cannot be null.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }
}
