package br.com.techchallenge.restaurant_cleanarch.core.usecase;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.exception.InvalidRoleException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeWithoutRolesException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggerUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UserTypeInput;
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
    private LoggerUserGateway loggerUserGateway;

    @InjectMocks
    private CreateUserTypeUseCase createUserTypeUseCase;

    @Captor
    private ArgumentCaptor<UserType> userTypeCaptor;

    @Test
    @DisplayName("Deve criar UserType com sucesso")
    void shouldCreateUserTypeSuccessfully() {
        String roleName = "ADMIN";
        String userTypeName = "Administrator";
        UserTypeInput input = new UserTypeInput(userTypeName, Set.of(roleName));
        Role role = new Role(1L, roleName);
        Set<Role> roles = Set.of(role);
        UserType expectedUserType = new UserType(1L, userTypeName, roles);

        given(loggerUserGateway.hasRole(CreateUserTypeUseCase.CREATE_USER_TYPE_ROLE)).willReturn(true);
        given(roleGateway.getRolesByName(input.roles())).willReturn(roles);
        given(userTypeGateway.existsUserTypeWithName(input.name())).willReturn(false);
        given(userTypeGateway.save(any(UserType.class))).willReturn(expectedUserType);

        UserType result = createUserTypeUseCase.execute(input);

        assertThat(result).isEqualTo(expectedUserType);
        then(loggerUserGateway).should().hasRole(CreateUserTypeUseCase.CREATE_USER_TYPE_ROLE);
        then(roleGateway).should().getRolesByName(input.roles());
        then(userTypeGateway).should().existsUserTypeWithName(input.name());
        
        then(userTypeGateway).should().save(userTypeCaptor.capture());
        UserType capturedUserType = userTypeCaptor.getValue();
        assertThat(capturedUserType.getName()).isEqualTo(userTypeName);
        assertThat(capturedUserType.getRoles()).containsExactly(role);
        assertThat(capturedUserType.getId()).isNull();
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        UserTypeInput input = new UserTypeInput("Admin", Set.of("ADMIN"));
        given(loggerUserGateway.hasRole(CreateUserTypeUseCase.CREATE_USER_TYPE_ROLE)).willReturn(false);

        assertThatThrownBy(() -> createUserTypeUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to create user types.");

        then(roleGateway).should(never()).getRolesByName(any());
        then(userTypeGateway).should(never()).existsUserTypeWithName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando roles não são encontradas")
    void shouldThrowExceptionWhenRolesNotFound() {
        UserTypeInput input = new UserTypeInput("Admin", Set.of("INVALID_ROLE"));
        given(loggerUserGateway.hasRole(CreateUserTypeUseCase.CREATE_USER_TYPE_ROLE)).willReturn(true);
        given(roleGateway.getRolesByName(input.roles())).willReturn(Collections.emptySet());

        assertThatThrownBy(() -> createUserTypeUseCase.execute(input))
                .isInstanceOf(UserTypeWithoutRolesException.class);

        then(userTypeGateway).should(never()).existsUserTypeWithName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando algumas roles são inválidas")
    void shouldThrowExceptionWhenSomeRolesAreInvalid() {
        String validRoleName = "VALID_ROLE";
        String invalidRoleName = "INVALID_ROLE";
        UserTypeInput input = new UserTypeInput("Admin", Set.of(validRoleName, invalidRoleName));
        Role validRole = new Role(1L, validRoleName);
        
        given(loggerUserGateway.hasRole(CreateUserTypeUseCase.CREATE_USER_TYPE_ROLE)).willReturn(true);
        given(roleGateway.getRolesByName(input.roles())).willReturn(Set.of(validRole));

        assertThatThrownBy(() -> createUserTypeUseCase.execute(input))
                .isInstanceOf(InvalidRoleException.class)
                .hasMessageContaining(invalidRoleName);

        then(userTypeGateway).should(never()).existsUserTypeWithName(any());
        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do UserType já está em uso")
    void shouldThrowExceptionWhenUserTypeNameAlreadyInUse() {
        String roleName = "ADMIN";
        UserTypeInput input = new UserTypeInput("Administrator", Set.of(roleName));
        Role role = new Role(1L, roleName);
        Set<Role> roles = Set.of(role);

        given(loggerUserGateway.hasRole(CreateUserTypeUseCase.CREATE_USER_TYPE_ROLE)).willReturn(true);
        given(roleGateway.getRolesByName(input.roles())).willReturn(roles);
        given(userTypeGateway.existsUserTypeWithName(input.name())).willReturn(true);

        assertThatThrownBy(() -> createUserTypeUseCase.execute(input))
                .isInstanceOf(UserTypeNameIsAlreadyInUseException.class);

        then(userTypeGateway).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando input é nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> createUserTypeUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserTypeInput cannot be null.");
    }
}
