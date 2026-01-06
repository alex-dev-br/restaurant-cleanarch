package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
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

    @Test
    @DisplayName("Deve criar restaurante com sucesso")
    void shouldCreateRestaurantSuccessfully() {
        UUID ownerId = UUID.randomUUID();
        var address = new AddressBuilder().buildInput();
        var menuItem = new MenuItemBuilder().buildInput();
        var openingHoursTuesday = new OpeningHoursBuilder().withDayOfDay(DayOfWeek.TUESDAY).buildInput();
        var openingHoursFriday = new OpeningHoursBuilder().buildInput();

        CreateRestaurantInput input = new CreateRestaurantInput(
                "My Restaurant",
                address,
                "Italian",
                Set.of(openingHoursTuesday, openingHoursFriday),
                Set.of(menuItem),
                ownerId
        );

        var addressInput = input.address();
        User owner = mock(User.class);
        given(owner.canOwnRestaurant()).willReturn(true);

        // Restaurante sem menu (primeiro save) — ID ainda null
        Restaurant restaurantWithoutMenu = new RestaurantBuilder()
                .withName(input.name())
                .withAddress(new Address(
                        addressInput.street(), addressInput.number(), addressInput.city(), addressInput.state(), addressInput.zipCode(), addressInput.complement()
                ))
                .withCuisineType(input.cuisineType())
                .withOpeningHours(Set.of(
                        new OpeningHours(null, openingHoursTuesday.dayOfDay(), openingHoursTuesday.openHour(), openingHoursTuesday.closeHour()),
                        new OpeningHours(null, openingHoursFriday.dayOfDay(), openingHoursFriday.openHour(), openingHoursFriday.closeHour())
                ))
                .withMenu(Set.of())
                .withOwner(owner)
                .build();

// Restaurante com ID 1L (retorno do primeiro save)
        Restaurant restaurantWithId = new RestaurantBuilder()
                .withId(1L)  // Agora com ID
                .withName(input.name())
                .withAddress(new Address(
                        addressInput.street(), addressInput.number(), addressInput.city(), addressInput.state(), addressInput.zipCode(), addressInput.complement()
                ))
                .withCuisineType(input.cuisineType())
                .withOpeningHours(Set.of(
                        new OpeningHours(null, openingHoursTuesday.dayOfDay(), openingHoursTuesday.openHour(), openingHoursTuesday.closeHour()),
                        new OpeningHours(null, openingHoursFriday.dayOfDay(), openingHoursFriday.openHour(), openingHoursFriday.closeHour())
                ))
                .withMenu(Set.of())
                .withOwner(owner)
                .build();

// Restaurante final com menu (segundo save)
        Restaurant finalRestaurant = new RestaurantBuilder()
                .withId(1L)
                .withName(input.name())
                .withAddress(new Address(
                        addressInput.street(), addressInput.number(), addressInput.city(), addressInput.state(), addressInput.zipCode(), addressInput.complement()
                ))
                .withCuisineType(input.cuisineType())
                .withOpeningHours(Set.of(
                        new OpeningHours(null, openingHoursTuesday.dayOfDay(), openingHoursTuesday.openHour(), openingHoursTuesday.closeHour()),
                        new OpeningHours(null, openingHoursFriday.dayOfDay(), openingHoursFriday.openHour(), openingHoursFriday.closeHour())
                ))
                .withMenu(Set.of(
                        new MenuItemBuilder()
                                .withId(1L)
                                .withName(menuItem.name())
                                .withDescription(menuItem.description())
                                .withPrice(menuItem.price())
                                .withRestaurantOnly(menuItem.restaurantOnly())
                                .withPhotoPath(menuItem.photoPath())
                                .build()
                ))
                .withOwner(owner)
                .build();

// Mock das duas chamadas ao save (ordem importa)
        given(restaurantGateway.save(any(Restaurant.class)))
                .willReturn(restaurantWithId)   // primeira chamada: retorna com ID
                .willReturn(finalRestaurant);   // segunda chamada: retorna com menu

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findByUuid(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.existsRestaurantWithName(input.name())).willReturn(false);

        // When
        Restaurant result = createRestaurantUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(input.name());
        assertThat(result.getMenu()).hasSize(1);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should().findByUuid(ownerId);
        then(restaurantGateway).should().existsRestaurantWithName(input.name());
        then(restaurantGateway).should(times(2)).save(any(Restaurant.class));  // ← Agora espera 2 chamadas
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        CreateRestaurantInput input = mock(CreateRestaurantInput.class);
        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to create restaurants.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.CREATE_RESTAURANT);
        then(userGateway).should(never()).findByUuid(any());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando dono não é encontrado")
    void shouldThrowExceptionWhenOwnerNotFound() {
        UUID ownerId = UUID.randomUUID();

        CreateRestaurantInput input = createValidRestaurantInput(ownerId);

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findByUuid(ownerId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Owner not found.");

        then(userGateway).should().findByUuid(ownerId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não pode ser dono de restaurante")
    void shouldThrowExceptionWhenUserCannotBeOwner() {
        UUID ownerId = UUID.randomUUID();

        CreateRestaurantInput input = createValidRestaurantInput(ownerId);
        User owner = mock(User.class);

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findByUuid(ownerId)).willReturn(Optional.of(owner));
        given(owner.canOwnRestaurant()).willReturn(false);

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class);

        then(userGateway).should().findByUuid(ownerId);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do restaurante já existe")
    void shouldThrowExceptionWhenRestaurantNameAlreadyExists() {
        UUID ownerId = UUID.randomUUID();

        CreateRestaurantInput input = createValidRestaurantInput(ownerId);
        User owner = mock(User.class);

        given(loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT)).willReturn(true);
        given(userGateway.findByUuid(ownerId)).willReturn(Optional.of(owner));
        given(owner.canOwnRestaurant()).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(input.name())).willReturn(true);

        assertThatThrownBy(() -> createRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class);

        then(restaurantGateway).should().existsRestaurantWithName(input.name());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando input é nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> createRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateRestaurantInput cannot be null");

        then(loggedUserGateway).should(never()).hasRole(any());
    }

    private CreateRestaurantInput createValidRestaurantInput(UUID ownerId) {
        var address = new AddressBuilder().buildInput();
        var menuItem = new MenuItemBuilder().buildInput();
        var openingHoursTuesday = new OpeningHoursBuilder().withDayOfDay(DayOfWeek.TUESDAY).buildInput();
        var openingHoursFriday = new OpeningHoursBuilder().buildInput();

        return new CreateRestaurantInput(
                "My Restaurant",
                address,
                "Italian",
                Set.of(openingHoursTuesday, openingHoursFriday),
                Set.of(menuItem),
                ownerId
        );
    }
}
