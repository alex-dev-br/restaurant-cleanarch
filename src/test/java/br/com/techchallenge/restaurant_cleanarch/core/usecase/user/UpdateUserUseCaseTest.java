package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;


import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserTypeBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.ResourceNotFoundException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateUserUseCase")
class UpdateUserUseCaseTest {

    @Mock private UserGateway userGateway;
    @Mock private UserTypeGateway userTypeGateway;
    @Mock private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Deve atualizar nome, email, address e userType quando input fornecer novos valores")
    void shouldUpdateUserSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();

        var currentAddress = new AddressBuilder()
                .withStreet("Rua Antiga")
                .withNumber("1")
                .withCity("Rio")
                .withState("RJ")
                .withZipCode("20000000")
                .withComplement("Casa")
                .build();

        UserType currentType = new UserTypeBuilder()
                .withId(1L)
                .withName("Cliente")
                .withRoleNames(java.util.Set.of("VIEW_MENU_ITEM"))
                .build();

        User currentUser = new UserBuilder()
                .withId(userId)
                .withName("Nome Antigo")
                .withEmail("old@teste.com")
                .withAddress(currentAddress)
                .withUserType(currentType)
                .withPasswordHash("HASHED_OLD")
                .build();

        var newAddressInput = new AddressBuilder()
                .withStreet("Rua Nova")
                .withNumber("10")
                .withCity("Niterói")
                .withState("RJ")
                .withZipCode("24000000")
                .withComplement("Apto 101")
                .buildInput();

        Long newUserTypeId = 2L;
        UserType newUserType = new UserTypeBuilder()
                .withId(newUserTypeId)
                .withName("Dono")
                .withRoleNames(java.util.Set.of("RESTAURANT_OWNER"))
                .build();

