package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para UserTypePresenter")
class UserTypePresenterTest {

    @Test
    @DisplayName("Deve converter UserType para UserTypeOutput corretamente")
    void shouldConvertUserTypeToUserTypeOutput() {
        Long id = 1L;
        String name = "Administrator";
        String roleName1 = "ADMIN";
        String roleName2 = "USER";
        
        Role role1 = new Role(1L, roleName1);
        Role role2 = new Role(2L, roleName2);
        Set<Role> roles = Set.of(role1, role2);
        
        UserType userType = new UserType(id, name, roles);

        UserTypeOutput output = UserTypePresenter.toOutput(userType);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(name);
        assertThat(output.roles()).containsExactlyInAnyOrder(roleName1, roleName2);
    }
}
