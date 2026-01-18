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
import br.com.techchallenge.restaurant_cleanarch.core.inbound.AddressInput;
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
import java.time.DayOfWeek;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

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
    private String restaurantName;
    private String cuisineType;

    @BeforeEach
    void setUp() {
        // Arrange (common)
        ownerId = UUID.randomUUID();
        employeeId = UUID.randomUUID();

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
        OpeningHoursInput ohFridayInput = new OpeningHoursBuilder().withDefaults().buildInput();
        OpeningHours ohFriday = new OpeningHoursBuilder().withDefaults().build();

        OpeningHoursInput ohTuesdayInput = new OpeningHoursBuilder()
                .withDefaults()
                .withDayOfWeek(DayOfWeek.TUESDAY)
                .buildInput();
        OpeningHours ohTuesday = new OpeningHoursBuilder()
                .withDefaults()
                .withDayOfWeek(DayOfWeek.TUESDAY)
                .build();

        Set<OpeningHoursInput> openingHoursInput = Set.of(ohTuesdayInput, ohFridayInput);
        Set<OpeningHours> openingHours = Set.of(ohTuesday, ohFriday);

        MenuItemInput menuItemInput = new MenuItemBuilder().withDefaults().buildInput();
        MenuItem menuItem = new MenuItemBuilder().withDefaults().build();
        Set<MenuItemInput> menuItemsInput = Set.of(menuItemInput);
        Set<MenuItem> menuItems = Set.of(menuItem);

        AddressInput addressInput = new AddressBuilder().withDefaults().buildInput();

        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                addressInput,
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
                .withAddress(new AddressBuilder().withDefaults().build())
                .withCuisineType(cuisineType)
                .withOwner(owner)
                .withEmployees(Set.of(employee))
                .withOpeningHours(openingHours)
                .withMenuItems(menuItems)
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(employeeId)).willReturn(Optional.of(employee));
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(savedFromGateway);

        // Act
        Restaurant result = createRestaurantUseCase.execute(input);

        // Assert (retorno)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should().findById(ownerId);
        then(userGateway).should().findById(employeeId);
        then(restaurantGateway).should().save(restaurantCaptor.capture());

        // Assert (objeto enviado ao gateway)
        Restaurant captured = restaurantCaptor.getValue();
        assertThat(captured).isNotNull();
        assertThat(captured.getId()).isNull();
        assertThat(captured.getName()).isEqualTo(restaurantName);
        assertThat(captured.getCuisineType()).isEqualTo(cuisineType);

        // compara Address por campos (evita fragilidade de instância)
        assertThat(captured.getAddress().getStreet()).isEqualTo(addressInput.street());
        assertThat(captured.getAddress().getNumber()).isEqualTo(addressInput.number());
        assertThat(captured.getAddress().getCity()).isEqualTo(addressInput.city());
        assertThat(captured.getAddress().getState()).isEqualTo(addressInput.state());
        assertThat(captured.getAddress().getZipCode()).isEqualTo(addressInput.zipCode());
        assertThat(captured.getAddress().getComplement()).isEqualTo(addressInput.complement());

        assertThat(captured.getOwner()).isEqualTo(owner);

        assertThat(captured.getEmployees()).extracting(User::getId)
                .containsExactlyInAnyOrder(employeeId);

        assertThat(captured.getOpeningHours())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(openingHours);

        assertThat(captured.getMenuItems())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(menuItems);
    }

    @Test
    @DisplayName("Deve criar restaurante apenas com propriedades obrigatórias (sem coleções) com sucesso")
    void shouldCreateRestaurantWithMandatoryPropertiesSuccessfully() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().withDefaults().buildInput(),
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
                .withAddress(new AddressBuilder().withDefaults().build())
                .withCuisineType(cuisineType)
                .withOwner(owner)
                .withEmployees(Set.of())
                .withOpeningHours(Set.of())
                .withMenuItems(Set.of())
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

        then(userGateway).should(times(1)).findById(ownerId);

        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant captured = restaurantCaptor.getValue();

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
                new AddressBuilder().withDefaults().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(),
                ownerId
        );

        Restaurant savedFromGateway = new RestaurantBuilder()
                .withDefaults()
                .withId(1L)
                .withName(restaurantName)
                .withAddress(new AddressBuilder().withDefaults().build())
                .withCuisineType(cuisineType)
                .withOwner(owner)
                .withEmployees(Set.of())
                .withOpeningHours(Set.of())
                .withMenuItems(Set.of())
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

        then(userGateway).should(times(1)).findById(ownerId);
        then(userGateway).shouldHaveNoMoreInteractions();

        then(restaurantGateway).should().save(restaurantCaptor.capture());
        assertThat(restaurantCaptor.getValue().getEmployees()).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir ownerId estar em employees e evitar dupla consulta via cache")
    void shouldAllowOwnerToBeInEmployeesAndAvoidDoubleLookup() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().withDefaults().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(ownerId),
                ownerId
        );

        Restaurant savedFromGateway = new RestaurantBuilder()
                .withDefaults()
                .withId(1L)
                .withName(restaurantName)
                .withAddress(new AddressBuilder().withDefaults().build())
                .withCuisineType(cuisineType)
                .withOwner(owner)
                .withEmployees(Set.of(owner))
                .withOpeningHours(Set.of())
                .withMenuItems(Set.of())
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(savedFromGateway);

        // Act
        Restaurant result = createRestaurantUseCase.execute(input);

        // Assert
        assertThat(result.getEmployees()).contains(owner);

        then(userGateway).should(times(1)).findById(ownerId);
        then(userGateway).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role (UseCaseBase)")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        // Arrange
        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(mock(CreateRestaurantInput.class)))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission");

        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar RestaurantNameIsAlreadyInUseException quando nome já existe (não consulta userGateway)")
    void shouldThrowExceptionWhenRestaurantNameAlreadyExists() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().withDefaults().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(employeeId),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class)
                .hasMessageContaining("already in use");

        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando ownerId não é encontrado")
    void shouldThrowExceptionWhenOwnerNotFound() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().withDefaults().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(employeeId),
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ownerId)).willReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Owner not found");

        then(userGateway).should().findById(ownerId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UserCannotBeRestaurantOwnerException quando ownerId não pode ser dono")
    void shouldThrowExceptionWhenUserCannotBeOwner() {
        // Arrange
        UUID ordinaryUserId = UUID.randomUUID();

        User ordinaryUser = new UserBuilder()
                .withDefaults()
                .withId(ordinaryUserId)
                .build();

        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().withDefaults().buildInput(),
                cuisineType,
                null,
                null,
                Set.of(employeeId),
                ordinaryUserId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(ordinaryUserId)).willReturn(Optional.of(ordinaryUser));

        // Act + Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class)
                .hasMessageContaining("restaurant ownerId");

        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando employee não é encontrado")
    void shouldThrowExceptionWhenEmployeeNotFound() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                new AddressBuilder().withDefaults().buildInput(),
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

        // Act + Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee " + employeeId);

        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (UseCaseBase)")
    void shouldThrowExceptionWhenInputIsNull() {
        // Arrange / Act + Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null");

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
                new AddressBuilder().withDefaults().buildInput(),
                cuisineType,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Owner");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando nome do restaurante é nulo (precisa passar pela role antes)")
    void shouldThrowExceptionWhenRestaurantNameIsNull() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                null,
                new AddressBuilder().withDefaults().buildInput(),
                cuisineType,
                null,
                null,
                null,
                ownerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Restaurant name");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve retornar null nos métodos build* quando input for null (cobre branches)")
    void shouldReturnNullWhenCallingPrivateBuildersWithNullInput() throws Exception {
        // Arrange
        Method buildMenu = CreateRestaurantUseCase.class.getDeclaredMethod("buildMenu", MenuItemInput.class);
        buildMenu.setAccessible(true);

        Method buildOpeningHours = CreateRestaurantUseCase.class.getDeclaredMethod("buildOpeningHours", OpeningHoursInput.class);
        buildOpeningHours.setAccessible(true);

        Method buildAddress = CreateRestaurantUseCase.class.getDeclaredMethod("buildAddress", AddressInput.class);
        buildAddress.setAccessible(true);

        // Act
        MenuItem menuItem = (MenuItem) buildMenu.invoke(createRestaurantUseCase, new Object[]{null});
        OpeningHours openingHours = (OpeningHours) buildOpeningHours.invoke(createRestaurantUseCase, new Object[]{null});
        Address address = (Address) buildAddress.invoke(createRestaurantUseCase, new Object[]{null});

        // Assert
        assertThat(menuItem).isNull();
        assertThat(openingHours).isNull();
        assertThat(address).isNull();
    }

    @Test
    @DisplayName("Deve construir objetos corretamente nos métodos build* via reflection (cobre branches não-nulos)")
    void shouldBuildObjectsWhenCallingPrivateBuildersWithNonNullInput() throws Exception {
        // Arrange
        MenuItemInput menuItemInput = new MenuItemBuilder().withDefaults().withName("Dish X").buildInput();
        OpeningHoursInput openingHoursInput = new OpeningHoursBuilder().withDefaults().withDayOfWeek(DayOfWeek.TUESDAY).buildInput();
        AddressInput addressInput = new AddressBuilder().withDefaults().withStreet("Street X").buildInput();

        Method buildMenu = CreateRestaurantUseCase.class.getDeclaredMethod("buildMenu", MenuItemInput.class);
        buildMenu.setAccessible(true);

        Method buildOpeningHours = CreateRestaurantUseCase.class.getDeclaredMethod("buildOpeningHours", OpeningHoursInput.class);
        buildOpeningHours.setAccessible(true);

        Method buildAddress = CreateRestaurantUseCase.class.getDeclaredMethod("buildAddress", AddressInput.class);
        buildAddress.setAccessible(true);

        // Act
        MenuItem menuItem = (MenuItem) buildMenu.invoke(createRestaurantUseCase, menuItemInput);
        OpeningHours openingHours = (OpeningHours) buildOpeningHours.invoke(createRestaurantUseCase, openingHoursInput);
        Address address = (Address) buildAddress.invoke(createRestaurantUseCase, addressInput);

        // Assert
        assertThat(menuItem).isNotNull();
        assertThat(menuItem.getId()).isNull(); // create use case seta null
        assertThat(menuItem.getName()).isEqualTo(menuItemInput.name());
        assertThat(menuItem.getDescription()).isEqualTo(menuItemInput.description());

        assertThat(openingHours).isNotNull();
        assertThat(openingHours.getId()).isNull(); // create use case seta null
        assertThat(openingHours.getDayOfWeek()).isEqualTo(openingHoursInput.dayOfWeek());

        assertThat(address).isNotNull();
        assertThat(address.getStreet()).isEqualTo("Street X");
        assertThat(address.getZipCode()).isEqualTo(addressInput.zipCode());
    }
}
