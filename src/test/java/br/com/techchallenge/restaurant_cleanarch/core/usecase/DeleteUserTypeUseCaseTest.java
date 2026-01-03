package br.com.techchallenge.restaurant_cleanarch.core.usecase;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype.DeleteUserTypeUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DeleteUserTypeUseCase")
class DeleteUserTypeUseCaseTest {

    @Mock
    private UserTypeGateway userTypeGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private DeleteUserTypeUseCase deleteUserTypeUseCase;

    @Test
    @DisplayName("Deve deletar UserType com sucesso")
    void shouldDeleteUserTypeSuccessfully() {
        Long id = 1L;
        UserType userType = new UserType(id, "Administrator", Set.of(new Role(1L, "ADMIN")));

        given(loggedUserGateway.hasRole(UserTypeRoles.DELETE_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(userType));
        given(userTypeGateway.isInUse(id)).willReturn(false);

        deleteUserTypeUseCase.execute(id);

        then(loggedUserGateway).should().hasRole(UserTypeRoles.DELETE_USER_TYPE);
        then(userTypeGateway).should().findById(id);
        then(userTypeGateway).should().isInUse(id);
        then(userTypeGateway).should().delete(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        Long id = 1L;
        given(loggedUserGateway.hasRole(UserTypeRoles.DELETE_USER_TYPE)).willReturn(false);

        assertThatThrownBy(() -> deleteUserTypeUseCase.execute(id))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to delete user types.");

        then(loggedUserGateway).should().hasRole(UserTypeRoles.DELETE_USER_TYPE);
        then(userTypeGateway).should(never()).findById(any());
        then(userTypeGateway).should(never()).isInUse(any());
        then(userTypeGateway).should(never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando UserType não é encontrado")
    void shouldThrowExceptionWhenUserTypeNotFound() {
        Long id = 1L;
        given(loggedUserGateway.hasRole(UserTypeRoles.DELETE_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> deleteUserTypeUseCase.execute(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User type not found.");

        then(loggedUserGateway).should().hasRole(UserTypeRoles.DELETE_USER_TYPE);
        then(userTypeGateway).should().findById(id);
        then(userTypeGateway).should(never()).isInUse(any());
        then(userTypeGateway).should(never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando UserType está em uso")
    void shouldThrowExceptionWhenUserTypeIsInUse() {
        Long id = 1L;
        UserType userType = new UserType(id, "Administrator", Set.of(new Role(1L, "ADMIN")));

        given(loggedUserGateway.hasRole(UserTypeRoles.DELETE_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(userType));
        given(userTypeGateway.isInUse(id)).willReturn(true);

        assertThatThrownBy(() -> deleteUserTypeUseCase.execute(id))
                .isInstanceOf(UserTypeInUseException.class);

        then(loggedUserGateway).should().hasRole(UserTypeRoles.DELETE_USER_TYPE);
        then(userTypeGateway).should().findById(id);
        then(userTypeGateway).should().isInUse(id);
        then(userTypeGateway).should(never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> deleteUserTypeUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Id of user type cannot be null.");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(userTypeGateway).should(never()).findById(any());
        then(userTypeGateway).should(never()).isInUse(any());
        then(userTypeGateway).should(never()).delete(any());
    }
}
