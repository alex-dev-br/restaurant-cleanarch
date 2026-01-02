package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para Role")
class RoleTest {

    @Test
    @DisplayName("Deve criar Role com sucesso quando dados são válidos")
    void shouldCreateRoleSuccessfully() {
        Long id = 1L;
        String name = "ADMIN";

        Role role = new Role(id, name);

        assertThat(role.id()).isEqualTo(id);
        assertThat(role.name()).isEqualTo(name);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThatThrownBy(() -> new Role(1L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Role name cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for vazio")
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThatThrownBy(() -> new Role(1L, "   "))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Role name cannot be blank.");
    }
}
