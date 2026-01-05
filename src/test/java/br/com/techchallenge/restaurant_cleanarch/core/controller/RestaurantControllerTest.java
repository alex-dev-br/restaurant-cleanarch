package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.OpeningHoursBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantOutput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.CreateRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.GetByIdRestaurantUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RestaurantController")
class RestaurantControllerTest {

    @Mock
    private CreateRestaurantUseCase createRestaurantUseCase;

    @Mock
    private GetByIdRestaurantUseCase getByIdRestaurantUseCase;

    @InjectMocks
    private RestaurantController restaurantController;

    @Captor
    private ArgumentCaptor<CreateRestaurantInput> createRestaurantInputCaptor;

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
        assertThatThrownBy(() -> new RestaurantController(null, getByIdRestaurantUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetByIdRestaurantUseCase nulo")
    void shouldThrowExceptionWhenGetByIdUseCaseIsNull() {
        assertThatThrownBy(() -> new RestaurantController(createRestaurantUseCase, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetByIdRestaurantUseCase cannot be null.");
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao chamar createRestaurant com input nulo")
    void shouldThrowExceptionWhenCreateRestaurantInputIsNull() {
        assertThatThrownBy(() -> restaurantController.createRestaurant(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateRestaurantInput cannot be null.");
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

        given(getByIdRestaurantUseCase.execute(id)).willReturn(restaurant);

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

        then(getByIdRestaurantUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando GetByIdRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenGetByIdUseCaseThrowsException() {
        // Arrange
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("Restaurant not found");

        given(getByIdRestaurantUseCase.execute(id)).willThrow(expectedException);

        // Act & Assert
        assertThatThrownBy(() -> restaurantController.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Restaurant not found");

        then(getByIdRestaurantUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar findById com ID nulo")
    void shouldThrowExceptionWhenFindByIdWithNullId() {
        assertThatThrownBy(() -> restaurantController.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");
    }
}
