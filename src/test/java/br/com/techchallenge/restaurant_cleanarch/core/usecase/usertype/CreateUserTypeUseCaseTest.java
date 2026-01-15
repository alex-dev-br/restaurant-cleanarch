package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserTypeBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.InvalidRoleException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeWithoutRolesException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserTypeInput;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateUserTypeUseCase")
class CreateUserTypeUseCaseTest {

    @Mock
    private RoleGateway roleGateway;

    @Mock
    private UserTypeGateway userTypeGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private CreateUserTypeUseCase createUserTypeUseCase;

    @Captor
    private ArgumentCaptor<UserType> userTypeCaptor;

    @Test
    @DisplayName("Deve criar UserType com sucesso")
    void shouldCreateUserTypeSuccessfully() {
        var builder = new UserTypeBuilder().withDefaults();

        CreateUserTypeInput input = builder.buildCreateInput();
        Role role = new Role(1L, "ADMIN");
        Set<Role> roles = Set.of(role);

        UserType expectedUserType = builder.copy()
                .withId(1L)
                .withRoles(roles)
                .build();

        given(loggedUserGateway.hasRole(UserTypeRoles.CREATE_USER_TYPE)).willReturn(true);
        given(roleGateway.getRolesByName(input.roles())).willReturn(roles);
        given(userTypeGateway.existsUserTypeWithName(input.name())).willReturn(false);
        given(userTypeGateway.save(any(UserType.class))).willReturn(expectedUserType);

        UserType result = createUserTypeUseCase.execute(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedUserType.getId());
        assertThat(result.getName()).isEqualTo(input.name());
        assertThat(result.getRoles()).containsExactlyInAnyOrder(role);

        then(loggedUserGateway).should().hasRole(UserTypeRoles.CREATE_USER_TYPE);
        then(roleGateway).should().getRolesByName(input.roles());
        then(userTypeGateway).should().existsUserTypeWithName(input.name());
        then(userTypeGateway).should().save(userTypeCaptor.capture());

        UserType capturedUserType = userTypeCaptor.getValue();
        assertThat(capturedUserType.getId()).isNull();
        assertThat(capturedUserType.getName()).isEqualTo(input.name());
        assertThat(capturedUserType.getRoles()).containsExactlyInAnyOrder(role);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        CreateUserTypeInput input = new UserTypeBuilder().withDefaults().buildCreateInput();

        given(loggedUserGateway.hasRole(UserTypeRoles.CREATE_USER_TYPE)).willReturn(false);

        assertThatThrownBy(() -> createUserTypeUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to perform this action.");

        then(loggedUserGateway).should().hasRole(UserTypeRoles.CREATE_USER_TYPE);
        then(roleGateway).should(never()).getRolesByName(any());
        then(userTypeGateway).should(never()).existsUserTypeWithName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando roles não são encontradas")
    void shouldThrowExceptionWhenRolesNotFound() {
        CreateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withRoleNames(Set.of("INVALID_ROLE"))
                .buildCreateInput();

        given(loggedUserGateway.hasRole(UserTypeRoles.CREATE_USER_TYPE)).willReturn(true);
        given(roleGateway.getRolesByName(input.roles())).willReturn(Collections.emptySet());

        assertThatThrownBy(() -> createUserTypeUseCase.execute(input))
                .isInstanceOf(UserTypeWithoutRolesException.class);

        then(loggedUserGateway).should().hasRole(UserTypeRoles.CREATE_USER_TYPE);
        then(roleGateway).should().getRolesByName(input.roles());
        then(userTypeGateway).should(never()).existsUserTypeWithName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando algumas roles são inválidas")
    void shouldThrowExceptionWhenSomeRolesAreInvalid() {
        String validRoleName = "VALID_ROLE";
        String invalidRoleName = "INVALID_ROLE";

        CreateUserTypeInput input = new UserTypeBuilder()
                .withDefaults()
                .withRoleNames(Set.of(validRoleName, invalidRoleName))
                .buildCreateInput();

        Role validRole = new Role(1L, validRoleName);

        given(loggedUserGateway.hasRole(UserTypeRoles.CREATE_USER_TYPE)).willReturn(true);
        given(roleGateway.getRolesByName(input.roles())).willReturn(Set.of(validRole));

        assertThatThrownBy(() -> createUserTypeUseCase.execute(input))
                .isInstanceOf(InvalidRoleException.class)
                .asInstanceOf(InstanceOfAssertFactories.type(InvalidRoleException.class))
                .extracting(InvalidRoleException::getInvalidRoles)
                .asInstanceOf(InstanceOfAssertFactories.COLLECTION)
                .containsExactlyInAnyOrder(invalidRoleName)
                .doesNotContain(validRoleName);

        then(loggedUserGateway).should().hasRole(UserTypeRoles.CREATE_USER_TYPE);
        then(roleGateway).should().getRolesByName(input.roles());
        then(userTypeGateway).should(never()).existsUserTypeWithName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do UserType já está em uso")
    void shouldThrowExceptionWhenUserTypeNameAlreadyInUse() {
        CreateUserTypeInput input = new UserTypeBuilder().withDefaults().buildCreateInput();
        Role role = new Role(1L, "ADMIN");
        Set<Role> roles = Set.of(role);

        given(loggedUserGateway.hasRole(UserTypeRoles.CREATE_USER_TYPE)).willReturn(true);
        given(roleGateway.getRolesByName(input.roles())).willReturn(roles);
        given(userTypeGateway.existsUserTypeWithName(input.name())).willReturn(true);

        assertThatThrownBy(() -> createUserTypeUseCase.execute(input))
                .isInstanceOf(UserTypeNameIsAlreadyInUseException.class);

        then(loggedUserGateway).should().hasRole(UserTypeRoles.CREATE_USER_TYPE);
        then(roleGateway).should().getRolesByName(input.roles());
        then(userTypeGateway).should().existsUserTypeWithName(input.name());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando input é nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> createUserTypeUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Input cannot be null.");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(roleGateway).should(never()).getRolesByName(any());
        then(userTypeGateway).should(never()).existsUserTypeWithName(any());
        then(userTypeGateway).should(never()).save(any());
    }
}
