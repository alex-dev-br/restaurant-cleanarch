package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.RoleMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import({RoleAdapter.class})
@ComponentScan(basePackageClasses = {RoleMapper.class})
@DisplayName("Testes de Integração para RoleAdapter")
@Sql(scripts = "/roles/CREATE_ROLES.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/roles/CLEAR_ROLES.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RoleAdapterTest {

    @Autowired
    private RoleAdapter adapter;

    @Test
    @DisplayName("Deve retornar roles pelos nomes fornecidos")
    void shouldGetRolesByNameSuccessfully() {
        // Given
        Set<String> rolesNames = Set.of("VIEW_USER_TYPE", "VIEW_ROLE");

        // When
        Set<Role> roles = adapter.getRolesByName(rolesNames);

        // Then
        assertThat(roles).hasSize(2);
        assertThat(roles).extracting(Role::name).containsExactlyInAnyOrder("VIEW_USER_TYPE", "VIEW_ROLE");
    }

    @Test
    @DisplayName("Deve retornar conjunto vazio se nomes de roles não forem encontrados")
    void shouldReturnEmptySetWhenRolesNotFound() {
        // Given
        Set<String> rolesNames = Set.of("NON_EXISTENT");

        // When
        Set<Role> roles = adapter.getRolesByName(rolesNames);

        // Then
        assertThat(roles).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar conjunto vazio se lista de nomes for vazia")
    void shouldReturnEmptySetWhenRolesNamesIsEmpty() {
        // Given
        Set<String> rolesNames = Set.of();

        // When
        Set<Role> roles = adapter.getRolesByName(rolesNames);

        // Then
        assertThat(roles).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar exceção se lista de nomes for nula")
    void shouldThrowExceptionWhenRolesNamesIsNull() {
        assertThatThrownBy(() -> adapter.getRolesByName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("RolesName cannot be null.");
    }

    @Test
    @DisplayName("Deve retornar todas as roles")
    void shouldReturnAllRoles() {
        // When
        Set<Role> allRoles = adapter.findAll();

        // Then
        assertThat(allRoles).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allRoles).extracting(Role::name).contains("VIEW_USER_TYPE", "VIEW_ROLE");
    }
}
