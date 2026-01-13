package br.com.techchallenge.restaurant_cleanarch.infra.auth;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoggedUserGatewayAdapterTest {

    private UserGateway userGateway;
    private LoggedUserGatewayAdapter adapter;

    @BeforeEach
    void setUp() {
        userGateway = Mockito.mock(UserGateway.class);
        adapter = new LoggedUserGatewayAdapter(userGateway);
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
        verifyNoInteractions(userGateway);
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
        verifyNoInteractions(userGateway);
    }

    @Test
    void getCurrentUser_deveBuscarNoGateway_quandoPrincipalEhUuidString() {
        // Arrange
        UUID id = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(
                id.toString(),
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ANY"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = Mockito.mock(User.class);
        when(userGateway.findById(id)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = adapter.getCurrentUser();

        // Assert
        assertThat(result).contains(user);
        verify(userGateway).findById(id);
    }

    @Test
    void getCurrentUser_deveBuscarNoGateway_quandoPrincipalEhUuid() {
        // Arrange
        UUID id = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(
                id,
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ANY"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = Mockito.mock(User.class);
        when(userGateway.findById(id)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = adapter.getCurrentUser();

        // Assert
        assertThat(result).contains(user);
        verify(userGateway).findById(id);
    }

    @Test
    void hasRole_deveRetornarFalse_quandoRoleNameEhNull() {
        // Arrange
        ForGettingRoleName role = null;

        // Act
        boolean result = adapter.hasRole(role);

        // Assert
        assertThat(result).isFalse();
        verifyNoInteractions(userGateway);
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
        verifyNoInteractions(userGateway);
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
        verifyNoInteractions(userGateway);
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
        verifyNoInteractions(userGateway);
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
        verifyNoInteractions(userGateway);
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
        verifyNoInteractions(userGateway);
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
        verifyNoInteractions(userGateway);
    }
}
