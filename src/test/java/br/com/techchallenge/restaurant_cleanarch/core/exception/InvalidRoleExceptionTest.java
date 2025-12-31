package br.com.techchallenge.restaurant_cleanarch.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para InvalidRoleException")
class InvalidRoleExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem correta e lista de roles")
    void shouldCreateExceptionWithCorrectMessageAndRoles() {
        Set<String> roles = Set.of("ADMIN", "USER");
        InvalidRoleException exception = new InvalidRoleException(roles);

        assertThat(exception.getMessage())
                .isNotNull()
                .contains("Invalid roles:")
                .contains("ADMIN")
                .contains("USER");

        assertThat(exception.getInvalidRoles())
                .containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a lista de roles for nula")
    void shouldThrowExceptionWhenRolesIsNull() {
        assertThatThrownBy(() -> new InvalidRoleException(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Set of invalid roles cannot be null.");
    }

    @Test
    @DisplayName("Deve criar um conjunto imutável de roles")
    void shouldCreateImmutableSetOfRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        InvalidRoleException exception = new InvalidRoleException(roles);

        assertThatThrownBy(() -> exception.getInvalidRoles().add("USER"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
