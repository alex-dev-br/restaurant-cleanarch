package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeWithoutRolesException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para UserType")
class UserTypeTest {

    @Test
    @DisplayName("Deve criar UserType com sucesso quando dados são válidos")
    void shouldCreateUserTypeSuccessfully() {
        Long id = 1L;
        String name = "ADMIN";
        Role role = new Role(1L, "ADMIN_ROLE");
        Set<Role> roles = Set.of(role);

        UserType userType = new UserType(id, name, roles);

        assertThat(userType.getId()).isEqualTo(id);
        assertThat(userType.getName()).isEqualTo(name);
        assertThat(userType.getRoles()).containsExactly(role);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        Role role = new Role(1L, "ADMIN_ROLE");
        Set<Role> roles = Set.of(role);

        assertThatThrownBy(() -> new UserType(1L, null, roles))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Name cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando roles for nulo")
    void shouldThrowExceptionWhenRolesIsNull() {
        assertThatThrownBy(() -> new UserType(1L, "ADMIN", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Roles cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for vazio")
    void shouldThrowExceptionWhenNameIsBlank() {
        Role role = new Role(1L, "ADMIN_ROLE");
        Set<Role> roles = Set.of(role);

        assertThatThrownBy(() -> new UserType(1L, "   ", roles))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Name cannot be blank.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando roles estiver vazio")
    void shouldThrowExceptionWhenRolesIsEmpty() {
        Set<Role> emptyRoles = Collections.emptySet();

        assertThatThrownBy(() -> new UserType(1L, "ADMIN", emptyRoles))
                .isInstanceOf(UserTypeWithoutRolesException.class)
                .hasMessageContaining("User type must have at least one role valid.");
    }

    @Test
    @DisplayName("Deve garantir que a lista de roles é imutável")
    void shouldEnsureRolesIsImmutable() {
        Role role = new Role(1L, "ADMIN_ROLE");
        Set<Role> roles = Set.of(role);
        UserType userType = new UserType(1L, "ADMIN", roles);

        Role newRole = new Role(2L, "USER_ROLE");
        assertThatThrownBy(() -> userType.getRoles().add(newRole)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Deve verificar igualdade baseada no ID quando presente")
    void shouldVerifyEqualityBasedOnId() {
        Role role = new Role(1L, "ADMIN_ROLE");
        Set<Role> roles = Set.of(role);
        UserType userType1 = new UserType(1L, "ADMIN", roles);
        UserType userType2 = new UserType(1L, "OTHER_NAME", roles);

        assertThat(userType1).isEqualTo(userType2);
    }

    @Test
    @DisplayName("Deve verificar igualdade baseada no nome quando ID é nulo")
    void shouldVerifyEqualityBasedOnNameWhenIdIsNull() {
        Role role = new Role(1L, "ADMIN_ROLE");
        Set<Role> roles = Set.of(role);
        UserType userType1 = new UserType(null, "ADMIN", roles);
        UserType userType2 = new UserType(null, "ADMIN", roles);

        assertThat(userType1).isEqualTo(userType2).hasSameHashCodeAs(userType2);
    }

    @Test
    @DisplayName("Deve verificar desigualdade quando IDs são diferentes")
    void shouldVerifyInequalityWhenIdsAreDifferent() {
        Role role = new Role(1L, "ADMIN_ROLE");
        Set<Role> roles = Set.of(role);
        UserType userType1 = new UserType(1L, "ADMIN", roles);
        UserType userType2 = new UserType(2L, "ADMIN", roles);

        assertThat(userType1).isNotEqualTo(userType2);
    }

    @Test
    @DisplayName("Deve verificar que o hashCode é baseado apenas no nome")
    void shouldVerifyHashCodeIsBasedOnlyOnName() {
        Role role = new Role(1L, "ADMIN_ROLE");
        Set<Role> roles = Set.of(role);
        UserType userType1 = new UserType(1L, "ADMIN", roles);
        UserType userType2 = new UserType(2L, "ADMIN", roles);

        assertThat(userType1).hasSameHashCodeAs(userType2);
    }
}
