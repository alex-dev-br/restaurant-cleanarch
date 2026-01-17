package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.ResourceNotFoundException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseWithoutOutput;

import java.util.Objects;
import java.util.UUID;

public class UpdateUserUseCase extends UseCaseWithoutOutput<UpdateUserInput> {

    private final UserGateway userGateway;
    private final UserTypeGateway userTypeGateway;

    public UpdateUserUseCase(UserGateway userGateway, UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(userGateway, "userGateway must not be null");
        Objects.requireNonNull(userTypeGateway, "userTypeGateway must not be null");
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return UserManagementRoles.UPDATE_USER;
    }

    @Override
    public void doExecute(UpdateUserInput input) {
        UUID id = Objects.requireNonNull(input.id(), "User UUID cannot be null.");
        User currentUser = userGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));

        String newName = input.name() == null ? currentUser.getName() : input.name().trim();
        String newEmail = input.email() == null ? currentUser.getEmail() : input.email().trim();

        if (newEmail.isBlank()) {
            throw new BusinessException("Email cannot be blank.");
        }

        // Se mudar o email, verificar se já existe outro usuário com esse email
        if (!currentUser.getEmail().equalsIgnoreCase(newEmail)
            && userGateway.existsUserWithEmail(newEmail)) {
            throw new BusinessException("Email " + newEmail + " is already in use.");
        }

        Address newAddress = currentUser.getAddress();
        if (input.address() != null) {
            newAddress = new Address(
                    input.address().street(),
                    input.address().number(),
                    input.address().city(),
                    input.address().state(),
                    input.address().zipCode(),
                    input.address().complement()
            );
        }

        var newUserType = currentUser.getUserType();
        if (input.userTypeId() != null && !Objects.equals(input.userTypeId(), currentUser.getUserType().getId())) {
            newUserType = userTypeGateway.findById(input.userTypeId())
                    .orElseThrow(() -> new BusinessException("User type with ID " + input.userTypeId() + " not found."));
        }

        User updatedUser = new User(
                currentUser.getId(),
                newName,
                newEmail,
                newAddress,
                newUserType,
                currentUser.getPasswordHash()   // senha não é alterada aqui (fluxo separado para isso)
        );

        userGateway.save(updatedUser);
    }

}
