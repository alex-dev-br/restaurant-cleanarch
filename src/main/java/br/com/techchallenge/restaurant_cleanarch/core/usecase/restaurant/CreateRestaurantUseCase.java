package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.AddressInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.MenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.OpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateRestaurantUseCase extends UseCaseBase<CreateRestaurantInput, Restaurant> {

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public CreateRestaurantUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway, UserGateway userGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(loggedUserGateway, "LoggerUserGateway cannot be null");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        Objects.requireNonNull(userGateway, "UserGateway cannot be null");
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.CREATE_RESTAURANT;
    }

    @Override
    protected Restaurant doExecute(CreateRestaurantInput input) {
        var owner = userGateway.findById(input.owner()).orElseThrow(() -> new BusinessException("Owner not found."));

        if (!owner.canOwnRestaurant()) {
            throw new UserCannotBeRestaurantOwnerException();
        }

        if (restaurantGateway.existsRestaurantWithName(input.name())) {
            throw new RestaurantNameIsAlreadyInUseException();
        }

        var employees = Optional.ofNullable(input.employees())
                .orElse(Set.of())
                .stream()
                .map(e -> userGateway.findById(e).orElseThrow(() -> new BusinessException("Employee "+ e +" not found.")))
                .collect(Collectors.toSet());

        var address = buildAddress(input.address());
        var openingHoursInput = Optional.ofNullable(input.openingHours());
        var menuItemsInput = Optional.ofNullable(input.menu());

        var restaurant = new Restaurant(null, input.name(), address, input.cuisineType(), owner);
        openingHoursInput.ifPresent(o -> o.stream().map(this::buildOpeningHours).forEach(restaurant::addOpeningHours));
        menuItemsInput.ifPresent(m -> m.stream().map(this::buildMenu).forEach(restaurant::addMenuItem));
        restaurant.addEmployees(employees);

        return restaurantGateway.save(restaurant);
    }

    private MenuItem buildMenu(MenuItemInput input) {
        return input == null ? null : new MenuItem (
                null,
                input.name(),
                input.description(),
                input.price(),
                input.restaurantOnly(),
                input.photoPath()
        );
    }

    private OpeningHours buildOpeningHours(OpeningHoursInput input) {
        return input == null ? null : new OpeningHours(null, input.dayOfDay(), input.openHour(), input.closeHour());
    }

    private Address buildAddress(AddressInput addressInput) {
        return addressInput == null
                ? null
                : new Address(
                addressInput.street(),
                addressInput.number(),
                addressInput.city(),
                addressInput.state(),
                addressInput.zipCode(),
                addressInput.complement()
        );
    }
}