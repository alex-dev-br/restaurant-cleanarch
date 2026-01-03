package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RoleOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para RolePresenter")
class RolePresenterTest {

    @Test
    @DisplayName("Deve converter Role para RoleOutput corretamente")
    void shouldConvertRoleToRoleOutput() {
        Long id = 1L;
        String name = "ADMIN";
        
        Role role = new Role(id, name);

        RoleOutput output = RolePresenter.toOutput(role);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(name);
    }
}
