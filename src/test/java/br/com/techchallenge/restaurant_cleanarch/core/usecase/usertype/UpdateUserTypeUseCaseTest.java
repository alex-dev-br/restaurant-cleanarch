package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserTypeBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.InvalidRoleException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeWithoutRolesException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserTypeInput;
import org.assertj.core.api.InstanceOfAssertFactories;
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
        Role role = new Role(1L, roleName);
        Set<Role> roles = Set.of(role);

        UpdateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withName("Administrator")
                .withRoleNames(Set.of(roleName))
                .buildUpdateInput(id);

        UserType existingUserType = new UserTypeBuilder()
                .withDefaults()
                .withId(id)
                .withName("Old Name")
                .withRoles(roles)
                .build();

        given(loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(roles);
        given(userTypeGateway.findByName(input.name())).willReturn(Optional.empty());

        updateUserTypeUseCase.execute(input);

        then(loggedUserGateway).should().hasRole(UserTypeRoles.UPDATE_USER_TYPE);
        then(userTypeGateway).should().findById(id);
        then(roleGateway).should().getRolesByName(input.roles());
        then(userTypeGateway).should().findByName(input.name());
        then(userTypeGateway).should().save(userTypeCaptor.capture());

        UserType captured = userTypeCaptor.getValue();
        assertThat(captured.getId()).isEqualTo(id);
        assertThat(captured.getName()).isEqualTo(input.name());
        assertThat(captured.getRoles()).containsExactlyInAnyOrder(role);
    }

    @Test
    @DisplayName("Deve atualizar roles do UserType com sucesso")
    void shouldUpdateUserTypeRolesSuccessfully() {
        Long id = 1L;
        String userTypeName = "Administrator";
        String oldRoleName = "OLD_ROLE";
        String newRoleName = "NEW_ROLE";

        UpdateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withName(userTypeName)
                .withRoleNames(Set.of(newRoleName))
                .buildUpdateInput(id);

        Role oldRole = new Role(1L, oldRoleName);
        Role newRole = new Role(2L, newRoleName);

        UserType existingUserType = new UserType(id, userTypeName, Set.of(oldRole));
        Set<Role> newRoles = Set.of(newRole);

        given(loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(newRoles);
        given(userTypeGateway.findByName(input.name())).willReturn(Optional.of(existingUserType)); // mesmo ID

        updateUserTypeUseCase.execute(input);

        then(userTypeGateway).should().save(userTypeCaptor.capture());
        UserType captured = userTypeCaptor.getValue();

        assertThat(captured.getId()).isEqualTo(id);
        assertThat(captured.getName()).isEqualTo(userTypeName);
        assertThat(captured.getRoles()).containsExactlyInAnyOrder(newRole);
        assertThat(captured.getRoles()).doesNotContain(oldRole);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        UpdateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withName("Admin")
                .withRoleNames(Set.of("ADMIN"))
                .buildUpdateInput(1L);

        given(loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)).willReturn(false);

        assertThatThrownBy(() -> updateUserTypeUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to perform this action.");

        then(loggedUserGateway).should().hasRole(UserTypeRoles.UPDATE_USER_TYPE);
        then(userTypeGateway).should(never()).findById(any());
        then(roleGateway).should(never()).getRolesByName(any());
        then(userTypeGateway).should(never()).findByName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando UserType não é encontrado")
    void shouldThrowExceptionWhenUserTypeNotFound() {
        Long id = 1L;
        UpdateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withName("Admin")
                .withRoleNames(Set.of("ADMIN"))
                .buildUpdateInput(id);

        given(loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateUserTypeUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User type not found.");

        then(loggedUserGateway).should().hasRole(UserTypeRoles.UPDATE_USER_TYPE);
        then(userTypeGateway).should().findById(id);
        then(roleGateway).should(never()).getRolesByName(any());
        then(userTypeGateway).should(never()).findByName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando roles não são encontradas")
    void shouldThrowExceptionWhenRolesNotFound() {
        Long id = 1L;
        UpdateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withName("Admin")
                .withRoleNames(Set.of("INVALID_ROLE"))
                .buildUpdateInput(id);

        UserType existingUserType = new UserTypeBuilder()
                .withDefaults()
                .withId(id)
                .build();

        given(loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)).willReturn(true);
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

        UpdateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withName("Admin")
                .withRoleNames(Set.of(validRoleName, invalidRoleName))
                .buildUpdateInput(id);

        Role validRole = new Role(1L, validRoleName);

        UserType existingUserType = new UserTypeBuilder()
                .withDefaults()
                .withId(id)
                .build();

        given(loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(existingUserType));
        given(roleGateway.getRolesByName(input.roles())).willReturn(Set.of(validRole));

        assertThatThrownBy(() -> updateUserTypeUseCase.execute(input))
                .isInstanceOf(InvalidRoleException.class)
                .asInstanceOf(InstanceOfAssertFactories.type(InvalidRoleException.class))
                .extracting(InvalidRoleException::getInvalidRoles)
                .asInstanceOf(InstanceOfAssertFactories.COLLECTION)
                .containsExactlyInAnyOrder(invalidRoleName)
                .doesNotContain(validRoleName);

        then(userTypeGateway).should(never()).findByName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do UserType já está em uso por outro ID")
    void shouldThrowExceptionWhenUserTypeNameAlreadyInUseByOtherId() {
        Long id = 1L;
        Long otherId = 2L;
        String roleName = "ADMIN";
        Role role = new Role(1L, roleName);
        Set<Role> roles = Set.of(role);

        UpdateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withName("Administrator")
                .withRoleNames(Set.of(roleName))
                .buildUpdateInput(id);

        UserType existingUserType = new UserType(id, "Old Name", roles);
        UserType otherUserType = new UserType(otherId, input.name(), roles);

        given(loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)).willReturn(true);
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
        Role role = new Role(1L, roleName);
        Set<Role> roles = Set.of(role);

        UpdateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withName("Administrator")
                .withRoleNames(Set.of(roleName))
                .buildUpdateInput(id);

        UserType existingUserType = new UserType(id, input.name(), roles);

        given(loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)).willReturn(true);
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
                .hasMessage("Input cannot be null.");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(userTypeGateway).should(never()).findById(any());
        then(roleGateway).should(never()).getRolesByName(any());
        then(userTypeGateway).should(never()).findByName(any());
        then(userTypeGateway).should(never()).save(any());
    }
}
