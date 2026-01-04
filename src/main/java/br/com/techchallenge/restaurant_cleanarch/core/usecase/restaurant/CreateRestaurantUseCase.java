package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateRestaurantUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public CreateRestaurantUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway, UserGateway userGateway) {
        Objects.requireNonNull(loggedUserGateway, "LoggerUserGateway cannot be null");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        Objects.requireNonNull(userGateway, "UserGateway cannot be null");
        this.loggedUserGateway = loggedUserGateway;
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    public Restaurant execute(CreateRestaurantInput input) {
        Objects.requireNonNull(input, "CreateRestaurantInput cannot be null");

        if (!loggedUserGateway.hasRole(RestaurantRoles.CREATE_RESTAURANT))
            throw new OperationNotAllowedException("The current user does not have permission to create restaurants.");

        var owner = userGateway.findByUuid(input.owner()).orElseThrow(() -> new BusinessException("Owner not found."));

        if (!owner.canOwnRestaurant()) {
            throw new UserCannotBeRestaurantOwnerException();
        }

        if (restaurantGateway.existsRestaurantWithName(input.name())) {
            throw new RestaurantNameIsAlreadyInUseException();
        }

        var address = buildAddress(input.address());
        var openingHours = buildOpeningHours(input.openingHours());
        var menuItemsInput = buildMenu(input.menu());

        var restaurant = new Restaurant (null, input.name(), address, input.cuisineType(), openingHours, menuItemsInput, owner);

        return restaurantGateway.save(restaurant);
    }

    private Set<MenuItem> buildMenu(Set<MenuItemInput> menuItemsInput) {
        if (menuItemsInput == null) return Set.of();
        return menuItemsInput.stream().map(m -> new MenuItem(null, m.name(), m.description(), m.price(), m.restaurantOnly(), m.photoPath())).collect(Collectors.toSet());
    }

    private Set<OpeningHours> buildOpeningHours(Set<OpeningHoursInput> openingHours) {
        if (openingHours == null) return Set.of();
        return openingHours.stream().map(o -> new OpeningHours(null, o.dayOfDay(), o.openHour(), o.closeHour())).collect(Collectors.toSet());
    }

    private Address buildAddress(AddressInput addressInput) {
        return addressInput == null ? null : new Address(addressInput.street(), addressInput.number(), addressInput.city(), addressInput.state(), addressInput.zipCode(), addressInput.complement());
    }
}
