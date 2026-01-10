package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    @DisplayName("Deve atualizar restaurante com sucesso")
    void shouldUpdateRestaurantSuccessfully() {
        // Given
        var address = new AddressBuilder().build();
        var owner = new UserBuilder().withId(UUID.randomUUID()).withRole(UserRoles.RESTAURANT_OWNER).build();
        var openingHoursInput = new OpeningHoursBuilder().buildInput();
        var menu = new MenuItemBuilder().buildInput();

        var restaurant = new RestaurantBuilder()
                .withName("Old Name")
                .withAddress(address)
                .withCuisineType("Old Cuisine")
                .withOwner(owner)
                .build();

        var input = new UpdateRestaurantInput(
                1L,
                "New Name",
                new AddressBuilder().buildInput(),
                "New Cuisine",
                Set.of(openingHoursInput),
                Set.of(menu),
                owner.getId()
        );

        when(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).thenReturn(true);
        when(restaurantGateway.findById(1L)).thenReturn(Optional.of(restaurant));
        when(userGateway.findByUuid(any(UUID.class))).thenReturn(Optional.of(owner));

        // When
        updateRestaurantUseCase.execute(input);

        // Then
        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(anyLong());
        then(userGateway).should().findByUuid(any(UUID.class));
        then(restaurantGateway).should().save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        UpdateRestaurantInput input = new UpdateRestaurantInput(1L, "New Name", null, "New Cuisine", null, null, null);
        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to update restaurants.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should(never()).findById(anyLong());
        then(userGateway).should(never()).findByUuid(any(UUID.class));
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando restaurante não é encontrado")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        UpdateRestaurantInput input = new UpdateRestaurantInput(1L, "Restaurant", null, "New Cuisine", null, null, null);
        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurant not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(anyLong());
        then(userGateway).should(never()).findByUuid(any(UUID.class));
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quanto tenta alterar o nome para que já está sendo utilizado")
    void shouldThrowExceptionWhenRestaurantNameAlreadyInUseByOtherId() {
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var inputAddress = new AddressBuilder().buildInput();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                1L,
                "Restaurant",
                inputAddress,
                "New Cuisine",
                null,
                null,
                owner.getId());

        var address = new AddressBuilder().build();
        var restaurant = new RestaurantBuilder()
                .withName("Restaurant")
                .withAddress(address)
                .withCuisineType("Old Cuisine")
                .withOwner(owner)
                .build();


        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(userGateway.findByUuid(any(UUID.class))).willReturn(Optional.of(owner));
        given(restaurantGateway.findById(anyLong())).willReturn(Optional.of(restaurant));
        given(restaurantGateway.existsRestaurantWithName(anyString())).willReturn(true);

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class)
                .hasMessageContaining("Restaurant name is already in use");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(anyLong());
        then(restaurantGateway).should().existsRestaurantWithName(anyString());
        then(userGateway).should().findByUuid(any(UUID.class));
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Dele lançar exceção quando o dono sendo alterado não tem role de dono")
    void shouldThrowExceptionWhenUserCannotBeOwner() {
        UUID newOwnerUuid = UUID.randomUUID();
        var inputAddress = new AddressBuilder().buildInput();
        UpdateRestaurantInput input = new UpdateRestaurantInput(1L, "Restaurant", inputAddress, "New Cuisine", null, null, newOwnerUuid);

        var newOwner = new UserBuilder().withId(newOwnerUuid).build();

        var address = new AddressBuilder().build();
        var owner = new UserBuilder().withRole(UserRoles.RESTAURANT_OWNER).build();
        var restaurant = new RestaurantBuilder()
                .withName("Restaurant II")
                .withAddress(address)
                .withCuisineType("Old Cuisine")
                .withOwner(owner)
                .build();

        when(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).thenReturn(true);
        when(restaurantGateway.findById(anyLong())).thenReturn(Optional.of(restaurant));
        when(restaurantGateway.existsRestaurantWithName(anyString())).thenReturn(false);
        when(userGateway.findByUuid(newOwnerUuid)).thenReturn(Optional.of(newOwner));

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class)
                .hasMessageContaining("User cannot be restaurant owner");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(anyLong());
        then(restaurantGateway).should().existsRestaurantWithName(anyString());
        then(userGateway).should().findByUuid(newOwnerUuid);
        then(restaurantGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando input é nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("UpdateRestaurantInput cannot be null");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(restaurantGateway).should(never()).findById(anyLong());
        then(userGateway).should(never()).findByUuid(any(UUID.class));
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any());
    }
}
