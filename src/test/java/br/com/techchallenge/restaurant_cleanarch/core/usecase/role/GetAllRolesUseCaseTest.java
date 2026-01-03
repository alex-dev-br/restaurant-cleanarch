package br.com.techchallenge.restaurant_cleanarch.core.usecase.role;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RoleRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetAllRolesUseCase")
class GetAllRolesUseCaseTest {

    @Mock
    private RoleGateway roleGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private GetAllRolesUseCase getAllRolesUseCase;

    @Test
    @DisplayName("Deve retornar todas as Roles com sucesso")
    void shouldReturnAllRolesSuccessfully() {
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(2L, "USER");
        Set<Role> expectedRoles = Set.of(role1, role2);

        given(loggedUserGateway.hasRole(RoleRoles.VIEW_ROLE)).willReturn(true);
        given(roleGateway.findAll()).willReturn(expectedRoles);

        Set<Role> result = getAllRolesUseCase.execute();

        assertThat(result).isNotNull().hasSize(2).containsExactlyInAnyOrder(role1, role2);

        then(loggedUserGateway).should().hasRole(RoleRoles.VIEW_ROLE);
        then(roleGateway).should().findAll();
    }

    @Test
    @DisplayName("Deve retornar conjunto vazio quando não houver Roles")
    void shouldReturnEmptySetWhenNoRolesFound() {
        given(loggedUserGateway.hasRole(RoleRoles.VIEW_ROLE)).willReturn(true);
        given(roleGateway.findAll()).willReturn(Collections.emptySet());

        Set<Role> result = getAllRolesUseCase.execute();

        assertThat(result).isNotNull().isEmpty();

        then(loggedUserGateway).should().hasRole(RoleRoles.VIEW_ROLE);
        then(roleGateway).should().findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        given(loggedUserGateway.hasRole(RoleRoles.VIEW_ROLE)).willReturn(false);

        assertThatThrownBy(() -> getAllRolesUseCase.execute())
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to get all roles.");

        then(loggedUserGateway).should().hasRole(RoleRoles.VIEW_ROLE);
        then(roleGateway).should(never()).findAll();
    }
}
