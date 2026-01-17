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
import br.com.techchallenge.restaurant_cleanarch.core.inbound.AddressInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateOpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseWithoutOutput;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateRestaurantUseCase extends UseCaseWithoutOutput<UpdateRestaurantInput> {

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public UpdateRestaurantUseCase(
            LoggedUserGateway loggedUserGateway,
            RestaurantGateway restaurantGateway,
            UserGateway userGateway
    ) {
        super(loggedUserGateway);
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        this.userGateway = Objects.requireNonNull(userGateway, "UserGateway cannot be null");
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.UPDATE_RESTAURANT;
    }

    @Override
    protected void doExecute(UpdateRestaurantInput input) {
        Objects.requireNonNull(input, "UpdateRestaurantInput cannot be null.");
        Long restaurantId = Objects.requireNonNull(input.id(), "Restaurant id cannot be null.");

        Restaurant current = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurant not found."));

        // ✅ regra nova do feat/employees (sem regredir patch semantics)
        User currentUser = loggedUserGateway.requireCurrentUser();
        if (!current.canBeManagedBy(currentUser)) {
            throw new OperationNotAllowedException();
        }

        // PATCH semantics
        String targetName = Optional.ofNullable(input.name()).orElse(current.getName());
        Address targetAddress = input.address() == null ? current.getAddress() : buildAddress(input.address());
        String targetCuisineType = Optional.ofNullable(input.cuisineType()).orElse(current.getCuisineType());

        UUID targetOwnerId = input.owner() == null ? current.getOwner().getId() : input.owner();
        User targetOwner = userGateway.findById(targetOwnerId)
                .orElseThrow(() -> new BusinessException("Owner not found."));

        if (!current.getName().equals(targetName) && restaurantGateway.existsRestaurantWithName(targetName)) {
            throw new RestaurantNameIsAlreadyInUseException();
        }

        if (!targetOwner.canOwnRestaurant()) {
            throw new UserCannotBeRestaurantOwnerException();
        }

        Set<OpeningHours> targetOpeningHours = input.openingHours() == null
                ? current.getOpeningHours()
                : input.openingHours().stream().map(this::buildOpeningHours).collect(Collectors.toSet());

        Set<MenuItem> targetMenu = input.menu() == null
                ? current.getMenuItems()
                : input.menu().stream().map(this::buildMenu).collect(Collectors.toSet());

        // cache para evitar consultar 2x (owner tb pode ser employee)
        Map<UUID, User> usersCache = new HashMap<>();
        usersCache.put(targetOwner.getId(), targetOwner);

        Set<User> targetEmployees = input.employees() == null
                ? current.getEmployees()
                : input.employees().stream()
                .map(employeeId -> usersCache.computeIfAbsent(employeeId, id ->
                        userGateway.findById(id)
                                .orElseThrow(() -> new BusinessException("Employee " + id + " not found."))))
                .collect(Collectors.toSet());

        Restaurant updated = new Restaurant(restaurantId, targetName, targetAddress, targetCuisineType, targetOwner);
        updated.addOpeningHours(targetOpeningHours);
        updated.addMenuItems(targetMenu);
        updated.addEmployees(targetEmployees);

        restaurantGateway.save(updated);
    }

    private OpeningHours buildOpeningHours(UpdateOpeningHoursInput input) {
        return input == null ? null : new OpeningHours(
                input.id(),
                input.dayOfDay(),
                input.openHour(),
                input.closeHour()
        );
    }

    private MenuItem buildMenu(UpdateMenuItemInput input) {
        return input == null ? null : new MenuItem(
                input.id(),
                input.name(),
                input.description(),
                input.price(),
                input.restaurantOnly(),
                input.photoPath()
        );
    }

    private Address buildAddress(AddressInput addressInput) {
        return addressInput == null ? null : new Address(
                addressInput.street(),
                addressInput.number(),
                addressInput.city(),
                addressInput.state(),
                addressInput.zipCode(),
                addressInput.complement()
        );
    }
}
