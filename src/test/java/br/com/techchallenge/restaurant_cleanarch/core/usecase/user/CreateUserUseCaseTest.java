package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserInput;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateUserUseCase")
public class CreateUserUseCaseTest {

    @Mock private UserGateway userGateway;
    @Mock private UserTypeGateway userTypeGateway;
    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private PasswordHasherGateway passwordHasherGateway;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Deve criar um usuário com sucesso (salvando hash e sem vazar password)")
    void shouldCreateUserSuccessfully() {
        // Arrange
        Long userTypeId = 1L;

        var input = new CreateUserInput(
                "Maria Oliveira",
                "maria@teste.com",
                "senhaSegura123",
                new AddressBuilder().buildInput(),
                userTypeId
        );

        var userType = new UserType(
                userTypeId,
                "Cliente",
                Set.of(new Role(10L, "VIEW_MENU_ITEM"))
        );

        given(loggedUserGateway.hasRole(UserManagementRoles.CREATE_USER)).willReturn(true);
        given(userGateway.existsUserWithEmail("maria@teste.com")).willReturn(false);
        given(userTypeGateway.findById(userTypeId)).willReturn(Optional.of(userType));
        given(passwordHasherGateway.hash("senhaSegura123")).willReturn("HASHED");
        given(userGateway.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        // Act
        User result = createUserUseCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Maria Oliveira");
        assertThat(result.getEmail()).isEqualTo("maria@teste.com");
        assertThat(result.getUserType()).isEqualTo(userType);

        then(userGateway).should().save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertThat(saved.getPasswordHash()).isEqualTo("HASHED");
        // garante que não veio “cru”
        assertThat(saved.getPasswordHash()).isNotEqualTo("senhaSegura123");

        then(loggedUserGateway).should().hasRole(UserManagementRoles.CREATE_USER);
        then(userGateway).should().existsUserWithEmail("maria@teste.com");
        then(userTypeGateway).should().findById(userTypeId);
        then(passwordHasherGateway).should().hash("senhaSegura123");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário logado não tem permissão")
    void shouldThrowWhenNoPermission() {
        // Arrange
        var input = new CreateUserInput("Maria", "maria@teste.com", "123", null, 1L);

        given(loggedUserGateway.hasRole(UserManagementRoles.CREATE_USER)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> createUserUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class);

        then(userGateway).should(never()).save(any());
        then(passwordHasherGateway).should(never()).hash(any());
        then(userTypeGateway).should(never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está em uso")
    void shouldThrowWhenEmailAlreadyInUse() {
        // Arrange
        Long userTypeId = 1L;
        var input = new CreateUserInput("Maria", "maria@teste.com", "123", null, userTypeId);

        given(loggedUserGateway.hasRole(UserManagementRoles.CREATE_USER)).willReturn(true);
        given(userGateway.existsUserWithEmail("maria@teste.com")).willReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> createUserUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email is already in use.");

        then(passwordHasherGateway).should(never()).hash(any());
        then(userGateway).should(never()).save(any());
        then(userTypeGateway).should(never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando password é blank")
    void shouldThrowWhenPasswordBlank() {
        // Arrange
        var input = new CreateUserInput("Maria", "maria@teste.com", "   ", null, 1L);

        given(loggedUserGateway.hasRole(UserManagementRoles.CREATE_USER)).willReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> createUserUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Password cannot be blank.");

        then(passwordHasherGateway).should(never()).hash(any());
        then(userGateway).should(never()).save(any());
        then(userTypeGateway).should(never()).findById(any());
        then(userGateway).should(never()).existsUserWithEmail(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando UserType não existe")
    void shouldThrowWhenUserTypeNotFound() {
        // Arrange
        Long userTypeId = 999L;
        var input = new CreateUserInput("Maria", "maria@teste.com", "123", null, userTypeId);

        given(loggedUserGateway.hasRole(UserManagementRoles.CREATE_USER)).willReturn(true);
        given(userGateway.existsUserWithEmail("maria@teste.com")).willReturn(false);
        given(userTypeGateway.findById(userTypeId)).willReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> createUserUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User type with ID 999 not found.");

        then(passwordHasherGateway).should(never()).hash(any());
        then(userGateway).should(never()).save(any());
    }


}