        var input = new UpdateUserInput(
                userId,
                "  Maria Oliveira  ",
                "  maria@teste.com  ",
                newAddressInput,
                newUserTypeId
        );

        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.of(currentUser));
        given(userGateway.existsUserWithEmail("maria@teste.com")).willReturn(false);
        given(userTypeGateway.findById(newUserTypeId)).willReturn(Optional.of(newUserType));
        given(userGateway.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        // Act
        updateUserUseCase.execute(input);

        // Assert
        then(userGateway).should().save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertThat(saved.getId()).isEqualTo(userId);
        assertThat(saved.getName()).isEqualTo("Maria Oliveira");
        assertThat(saved.getEmail()).isEqualTo("maria@teste.com");
        assertThat(saved.getPasswordHash()).isEqualTo("HASHED_OLD");

        assertThat(saved.getAddress()).isNotNull();
        assertThat(saved.getAddress().getStreet()).isEqualTo("Rua Nova");
        assertThat(saved.getAddress().getNumber()).isEqualTo("10");
        assertThat(saved.getAddress().getCity()).isEqualTo("Niterói");
        assertThat(saved.getAddress().getState()).isEqualTo("RJ");
        assertThat(saved.getAddress().getZipCode()).isEqualTo("24000000");
        assertThat(saved.getAddress().getComplement()).isEqualTo("Apto 101");

        assertThat(saved.getUserType()).isEqualTo(newUserType);

        then(loggedUserGateway).should().hasRole(UserManagementRoles.UPDATE_USER);
        then(userGateway).should().findById(userId);
        then(userGateway).should().existsUserWithEmail("maria@teste.com");
        then(userTypeGateway).should().findById(newUserTypeId);
    }

    @Test
    @DisplayName("Deve manter nome/email/address/userType quando input vier com campos nulos e não alterar email")
    void shouldKeepCurrentValuesWhenInputFieldsAreNull() {
        // Arrange
        UUID userId = UUID.randomUUID();

        var currentAddress = new AddressBuilder().build();

        UserType currentType = new UserTypeBuilder()
                .withId(1L)
                .withName("Cliente")
                .withRoleNames(java.util.Set.of("VIEW_MENU_ITEM"))
                .build();

        User currentUser = new UserBuilder()
                .withId(userId)
                .withName("Nome Atual")
                .withEmail("atual@teste.com")
                .withAddress(currentAddress)
                .withUserType(currentType)
                .withPasswordHash("HASHED")
                .build();

        var input = new UpdateUserInput(
                userId,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.of(currentUser));
        given(userGateway.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        // Act
        updateUserUseCase.execute(input);

        // Assert
        then(userGateway).should().save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertThat(saved.getId()).isEqualTo(userId);
        assertThat(saved.getName()).isEqualTo("Nome Atual");
        assertThat(saved.getEmail()).isEqualTo("atual@teste.com");
        assertThat(saved.getAddress()).isEqualTo(currentAddress);
        assertThat(saved.getUserType()).isEqualTo(currentType);
        assertThat(saved.getPasswordHash()).isEqualTo("HASHED");

        // Não chama existsUserWithEmail porque email não mudou
        then(userGateway).should(never()).existsUserWithEmail(any());
        then(userTypeGateway).should(never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input.id for nulo")
    void shouldThrowWhenIdIsNull() {
        // Arrange
        var input = new UpdateUserInput(
                null,
                "Maria",
                "maria@teste.com",
                null,
                null
        );

        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> updateUserUseCase.execute(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User UUID cannot be null.");

        then(userGateway).should(never()).findById(any());
        then(userGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não existir")
    void shouldThrowWhenUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        var input = new UpdateUserInput(userId, "Maria", "maria@teste.com", null, null);

        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> updateUserUseCase.execute(input))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with ID " + userId + " not found.");

        then(userGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando newEmail ficar blank (input.email em branco)")
    void shouldThrowWhenEmailBlank() {
        // Arrange
        UUID userId = UUID.randomUUID();

        User currentUser = new UserBuilder()
                .withId(userId)
                .withEmail("old@teste.com")
                .build();

        var input = new UpdateUserInput(userId, null, "   ", null, null);

        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.of(currentUser));

        // Act + Assert
        assertThatThrownBy(() -> updateUserUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email cannot be blank.");

        then(userGateway).should(never()).save(any());
        then(userGateway).should(never()).existsUserWithEmail(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando email mudar e já estiver em uso")
    void shouldThrowWhenEmailChangedAndAlreadyInUse() {
        // Arrange
        UUID userId = UUID.randomUUID();

        User currentUser = new UserBuilder()
                .withId(userId)
                .withEmail("old@teste.com")
                .build();

        var input = new UpdateUserInput(userId, null, "new@teste.com", null, null);

        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.of(currentUser));
        given(userGateway.existsUserWithEmail("new@teste.com")).willReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> updateUserUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email new@teste.com is already in use.");

        then(userGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando userTypeId mudar e não existir")
    void shouldThrowWhenUserTypeNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();

        UserType currentType = new UserTypeBuilder()
                .withId(1L)
                .withName("Cliente")
                .withRoleNames(java.util.Set.of("VIEW_MENU_ITEM"))
                .build();

        User currentUser = new UserBuilder()
                .withId(userId)
                .withUserType(currentType)
                .build();

        Long newUserTypeId = 999L;
        var input = new UpdateUserInput(userId, null, null, null, newUserTypeId);

        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.of(currentUser));
        given(userTypeGateway.findById(newUserTypeId)).willReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> updateUserUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User type with ID " + newUserTypeId + " not found.");

        then(userGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário logado não tiver permissão")
    void shouldThrowWhenNoPermission() {
        // Arrange
        var input = new UpdateUserInput(UUID.randomUUID(), "Maria", "maria@teste.com", null, null);
        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> updateUserUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class);

        then(userGateway).should(never()).findById(any());
        then(userGateway).should(never()).save(any());
        then(userTypeGateway).should(never()).findById(any());
    }

    @Test
    @DisplayName("Construtor deve lançar NullPointerException quando loggedUserGateway for nulo")
    void constructorShouldThrowWhenLoggedUserGatewayIsNull() {
        // Arrange
        LoggedUserGateway nullLoggedUserGateway = null;

        // Act + Assert
        assertThatThrownBy(() -> new UpdateUserUseCase(userGateway, userTypeGateway, nullLoggedUserGateway))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("LoggedUserGateway cannot be null.");
    }

    @Test
    @DisplayName("Não deve buscar UserType quando input.userTypeId for igual ao userType atual")
    void shouldNotFetchUserTypeWhenUserTypeIdIsSameAsCurrent() {
        // Arrange
        UUID userId = UUID.randomUUID();

        Long currentUserTypeId = 1L;
        UserType currentType = new UserTypeBuilder()
                .withId(currentUserTypeId)
                .withName("Cliente")
                .withRoleNames(Set.of("VIEW_MENU_ITEM"))
                .build();

        User currentUser = new UserBuilder()
                .withId(userId)
                .withUserType(currentType)
                .withEmail("atual@teste.com")
                .build();

        var input = new UpdateUserInput(
                userId,
                "Novo Nome",
                "atual@teste.com", // mantém email igual para não cair no existsUserWithEmail
                null,
                currentUserTypeId // <- igual ao atual (cobre o ramo que faltava)
        );

        given(loggedUserGateway.hasRole(UserManagementRoles.UPDATE_USER)).willReturn(true);
        given(userGateway.findById(userId)).willReturn(Optional.of(currentUser));
        given(userGateway.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        // Act
        updateUserUseCase.execute(input);

        // Assert
        then(userGateway).should().save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertThat(saved.getUserType()).isEqualTo(currentType);
        assertThat(saved.getName()).isEqualTo("Novo Nome");
        assertThat(saved.getEmail()).isEqualTo("atual@teste.com");

        then(userTypeGateway).should(never()).findById(any());
        then(userGateway).should(never()).existsUserWithEmail(any()); // email não mudou
    }

}

