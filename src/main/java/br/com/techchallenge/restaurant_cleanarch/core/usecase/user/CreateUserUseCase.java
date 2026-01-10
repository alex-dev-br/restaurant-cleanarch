package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserInput;

import java.util.Objects;

public class CreateUserUseCase {

    private final UserGateway userGateway;
    private final UserTypeGateway userTypeGateway;
    private final LoggedUserGateway loggedUserGateway;
    private final PasswordHasherGateway passwordHasherGateway;

    public CreateUserUseCase(
            UserGateway userGateway,
            UserTypeGateway userTypeGateway,
            LoggedUserGateway loggedUserGateway,
            PasswordHasherGateway passwordHasherGateway
    ) {
        Objects.requireNonNull(userGateway, "userGateway must not be null");
        Objects.requireNonNull(userTypeGateway, "userTypeGateway must not be null");
        Objects.requireNonNull(loggedUserGateway, "loggedUserGateway must not be null");
        Objects.requireNonNull(passwordHasherGateway, "passwordHasherGateway must not be null");

        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
        this.loggedUserGateway = loggedUserGateway;
        this.passwordHasherGateway = passwordHasherGateway;
    }

    public User execute(CreateUserInput input) {
        Objects.requireNonNull(input, "createUserInput must not be null");

        if (!loggedUserGateway.hasRole(UserManagementRoles.CREATE_USER)) {
            throw new OperationNotAllowedException("The current user does not have permission to create users.");
        }

        if (input.password() == null || input.password().trim().isBlank()) {
            throw new BusinessException("Password cannot be blank.");
        }

        if (userGateway.existsUserWithEmail(input.email().trim())) {
            throw new BusinessException("Email is already in use.");
        }

        var userType = userTypeGateway.findById(input.userTypeId())
                .orElseThrow(() -> new BusinessException("User type with ID " + input.userTypeId() + " not found."));

        Address address = input.address() == null ? null : new Address(
                input.address().street(),
                input.address().number(),
                input.address().city(),
                input.address().state(),
                input.address().zipCode(),
                input.address().complement()
        );

        String passwordHash = passwordHasherGateway.hash(input.password().trim());

        var user = new User(
                null,
                input.name().trim(),
                input.email().trim(),
                address,
                userType,
                passwordHash
        );

        return userGateway.save(user);
    }
}
