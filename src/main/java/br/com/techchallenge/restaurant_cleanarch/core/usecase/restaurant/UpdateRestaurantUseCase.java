package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.*;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateRestaurantUseCase extends UseCaseBase<UpdateRestaurantInput, Void> {

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public UpdateRestaurantUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway, UserGateway userGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        Objects.requireNonNull(userGateway, "UserGateway cannot be null");
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    protected Void doExecute(UpdateRestaurantInput input) {
        var restaurant = restaurantGateway.findById(input.id()).orElseThrow(() -> new BusinessException("Restaurant not found."));
        User owner = userGateway.findById(input.owner()).orElseThrow(() -> new BusinessException("Owner not found."));

        if (!restaurant.getName().equals(input.name()) && restaurantGateway.existsRestaurantWithName(input.name())) {
            throw new RestaurantNameIsAlreadyInUseException();
        }

        if (!owner.canOwnRestaurant()) {
            throw new UserCannotBeRestaurantOwnerException();
        }

        var employees = Optional.ofNullable(input.employees())
                .orElse(Set.of())
                .stream()
                .map(e -> userGateway.findById(e).orElseThrow(() -> new BusinessException("Employee "+ e + " not found.")))
                .collect(Collectors.toSet());

        var address = buildAddress(input.address());
        var openingHoursInput = Optional.ofNullable(input.openingHours());
        var menuItemsInput = Optional.ofNullable(input.menu());

        var restaurantToUpdate = new Restaurant(input.id(), input.name(), address, input.cuisineType(), owner);
        openingHoursInput.ifPresent(o -> o.stream().map(this::buildOpeningHours).forEach(restaurantToUpdate::addOpeningHours));
        menuItemsInput.ifPresent(m -> m.stream().map(this::buildMenu).forEach(restaurantToUpdate::addMenuItem));
        restaurantToUpdate.addEmployees(employees);

        restaurantGateway.save(restaurantToUpdate);
        return null;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.UPDATE_RESTAURANT;
    }

    private MenuItem buildMenu(UpdateMenuItemInput input) {
        return input == null ? null : new MenuItem (
                input.id(),
                input.name(),
                input.description(),
                input.price(),
                input.restaurantOnly(),
                input.photoPath()
        );
    }

    private OpeningHours buildOpeningHours(UpdateOpeningHoursInput input) {
        return input == null ? null : new OpeningHours(input.id(), input.dayOfDay(), input.openHour(), input.closeHour());
    }

    private Address buildAddress(AddressInput input) {
        return input == null ? null : new Address(
                input.street(),
                input.number(),
                input.city(),
                input.state(),
                input.zipCode(),
                input.complement()
        );
    }
}
