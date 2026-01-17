package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.ResourceNotFoundException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DeleteUserUseCase")
class DeleteUserUseCaseTest {

    @Mock private UserGateway userGateway;
    @Mock private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    @Test
    @DisplayName("Deve deletar usuário quando existir e usuário logado tiver permissão")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = new UserBuilder().withId(userId).build();

        given(loggedUserGateway.hasRole(UserManagementRoles.DELETE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.of(existingUser));

        // Act
        deleteUserUseCase.execute(userId);

        // Assert
        then(loggedUserGateway).should().hasRole(UserManagementRoles.DELETE_USER);
        then(userGateway).should().findById(userId);
        then(userGateway).should().deleteById(userId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não existir")
    void shouldThrowWhenUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();

        given(loggedUserGateway.hasRole(UserManagementRoles.DELETE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> deleteUserUseCase.execute(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with ID " + userId + " not found.");

        then(loggedUserGateway).should().hasRole(UserManagementRoles.DELETE_USER);
        then(userGateway).should().findById(userId);
        then(userGateway).should(never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário logado não tiver permissão")
    void shouldThrowWhenNoPermission() {
        // Arrange
        UUID userId = UUID.randomUUID();
        given(loggedUserGateway.hasRole(UserManagementRoles.DELETE_USER)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> deleteUserUseCase.execute(userId))
                .isInstanceOf(OperationNotAllowedException.class);

        then(loggedUserGateway).should().hasRole(UserManagementRoles.DELETE_USER);
        then(userGateway).should(never()).findById(any());
        then(userGateway).should(never()).deleteById(any());
    }
}

