package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantOutput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.*;
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
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private RestaurantController restaurantController;

    @Captor
    private ArgumentCaptor<CreateRestaurantInput> createRestaurantInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateRestaurantInput> updateRestaurantInputCaptor;

    @Captor
    private ArgumentCaptor<PagedQuery<String>> pagedQueryCaptor;

    @Test
    @DisplayName("Deve criar Restaurant com sucesso e retornar RestaurantOutput")
    void shouldCreateRestaurantSuccessfully() {
        // Arrange
        UUID ownerId = UUID.randomUUID();
        CreateRestaurantInput input = new CreateRestaurantInput(
                "Restaurante Teste",
                new AddressBuilder().buildInput(),
                "Italiana",
                Set.of(new OpeningHoursBuilder().buildInput()),
                Set.of(new MenuItemBuilder().buildInput()),
                ownerId
        );

        Address address = new AddressBuilder().build();
        User owner = new UserBuilder().withId(ownerId).withRole(UserRoles.RESTAURANT_OWNER).build();
        
        Restaurant restaurant = new Restaurant(
                1L,
                "Restaurante Teste",
                address,
                "Italiana",
                Set.of(new OpeningHoursBuilder().build()),
                Set.of(new MenuItemBuilder().build()),
                owner
        );

        given(createRestaurantUseCase.execute(input)).willReturn(restaurant);

        // Act
        RestaurantOutput result = restaurantController.createRestaurant(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(restaurant.getId());
        assertThat(result.name()).isEqualTo(restaurant.getName());
        assertThat(result.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(result.openingHours()).isNotNull().hasSize(1);
        assertThat(result.menuItems()).isNotNull().hasSize(1);
        assertThat(result.ownerId()).isEqualTo(ownerId);

        then(createRestaurantUseCase).should().execute(createRestaurantInputCaptor.capture());
        CreateRestaurantInput capturedInput = createRestaurantInputCaptor.getValue();
        assertThat(capturedInput).isEqualTo(input);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CreateRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenUseCaseThrowsException() {
        // Arrange
        CreateRestaurantInput input = new CreateRestaurantInput(
                "Restaurante Teste",
                new AddressBuilder().buildInput(),
                "Italiana",
                Collections.emptySet(),
                Collections.emptySet(),
                UUID.randomUUID()
        );
        RuntimeException expectedException = new RuntimeException("Error creating restaurant");

        given(createRestaurantUseCase.execute(input)).willThrow(expectedException);

        // Act & Assert
        assertThatThrownBy(() -> restaurantController.createRestaurant(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error creating restaurant");

        then(createRestaurantUseCase).should().execute(input);
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com CreateRestaurantUseCase nulo")
    void shouldThrowExceptionWhenCreateUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(null, updateRestaurantUseCase, getRestaurantByIdUseCase, listRestaurantsUseCase, deleteRestaurantUseCase, listRestaurantsByCuisineTypeUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com UpdateRestaurantUseCase nulo")
    void shouldThrowExceptionWhenUpdateUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(createRestaurantUseCase, null, getRestaurantByIdUseCase, listRestaurantsUseCase, deleteRestaurantUseCase, listRestaurantsByCuisineTypeUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetByIdRestaurantUseCase nulo")
    void shouldThrowExceptionWhenGetByIdUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(createRestaurantUseCase, updateRestaurantUseCase, null, listRestaurantsUseCase, deleteRestaurantUseCase, listRestaurantsByCuisineTypeUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetByIdRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetAllRestaurantUseCase nulo")
    void shouldThrowExceptionWhenGetAllUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(createRestaurantUseCase, updateRestaurantUseCase, getRestaurantByIdUseCase, null, deleteRestaurantUseCase, listRestaurantsByCuisineTypeUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetAllRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com DeleteRestaurantUseCase nulo")
    void shouldThrowExceptionWhenDeleteUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(createRestaurantUseCase, updateRestaurantUseCase, getRestaurantByIdUseCase, listRestaurantsUseCase, null, listRestaurantsByCuisineTypeUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DeleteRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com ListRestaurantsByCuisineTypeUseCase nulo")
    void shouldThrowExceptionWhenListByCuisineTypeUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(createRestaurantUseCase, updateRestaurantUseCase, getRestaurantByIdUseCase, listRestaurantsUseCase, deleteRestaurantUseCase, null))
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
        // Arrange
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                1L,
                "Novo Nome",
                new AddressBuilder().buildInput(),
                "Japonesa",
                null,
                null,
                null
        );

        // Act
        restaurantController.updateRestaurant(input);

        // Assert
        then(updateRestaurantUseCase).should().execute(updateRestaurantInputCaptor.capture());
        UpdateRestaurantInput capturedInput = updateRestaurantInputCaptor.getValue();
        assertThat(capturedInput).isEqualTo(input);
    }

    @Test
    @DisplayName("Deve lançar exceção quando UpdateRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenUpdateUseCaseThrowsException() {
        // Arrange
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                1L,
                "Novo Nome",
                new AddressBuilder().buildInput(),
                "Japonesa",
                null,
                null,
                null
        );
        RuntimeException expectedException = new RuntimeException("Error updating restaurant");

        willThrow(expectedException).given(updateRestaurantUseCase).execute(input);

        // Act & Assert
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
    @DisplayName("Deve buscar Restaurant por ID com sucesso e retornar RestaurantOutput")
    void shouldFindRestaurantByIdSuccessfully() {
        // Arrange
        Long id = 1L;
        UUID ownerId = UUID.randomUUID();
        Address address = new AddressBuilder().build();
        User owner = new UserBuilder().withId(ownerId).withRole(UserRoles.RESTAURANT_OWNER).build();

        Restaurant restaurant = new Restaurant(
                id,
                "Restaurante Teste",
                address,
                "Italiana",
                Set.of(new OpeningHoursBuilder().build()),
                Set.of(new MenuItemBuilder().build()),
                owner
        );

        given(getRestaurantByIdUseCase.execute(id)).willReturn(restaurant);

        // Act
        RestaurantOutput result = restaurantController.findById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(restaurant.getId());
        assertThat(result.name()).isEqualTo(restaurant.getName());
        assertThat(result.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(result.openingHours()).isNotNull().hasSize(1);
        assertThat(result.menuItems()).isNotNull().hasSize(1);
        assertThat(result.ownerId()).isEqualTo(ownerId);

        then(getRestaurantByIdUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando GetByIdRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenGetByIdUseCaseThrowsException() {
        // Arrange
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("Restaurant not found");

        given(getRestaurantByIdUseCase.execute(id)).willThrow(expectedException);

        // Act & Assert
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
    @DisplayName("Deve buscar todos os restaurantes com sucesso e retornar lista de RestaurantOutput")
    void shouldFindAllRestaurantsSuccessfully() {
        // Arrange
        Long id = 1L;
        UUID ownerId = UUID.randomUUID();
        Address address = new AddressBuilder().build();
        User owner = new UserBuilder().withId(ownerId).withRole(UserRoles.RESTAURANT_OWNER).build();

        Restaurant restaurant = new Restaurant(
                id,
                "Restaurante Teste",
                address,
                "Italiana",
                Set.of(new OpeningHoursBuilder().build()),
                Set.of(new MenuItemBuilder().build()),
                owner
        );

        given(listRestaurantsUseCase.execute()).willReturn(List.of(restaurant));

        // Act
        List<RestaurantOutput> result = restaurantController.findAll();

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        RestaurantOutput output = result.getFirst();
        assertThat(output.id()).isEqualTo(restaurant.getId());
        assertThat(output.name()).isEqualTo(restaurant.getName());
        assertThat(output.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(output.openingHours()).isNotNull().hasSize(1);
        assertThat(output.menuItems()).isNotNull().hasSize(1);
        assertThat(output.ownerId()).isEqualTo(ownerId);

        then(listRestaurantsUseCase).should().execute();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver restaurantes")
    void shouldReturnEmptyListWhenNoRestaurantsFound() {
        // Arrange
        given(listRestaurantsUseCase.execute()).willReturn(Collections.emptyList());

        // Act
        List<RestaurantOutput> result = restaurantController.findAll();

        // Assert
        assertThat(result).isNotNull().isEmpty();

        then(listRestaurantsUseCase).should().execute();
    }

    @Test
    @DisplayName("Deve buscar por tipo de cozinha e retornar página de RestaurantOutput")
    void shouldFindByCuisineTypeAndReturnPageOfRestaurantOutput() {
        // Arrange
        String cuisineType = "Italiana";
        int pageNumber = 0;
        int pageSize = 10;

        Restaurant restaurant = new RestaurantBuilder().withCuisineType(cuisineType).build();
        Page<Restaurant> restaurantPage = new Page<>(pageNumber, pageSize, 1, 1, List.of(restaurant));

        given(listRestaurantsByCuisineTypeUseCase.execute(any())).willReturn(restaurantPage);

        // Act
        Page<RestaurantOutput> result = restaurantController.findByCuisineType(cuisineType, pageNumber, pageSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.currentPage()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isOne();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().cuisineType()).isEqualTo(cuisineType);

        then(listRestaurantsByCuisineTypeUseCase).should().execute(pagedQueryCaptor.capture());
        PagedQuery<String> capturedQuery = pagedQueryCaptor.getValue();
        assertThat(capturedQuery.filter()).isEqualTo(cuisineType);
        assertThat(capturedQuery.pageNumber()).isEqualTo(pageNumber);
        assertThat(capturedQuery.pageSize()).isEqualTo(pageSize);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum restaurante for encontrado por tipo de cozinha")
    void shouldReturnEmptyPageWhenNoRestaurantFoundByCuisineType() {
        // Arrange
        String cuisineType = "Inexistente";
        int pageNumber = 0;
        int pageSize = 10;

        Page<Restaurant> emptyPage = new Page<>(pageNumber, pageSize, 0, 1, List.of());

        given(listRestaurantsByCuisineTypeUseCase.execute(any())).willReturn(emptyPage);

        // Act
        Page<RestaurantOutput> result = restaurantController.findByCuisineType(cuisineType, pageNumber, pageSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.currentPage()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).isEmpty();

        then(listRestaurantsByCuisineTypeUseCase).should().execute(any());
    }

    @Test
    @DisplayName("Deve deletar restaurante com sucesso")
    void shouldDeleteRestaurantSuccessfully() {
        // Arrange
        Long id = 1L;

        // Act
        restaurantController.deleteById(id);

        // Assert
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
        // Arrange
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("Error deleting restaurant");

        willThrow(expectedException).given(deleteRestaurantUseCase).execute(id);

        // Act & Assert
        assertThatThrownBy(() -> restaurantController.deleteById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error deleting restaurant");

        then(deleteRestaurantUseCase).should().execute(id);
    }
}
