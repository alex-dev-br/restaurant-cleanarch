package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
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

import java.util.*;
import java.util.stream.Collectors;

public class CreateRestaurantUseCase extends UseCaseBase<CreateRestaurantInput, Restaurant> {

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public CreateRestaurantUseCase(
            LoggedUserGateway loggedUserGateway,
            RestaurantGateway restaurantGateway,
            UserGateway userGateway
    ) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null"));
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        this.userGateway = Objects.requireNonNull(userGateway, "UserGateway cannot be null");
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.CREATE_RESTAURANT;
    }

    @Override
    protected Restaurant doExecute(CreateRestaurantInput input) {
        Objects.requireNonNull(input, "CreateRestaurantInput cannot be null.");
        Objects.requireNonNull(input.ownerId(), "Owner id cannot be null.");
        Objects.requireNonNull(input.name(), "Restaurant name cannot be null.");

        if (restaurantGateway.existsRestaurantWithName(input.name())) {
            throw new RestaurantNameIsAlreadyInUseException();
        }

        var ownerId = input.ownerId();
        var owner = userGateway.findById(ownerId)
                .orElseThrow(() -> new BusinessException("Owner not found."));

        if (!owner.canOwnRestaurant()) {
            throw new UserCannotBeRestaurantOwnerException();
        }

        Map<UUID, User> usersCache = new HashMap<>();
        usersCache.put(ownerId, owner);

        Set<User> employees = Optional.ofNullable(input.employees())
                .orElse(Set.of())
                .stream()
                .map(id -> usersCache.computeIfAbsent(id, key ->
                        userGateway.findById(key).orElseThrow(() -> new BusinessException("Employee " + key + " not found."))))
                .collect(Collectors.toSet());

        var address = buildAddress(input.address());

        var restaurant = new Restaurant(null, input.name(), address, input.cuisineType(), owner);

        if (input.openingHours() != null) {
            input.openingHours().stream().map(this::buildOpeningHours).forEach(restaurant::addOpeningHours);
        }

        if (input.menu() != null) {
            input.menu().stream().map(this::buildMenu).forEach(restaurant::addMenuItem);
        }

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
        return input == null ? null : new OpeningHours(null, input.dayOfWeek(), input.openHour(), input.closeHour());
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