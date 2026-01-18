package br.com.techchallenge.restaurant_cleanarch.infra.auth;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoggedUserGatewayAdapterTest {

    private LoggedUserGatewayAdapter adapter;
    private UserMapper userMapperMock;

    @BeforeEach
    void setUp() {
        userMapperMock = Mockito.mock(UserMapper.class);
        adapter = new LoggedUserGatewayAdapter(userMapperMock);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_deveRetornarEmpty_quandoNaoHaAuthentication() {
        // Arrange
        // (nenhum authentication no context)

        // Act
        Optional<User> result = adapter.getCurrentUser();

        // Assert
        assertThat(result).isEmpty();
        verifyNoInteractions(userMapperMock);
    }

    @Test
    void getCurrentUser_deveRetornarEmpty_quandoPrincipalEhStringInvalida() {
        // Arrange
        var auth = new UsernamePasswordAuthenticationToken("not-a-uuid", "n/a");
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Optional<User> result = adapter.getCurrentUser();

        // Assert
        assertThat(result).isEmpty();
        verifyNoInteractions(userMapperMock);
    }

    @Test
    void hasRole_deveRetornarFalse_quandoRoleNameEhNull() {
        // Arrange
        ForGettingRoleName role = null;

        // Act
        boolean result = adapter.hasRole(role);

        // Assert
        assertThat(result).isFalse();
        verifyNoInteractions(userMapperMock);
    }

    @Test
    void hasRole_deveRetornarFalse_quandoNaoHaAuthentication() {
        // Arrange
        ForGettingRoleName role = () -> "ROLE_ANY";
        // (nenhum authentication no context)

        // Act
        boolean result = adapter.hasRole(role);

        // Assert
        assertThat(result).isFalse();
        verifyNoInteractions(userMapperMock);
    }

    @Test
    void hasRole_deveRetornarTrue_quandoAuthorityExiste() {
        // Arrange
        ForGettingRoleName role = () -> "ROLE_ADMIN";
        var auth = new UsernamePasswordAuthenticationToken(
                "11111111-1111-1111-1111-111111111111",
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        boolean result = adapter.hasRole(role);

        // Assert
        assertThat(result).isTrue();
        verifyNoInteractions(userMapperMock);
    }

    @Test
    void hasRole_deveRetornarFalse_quandoAuthorityNaoExiste() {
        // Arrange
        ForGettingRoleName role = () -> "ROLE_MANAGER";
        var auth = new UsernamePasswordAuthenticationToken(
                "11111111-1111-1111-1111-111111111111",
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        boolean result = adapter.hasRole(role);

        // Assert
        assertThat(result).isFalse();
        verifyNoInteractions(userMapperMock);
    }

    @Test
    void getCurrentUser_deveRetornarEmpty_quandoNaoAutenticado() {
        // Arrange
        var auth = new UsernamePasswordAuthenticationToken(
                "11111111-1111-1111-1111-111111111111",
                "n/a"
        );
        auth.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Optional<User> result = adapter.getCurrentUser();

        // Assert
        assertThat(result).isEmpty();
        verifyNoInteractions(userMapperMock);
    }

    @Test
    void getCurrentUser_deveRetornarEmpty_quandoPrincipalEhNull() {
        // Arrange
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Optional<User> result = adapter.getCurrentUser();

        // Assert
        assertThat(result).isEmpty();
        verifyNoInteractions(userMapperMock);
    }

    @Test
    void hasRole_deveRetornarFalse_quandoNaoAutenticado() {
        // Arrange
        ForGettingRoleName role = () -> "ROLE_ADMIN";
        var auth = new UsernamePasswordAuthenticationToken(
                "11111111-1111-1111-1111-111111111111",
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        auth.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        boolean result = adapter.hasRole(role);

        // Assert
        assertThat(result).isFalse();
        verifyNoInteractions(userMapperMock);
    }
}
