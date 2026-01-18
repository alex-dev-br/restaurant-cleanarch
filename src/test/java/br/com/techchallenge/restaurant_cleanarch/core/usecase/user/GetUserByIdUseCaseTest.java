package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetUserByIdUseCase")
class GetUserByIdUseCaseTest {

    @Mock private UserGateway userGateway;
    @Mock private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private GetUserByIdUseCase getUserByIdUseCase;

    @Test
    @DisplayName("Deve retornar usuário quando existir e usuário logado tiver permissão")
    void shouldReturnUserWhenExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User expectedUser = new UserBuilder().withId(userId).build();

        given(loggedUserGateway.hasRole(UserManagementRoles.VIEW_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.of(expectedUser));

        // Act
        var result = getUserByIdUseCase.execute(userId);

        // Assert
        assertThat(result).isNotEmpty().hasValue(expectedUser);

        then(loggedUserGateway).should().hasRole(UserManagementRoles.VIEW_USER);
        then(userGateway).should().findById(userId);
    }

    @Test
    @DisplayName("Deve retornar um optional vazio quando usuário não existir")
    void shouldThrowWhenUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();

        given(loggedUserGateway.hasRole(UserManagementRoles.VIEW_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.empty());

        // Act + Assert
        var result = getUserByIdUseCase.execute(userId);

        assertThat(result).isEmpty();

        then(loggedUserGateway).should().hasRole(UserManagementRoles.VIEW_USER);
        then(userGateway).should().findById(userId);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário logado não tiver permissão")
    void shouldThrowWhenNoPermission() {
        // Arrange
        UUID userId = UUID.randomUUID();

        given(loggedUserGateway.hasRole(UserManagementRoles.VIEW_USER)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> getUserByIdUseCase.execute(userId))
                .isInstanceOf(OperationNotAllowedException.class);

        then(loggedUserGateway).should().hasRole(UserManagementRoles.VIEW_USER);
        then(userGateway).should(never()).findById(any());
    }

    @Test
    @DisplayName("Construtor deve lançar NullPointerException quando loggedUserGateway for nulo")
    void constructorShouldThrowWhenLoggedUserGatewayIsNull() {
        // Arrange
        LoggedUserGateway nullLoggedUserGateway = null;

        // Act + Assert
        assertThatThrownBy(() -> new GetUserByIdUseCase(userGateway, nullLoggedUserGateway))
                .isInstanceOf(NullPointerException.class)
                // mensagem vem da superclasse UseCase (como você viu no ListUsersUseCaseTest)
                .hasMessage("LoggedUserGateway cannot be null.");
    }
}
