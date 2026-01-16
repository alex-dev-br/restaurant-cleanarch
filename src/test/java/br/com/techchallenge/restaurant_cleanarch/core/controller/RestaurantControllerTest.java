package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.*;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.*;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RestaurantController")
class RestaurantControllerTest {

    @Mock
    private CreateRestaurantUseCase createRestaurantUseCase;

    @Mock
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Mock
    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;

    @Mock
    private ListRestaurantsUseCase listRestaurantsUseCase;

    @Mock
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    @Mock
    private ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase;

    @Mock
    private GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase;

    @Mock
    private ListRestaurantsPagedUseCase listRestaurantsPagedUseCase;

    @InjectMocks
    private RestaurantController restaurantController;

    @Captor
    private ArgumentCaptor<CreateRestaurantInput> createRestaurantInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateRestaurantInput> updateRestaurantInputCaptor;

    @Captor
    private ArgumentCaptor<PagedQuery<String>> pagedQueryCaptor;

    private Long restaurantId;
    private UUID ownerId;
    private UUID employeeUuid;
    private User owner;
    private UserSummaryOutput ownerSummaryOutput;
    private User employee;
    private UserSummaryOutput employeeSummaryOutput;
    private OpeningHoursInput openingHoursInput;
    private OpeningHours openingHours;
    private OpeningHoursOutput openingHoursOutput;
    private MenuItemInput menuItemInput;
    private MenuItemOutput menuOutput;
    private MenuItem menuItem;
    private Address address;
    private AddressInput addressInput;
    private AddressOutput addressOutput;

    @BeforeEach
    void setUp() {
        var openingHoursBuilder = new OpeningHoursBuilder();
        var menuItemBuilder = new MenuItemBuilder();
        var userBuilder = new UserBuilder();
        var addressBuilder = new AddressBuilder();

        ownerId = UUID.randomUUID();
        employeeUuid = UUID.randomUUID();
        restaurantId = 1L;

        openingHoursInput = openingHoursBuilder.buildInput();
        openingHours = openingHoursBuilder.build();
        openingHoursOutput = openingHoursBuilder.buildOutput();

        menuItemInput = menuItemBuilder.buildInput();
        menuItem = menuItemBuilder.build();
        menuOutput = menuItemBuilder.buildOutput(restaurantId);

        owner = userBuilder.withId(ownerId).withRole(UserRoles.RESTAURANT_OWNER).build();
        ownerSummaryOutput = userBuilder.buildSummaryOutput();

        employee = userBuilder.withId(employeeUuid).build();
        employeeSummaryOutput = userBuilder.buildSummaryOutput();

        address = addressBuilder.build();
        addressInput = addressBuilder.buildInput();
        addressOutput = addressBuilder.buildOutput();
    }

