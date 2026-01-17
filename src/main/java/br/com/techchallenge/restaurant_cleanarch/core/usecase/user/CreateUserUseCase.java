package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.PasswordHasherGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;

public class CreateUserUseCase extends UseCaseBase<CreateUserInput, User> {

    private final UserGateway userGateway;
    private final UserTypeGateway userTypeGateway;
    private final PasswordHasherGateway passwordHasherGateway;

    public CreateUserUseCase(
            UserGateway userGateway,
            UserTypeGateway userTypeGateway,
            LoggedUserGateway loggedUserGateway,
            PasswordHasherGateway passwordHasherGateway
    ) {
        super(loggedUserGateway);
        Objects.requireNonNull(userGateway, "userGateway must not be null");
        Objects.requireNonNull(userTypeGateway, "userTypeGateway must not be null");
        Objects.requireNonNull(passwordHasherGateway, "passwordHasherGateway must not be null");

        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
        this.passwordHasherGateway = passwordHasherGateway;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return UserManagementRoles.CREATE_USER;
    }

    @Override
    public User doExecute(CreateUserInput input) {
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
