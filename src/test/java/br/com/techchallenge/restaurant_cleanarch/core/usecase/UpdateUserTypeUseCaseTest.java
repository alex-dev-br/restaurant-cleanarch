package br.com.techchallenge.restaurant_cleanarch.core.usecase;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserTypeInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateUserTypeUseCase")
class UpdateUserTypeUseCaseTest {

    @Mock
    private RoleGateway roleGateway;

    @Mock
    private UserTypeGateway userTypeGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private UpdateUserTypeUseCase updateUserTypeUseCase;

    @Captor
    private ArgumentCaptor<UserType> userTypeCaptor;

    @Test
    @DisplayName("Deve atualizar UserType com sucesso")
    void shouldUpdateUserTypeSuccessfully() {
        Long id = 1L;
        String roleName = "ADMIN";
        String userTypeName = "Administrator";
        UpdateUserTypeInput input = new UpdateUserTypeInput(id, userTypeName, Set.of(roleName));
        Role role = new Role(1L, roleName);
        Set<Role> roles = Set.of(role);
        UserType existingUserType = new UserType(id, "Old Name", roles);

        given(loggedUserGateway.hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(roles);
        given(userTypeGateway.findByName(input.name())).willReturn(Optional.empty());

        updateUserTypeUseCase.execute(input);

        then(loggedUserGateway).should().hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE);
        then(userTypeGateway).should().findById(id);
        then(roleGateway).should().getRolesByName(input.roles());
        then(userTypeGateway).should().findByName(input.name());
        
        then(userTypeGateway).should().save(userTypeCaptor.capture());
        UserType capturedUserType = userTypeCaptor.getValue();
        assertThat(capturedUserType.getId()).isEqualTo(id);
        assertThat(capturedUserType.getName()).isEqualTo(userTypeName);
        assertThat(capturedUserType.getRoles()).containsExactlyInAnyOrder(role);
    }

    @Test
    @DisplayName("Deve atualizar roles do UserType com sucesso")
    void shouldUpdateUserTypeRolesSuccessfully() {
        Long id = 1L;
        String userTypeName = "Administrator";
        String oldRoleName = "OLD_ROLE";
        String newRoleName = "NEW_ROLE";
        
        UpdateUserTypeInput input = new UpdateUserTypeInput(id, userTypeName, Set.of(newRoleName));
        
        Role oldRole = new Role(1L, oldRoleName);
        Role newRole = new Role(2L, newRoleName);
        
        UserType existingUserType = new UserType(id, userTypeName, Set.of(oldRole));
        Set<Role> newRoles = Set.of(newRole);

        given(loggedUserGateway.hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(newRoles);
        given(userTypeGateway.findByName(input.name())).willReturn(Optional.of(existingUserType)); // Mesmo nome, mesmo ID

        updateUserTypeUseCase.execute(input);

        then(userTypeGateway).should().save(userTypeCaptor.capture());
        UserType capturedUserType = userTypeCaptor.getValue();
        assertThat(capturedUserType.getId()).isEqualTo(id);
        assertThat(capturedUserType.getName()).isEqualTo(userTypeName);
        assertThat(capturedUserType.getRoles()).containsExactlyInAnyOrder(newRole);
        assertThat(capturedUserType.getRoles()).doesNotContain(oldRole);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        UpdateUserTypeInput input = new UpdateUserTypeInput(1L, "Admin", Set.of("ADMIN"));
        given(loggedUserGateway.hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE)).willReturn(false);

        assertThatThrownBy(() -> updateUserTypeUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to update user types.");

        then(userTypeGateway).should(never()).findById(any());
        then(roleGateway).should(never()).getRolesByName(any());
        then(userTypeGateway).should(never()).findByName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando UserType não é encontrado")
    void shouldThrowExceptionWhenUserTypeNotFound() {
        Long id = 1L;
        UpdateUserTypeInput input = new UpdateUserTypeInput(id, "Admin", Set.of("ADMIN"));
        
        given(loggedUserGateway.hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateUserTypeUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User type not found.");

        then(roleGateway).should(never()).getRolesByName(any());
        then(userTypeGateway).should(never()).findByName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando roles não são encontradas")
    void shouldThrowExceptionWhenRolesNotFound() {
        Long id = 1L;
        UpdateUserTypeInput input = new UpdateUserTypeInput(id, "Admin", Set.of("INVALID_ROLE"));
        UserType existingUserType = new UserType(id, "Old Name", Set.of(new Role(1L, "ADMIN")));

        given(loggedUserGateway.hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(Collections.emptySet());

        assertThatThrownBy(() -> updateUserTypeUseCase.execute(input))
                .isInstanceOf(UserTypeWithoutRolesException.class);

        then(userTypeGateway).should(never()).findByName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando algumas roles são inválidas")
    void shouldThrowExceptionWhenSomeRolesAreInvalid() {
        Long id = 1L;
        String validRoleName = "VALID_ROLE";
        String invalidRoleName = "INVALID_ROLE";
        UpdateUserTypeInput input = new UpdateUserTypeInput(id, "Admin", Set.of(validRoleName, invalidRoleName));
        Role validRole = new Role(1L, validRoleName);
        UserType existingUserType = new UserType(id, "Old Name", Set.of(validRole));
        
        given(loggedUserGateway.hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(Set.of(validRole));

        assertThatThrownBy(() -> updateUserTypeUseCase.execute(input))
                .isInstanceOf(InvalidRoleException.class)
                .hasMessageContaining(invalidRoleName);

        then(userTypeGateway).should(never()).findByName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do UserType já está em uso por outro ID")
    void shouldThrowExceptionWhenUserTypeNameAlreadyInUseByOtherId() {
        Long id = 1L;
        Long otherId = 2L;
        String roleName = "ADMIN";
        String userTypeName = "Administrator";
        UpdateUserTypeInput input = new UpdateUserTypeInput(id, userTypeName, Set.of(roleName));
        Role role = new Role(1L, roleName);
        Set<Role> roles = Set.of(role);
        UserType existingUserType = new UserType(id, "Old Name", roles);
        UserType otherUserType = new UserType(otherId, userTypeName, roles);

        given(loggedUserGateway.hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(roles);
        given(userTypeGateway.findByName(input.name())).willReturn(Optional.of(otherUserType));

        assertThatThrownBy(() -> updateUserTypeUseCase.execute(input))
                .isInstanceOf(UserTypeNameIsAlreadyInUseException.class);

        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar com sucesso quando nome já existe mas pertence ao mesmo ID")
    void shouldUpdateSuccessfullyWhenNameExistsButBelongsToSameId() {
        Long id = 1L;
        String roleName = "ADMIN";
        String userTypeName = "Administrator";
        UpdateUserTypeInput input = new UpdateUserTypeInput(id, userTypeName, Set.of(roleName));
        Role role = new Role(1L, roleName);
        Set<Role> roles = Set.of(role);
        UserType existingUserType = new UserType(id, userTypeName, roles);

        given(loggedUserGateway.hasRole(UpdateUserTypeUseCase.UPDATE_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(roles);
        given(userTypeGateway.findByName(input.name())).willReturn(Optional.of(existingUserType));

        updateUserTypeUseCase.execute(input);

        then(userTypeGateway).should().save(any(UserType.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando input é nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> updateUserTypeUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateUserTypeInput cannot be null.");
    }
}