    @Test
    @DisplayName("Deve criar Restaurant com sucesso e retornar RestaurantPublicOutput")
    void shouldCreateRestaurantSuccessfully() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                "Restaurante Teste",
                addressInput,
                "Italiana",
                Set.of(openingHoursInput),
                Set.of(menuItemInput),
                Set.of(employeeUuid),
                ownerId
        );

        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                "Italiana",
                owner
        );

        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);
        restaurant.addEmployee(employee);

        given(createRestaurantUseCase.execute(input)).willReturn(restaurant);

        // Act
        RestaurantPublicOutput result = restaurantController.createRestaurant(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(restaurant.getId());
        assertThat(result.name()).isEqualTo(restaurant.getName());
        assertThat(result.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(result.address()).isEqualTo(addressOutput);
        assertThat(result.openingHours()).isNotNull().hasSize(1).containsExactlyInAnyOrder(openingHoursOutput);
        assertThat(result.menuItems()).isNotNull().hasSize(1).containsExactlyInAnyOrder(menuOutput);

        then(createRestaurantUseCase).should().execute(createRestaurantInputCaptor.capture());
        CreateRestaurantInput capturedInput = createRestaurantInputCaptor.getValue();
        assertThat(capturedInput).isNotNull();
        assertThat(capturedInput).usingRecursiveComparison().isEqualTo(input);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CreateRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenUseCaseThrowsException() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                "Restaurante Teste",
                addressInput,
                "Italiana",
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet(),
                UUID.randomUUID()
        );
        RuntimeException expectedException = new RuntimeException("Error creating restaurant");

        given(createRestaurantUseCase.execute(input)).willThrow(expectedException);

        assertThatThrownBy(() -> restaurantController.createRestaurant(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error creating restaurant");

        then(createRestaurantUseCase).should().execute(input);
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com CreateRestaurantUseCase nulo")
    void shouldThrowExceptionWhenCreateUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(
                null,
                updateRestaurantUseCase,
                getRestaurantByIdUseCase,
                listRestaurantsUseCase,
                deleteRestaurantUseCase,
                listRestaurantsByCuisineTypeUseCase,
                getRestaurantManagementByIdUseCase,
                listRestaurantsPagedUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com UpdateRestaurantUseCase nulo")
    void shouldThrowExceptionWhenUpdateUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(
                createRestaurantUseCase,
                null,
                getRestaurantByIdUseCase,
                listRestaurantsUseCase,
                deleteRestaurantUseCase,
                listRestaurantsByCuisineTypeUseCase,
                getRestaurantManagementByIdUseCase,
                listRestaurantsPagedUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetByIdRestaurantUseCase nulo")
    void shouldThrowExceptionWhenGetByIdUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(
                createRestaurantUseCase,
                updateRestaurantUseCase,
                null,
                listRestaurantsUseCase,
                deleteRestaurantUseCase,
                listRestaurantsByCuisineTypeUseCase,
                getRestaurantManagementByIdUseCase,
                listRestaurantsPagedUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetByIdRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetAllRestaurantUseCase nulo")
    void shouldThrowExceptionWhenGetAllUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(
                createRestaurantUseCase,
                updateRestaurantUseCase,
                getRestaurantByIdUseCase,
                null,
                deleteRestaurantUseCase,
                listRestaurantsByCuisineTypeUseCase,
                getRestaurantManagementByIdUseCase,
                listRestaurantsPagedUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetAllRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com DeleteRestaurantUseCase nulo")
    void shouldThrowExceptionWhenDeleteUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(
                createRestaurantUseCase,
                updateRestaurantUseCase,
                getRestaurantByIdUseCase,
                listRestaurantsUseCase,
                null,
                listRestaurantsByCuisineTypeUseCase,
                getRestaurantManagementByIdUseCase,
                listRestaurantsPagedUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DeleteRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com ListRestaurantsByCuisineTypeUseCase nulo")
    void shouldThrowExceptionWhenListByCuisineTypeUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(
                createRestaurantUseCase,
                updateRestaurantUseCase,
                getRestaurantByIdUseCase,
                listRestaurantsUseCase,
                deleteRestaurantUseCase,
                null,
                getRestaurantManagementByIdUseCase,
                listRestaurantsPagedUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ListRestaurantsByCuisineTypeUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar createRestaurant com input nulo")
    void shouldThrowExceptionWhenCreateRestaurantInputIsNull() {
        assertThatThrownBy(() -> restaurantController.createRestaurant(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateRestaurantInput cannot be null.");
    }

    @Test
    @DisplayName("Deve atualizar Restaurant com sucesso")
    void shouldUpdateRestaurantSuccessfully() {
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                1L,
                "Novo Nome",
                addressInput,
                "Japonesa",
                null,
                null,
                null,
                null
        );

        restaurantController.updateRestaurant(input);

        then(updateRestaurantUseCase).should().execute(updateRestaurantInputCaptor.capture());
        UpdateRestaurantInput capturedInput = updateRestaurantInputCaptor.getValue();
        assertThat(capturedInput).usingRecursiveComparison().isEqualTo(input);
    }

    @Test
    @DisplayName("Deve lançar exceção quando UpdateRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenUpdateUseCaseThrowsException() {
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Novo Nome",
                addressInput,
                "Japonesa",
                null,
                null,
                null,
                null
        );
        RuntimeException expectedException = new RuntimeException("Error updating restaurant");

        willThrow(expectedException).given(updateRestaurantUseCase).execute(input);

        assertThatThrownBy(() -> restaurantController.updateRestaurant(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error updating restaurant");

        then(updateRestaurantUseCase).should().execute(input);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar updateRestaurant com input nulo")
    void shouldThrowExceptionWhenUpdateRestaurantInputIsNull() {
        assertThatThrownBy(() -> restaurantController.updateRestaurant(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateRestaurantInput cannot be null.");
    }

    @Test
    @DisplayName("Deve buscar Restaurant por ID com sucesso e retornar RestaurantPublicOutput")
    void shouldFindRestaurantByIdSuccessfully() {
        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                "Italiana",
                owner
        );
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);
        restaurant.addEmployee(employee);

        given(getRestaurantByIdUseCase.execute(restaurantId)).willReturn(restaurant);

        RestaurantPublicOutput result = restaurantController.findById(restaurantId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(restaurant.getId());
        assertThat(result.name()).isEqualTo(restaurant.getName());
        assertThat(result.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(result.openingHours()).isNotNull().hasSize(1);
        assertThat(result.menuItems()).isNotNull().hasSize(1);

        then(getRestaurantByIdUseCase).should().execute(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando GetByIdRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenGetByIdUseCaseThrowsException() {
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("Restaurant not found");

        given(getRestaurantByIdUseCase.execute(id)).willThrow(expectedException);

        assertThatThrownBy(() -> restaurantController.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Restaurant not found");

        then(getRestaurantByIdUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar findById com ID nulo")
    void shouldThrowExceptionWhenFindByIdWithNullId() {
        assertThatThrownBy(() -> restaurantController.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");
    }

    @Test
    @DisplayName("Deve buscar todos os restaurantes com sucesso e retornar lista de RestaurantPublicOutput")
    void shouldFindAllRestaurantsSuccessfully() {
        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                "Italiana",
                owner
        );
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);
        restaurant.addEmployee(employee);

        given(listRestaurantsUseCase.execute()).willReturn(List.of(restaurant));

        List<RestaurantPublicOutput> result = restaurantController.findAll();

        assertThat(result).isNotNull().hasSize(1);
        RestaurantPublicOutput output = result.getFirst();
        assertThat(output.id()).isEqualTo(restaurant.getId());
        assertThat(output.name()).isEqualTo(restaurant.getName());
        assertThat(output.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(output.openingHours()).isNotNull().hasSize(1);
        assertThat(output.menuItems()).isNotNull().hasSize(1);

        then(listRestaurantsUseCase).should().execute();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver restaurantes")
    void shouldReturnEmptyListWhenNoRestaurantsFound() {
        given(listRestaurantsUseCase.execute()).willReturn(Collections.emptyList());

        List<RestaurantPublicOutput> result = restaurantController.findAll();

        assertThat(result).isNotNull().isEmpty();

        then(listRestaurantsUseCase).should().execute();
    }

    @Test
    @DisplayName("Deve buscar por tipo de cozinha e retornar página de RestaurantPublicOutput")
    void shouldFindByCuisineTypeAndReturnPageOfRestaurantOutput() {
        String cuisineType = "Italiana";
        int pageNumber = 0;
        int pageSize = 10;

        var pagedQuery = new PagedQuery<>(cuisineType, pageNumber, pageSize);

        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                cuisineType,
                owner
        );
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);

        Page<Restaurant> restaurantPage = new Page<>(pageNumber, pageSize, 1L, List.of(restaurant));

        given(listRestaurantsByCuisineTypeUseCase.execute(pagedQuery)).willReturn(restaurantPage);

        Page<RestaurantPublicOutput> result = restaurantController.findByCuisineType(cuisineType, pageNumber, pageSize);

        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isOne();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).hasSize(1);

        RestaurantPublicOutput output = result.content().getFirst();
        assertThat(output.cuisineType()).isEqualTo(cuisineType);
        assertThat(output.address()).isEqualTo(addressOutput);
        assertThat(output.openingHours()).containsExactlyInAnyOrder(openingHoursOutput);
        assertThat(output.menuItems()).containsExactlyInAnyOrder(menuOutput);

        then(listRestaurantsByCuisineTypeUseCase).should().execute(pagedQueryCaptor.capture());
        assertThat(pagedQueryCaptor.getValue()).usingRecursiveComparison().isEqualTo(pagedQuery);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum restaurante for encontrado por tipo de cozinha")
    void shouldReturnEmptyPageWhenNoRestaurantFoundByCuisineType() {
        String cuisineType = "Inexistente";
        int pageNumber = 0;
        int pageSize = 10;

        var pagedQuery = new PagedQuery<>(cuisineType, pageNumber, pageSize);
        Page<Restaurant> emptyPage = new Page<>(pageNumber, pageSize, 0L, List.of());

        given(listRestaurantsByCuisineTypeUseCase.execute(pagedQuery)).willReturn(emptyPage);

        Page<RestaurantPublicOutput> result = restaurantController.findByCuisineType(cuisineType, pageNumber, pageSize);

        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
        assertThat(result.content()).isEmpty();

        then(listRestaurantsByCuisineTypeUseCase).should().execute(pagedQuery);
    }

    @Test
    @DisplayName("Deve deletar restaurante com sucesso")
    void shouldDeleteRestaurantSuccessfully() {
        Long id = 1L;

        restaurantController.deleteById(id);

        then(deleteRestaurantUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar deleteById com ID nulo")
    void shouldThrowExceptionWhenDeleteByIdWithNullId() {
        assertThatThrownBy(() -> restaurantController.deleteById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando DeleteRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenDeleteUseCaseThrowsException() {
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("Error deleting restaurant");

        willThrow(expectedException).given(deleteRestaurantUseCase).execute(id);

        assertThatThrownBy(() -> restaurantController.deleteById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error deleting restaurant");

        then(deleteRestaurantUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetRestaurantManagementByIdUseCase nulo")
    void shouldThrowExceptionWhenGetRestaurantManagementByIdUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(
                createRestaurantUseCase,
                updateRestaurantUseCase,
                getRestaurantByIdUseCase,
                listRestaurantsUseCase,
                deleteRestaurantUseCase,
                listRestaurantsByCuisineTypeUseCase,
                null,
                listRestaurantsPagedUseCase)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetRestaurantManagementByIdUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve buscar Restaurant Management por ID com sucesso e retornar RestaurantManagementOutput")
    void shouldFindRestaurantManagementByIdSuccessfully() {
        Long id = 1L;

        Restaurant restaurant = new RestaurantBuilder().withId(id).build();
        restaurant.addEmployee(new UserBuilder().withId(UUID.randomUUID()).build());

        given(getRestaurantManagementByIdUseCase.execute(id)).willReturn(restaurant);

        RestaurantManagementOutput result = restaurantController.findManagementById(id);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.employees()).isNotNull().hasSize(1);

        then(getRestaurantManagementByIdUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar findManagementById com ID nulo")
    void shouldThrowExceptionWhenFindManagementByIdWithNullId() {
        assertThatThrownBy(() -> restaurantController.findManagementById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");
    }
}
