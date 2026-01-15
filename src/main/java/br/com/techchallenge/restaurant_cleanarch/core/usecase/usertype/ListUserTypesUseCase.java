package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.NoInput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.Set;


public class ListUserTypesUseCase extends UseCaseBase<NoInput, Set<UserType>> {

    private final UserTypeGateway userTypeGateway;



    public ListUserTypesUseCase(UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null."));
        this.userTypeGateway = Objects.requireNonNull(userTypeGateway, "UserTypeGateway cannot be null.");
    }

    // mantém API antiga (sem input)
    public Set<UserType> execute() {
        return super.execute(new NoInput());
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return UserTypeRoles.VIEW_USER_TYPE;
    }

    @Override
    protected Set<UserType> doExecute(NoInput input) {
        return userTypeGateway.findAll();
    }


}
