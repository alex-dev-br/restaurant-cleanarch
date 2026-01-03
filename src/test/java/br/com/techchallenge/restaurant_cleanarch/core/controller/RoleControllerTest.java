package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RoleOutput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.role.GetAllRolesUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RoleController")
class RoleControllerTest {

    @Mock
    private GetAllRolesUseCase getAllRolesUseCase;

    @InjectMocks
    private RoleController roleController;

    @Test
    @DisplayName("Deve retornar todas as roles com sucesso")
    void shouldGetAllRolesSuccessfully() {
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(2L, "USER");
        Set<Role> roles = Set.of(role1, role2);

        given(getAllRolesUseCase.execute()).willReturn(roles);

        Set<RoleOutput> result = roleController.getAllRoles();

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result).extracting(RoleOutput::name).containsExactlyInAnyOrder("ADMIN", "USER");

        then(getAllRolesUseCase).should().execute();
    }

    @Test
    @DisplayName("Deve lançar exceção quando GetAllRolesUseCase lança exceção")
    void shouldThrowExceptionWhenGetAllUseCaseThrowsException() {
        RuntimeException expectedException = new RuntimeException("Error fetching roles");

        given(getAllRolesUseCase.execute()).willThrow(expectedException);

        assertThatThrownBy(() -> roleController.getAllRoles())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error fetching roles");

        then(getAllRolesUseCase).should().execute();
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetAllRolesUseCase nulo")
    void shouldThrowExceptionWhenGetAllUseCaseIsNull() {
        assertThatThrownBy(() -> new RoleController(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetAllRolesUseCase cannot be null.");
    }
}
