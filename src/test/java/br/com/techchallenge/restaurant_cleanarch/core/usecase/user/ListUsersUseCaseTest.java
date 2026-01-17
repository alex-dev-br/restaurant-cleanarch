package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListUsersUseCase")
class ListUsersUseCaseTest {

    @Mock private UserGateway userGateway;
    @Mock private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListUsersUseCase listUsersUseCase;

    @Captor
    private ArgumentCaptor<PagedQuery<Void>> pagedQueryCaptor;

    @Test
    @DisplayName("Deve listar usuários paginados quando usuário logado tiver permissão")
    void shouldListUsersPagedSuccessfully() {
        // Arrange
        var pagedQuery = new PagedQuery<Void>(null, 0, 10);

        var user1 = new UserBuilder().build();
        var user2 = new UserBuilder().copy().withEmail("ana@example.com").build();

        Page<User> expectedPage = new Page<>(
                0,
                10,
                2,
                List.of(user1, user2)
        );

        given(loggedUserGateway.hasRole(UserManagementRoles.VIEW_USER)).willReturn(true);
        given(userGateway.findAll(any())).willReturn(expectedPage);

        // Act
        Page<User> result = listUsersUseCase.execute(pagedQuery);

        // Assert
        assertThat(result).isNotNull().isSameAs(expectedPage);
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.content()).containsExactly(user1, user2);

        then(loggedUserGateway).should().hasRole(UserManagementRoles.VIEW_USER);
        then(userGateway).should().findAll(pagedQueryCaptor.capture());

        assertThat(pagedQueryCaptor.getValue().pageNumber()).isEqualTo(0);
        assertThat(pagedQueryCaptor.getValue().pageSize()).isEqualTo(10);
        assertThat(pagedQueryCaptor.getValue().filter()).isNull();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário logado não tiver permissão")
    void shouldThrowWhenNoPermission() {
        // Arrange
        var pagedQuery = new PagedQuery<Void>(null, 0, 10);
        given(loggedUserGateway.hasRole(UserManagementRoles.VIEW_USER)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> listUsersUseCase.execute(pagedQuery))
                .isInstanceOf(OperationNotAllowedException.class);

        then(loggedUserGateway).should().hasRole(UserManagementRoles.VIEW_USER);
        then(userGateway).should(never()).findAll(any());
    }

    @Test
    @DisplayName("Construtor deve lançar NullPointerException quando loggedUserGateway for nulo")
    void constructorShouldThrowWhenLoggedUserGatewayIsNull() {
        // Arrange
        LoggedUserGateway nullLoggedUserGateway = null;

        // Act + Assert
        assertThatThrownBy(() -> new ListUsersUseCase(userGateway, nullLoggedUserGateway))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("LoggedUserGateway")
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Deve delegar erro do PagedQuery quando pageNumber for inválido")
    void shouldThrowWhenPagedQueryHasInvalidPageNumber() {
        // Arrange + Act + Assert
        assertThatThrownBy(() -> new PagedQuery<Void>(null, -1, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pageNumber must be >= 0");
    }

    @Test
    @DisplayName("Deve delegar erro do PagedQuery quando pageSize for inválido")
    void shouldThrowWhenPagedQueryHasInvalidPageSize() {
        // Arrange + Act + Assert
        assertThatThrownBy(() -> new PagedQuery<Void>(null, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pageSize must be > 0");
    }
}
