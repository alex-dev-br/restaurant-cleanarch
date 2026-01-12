package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetAllUserTypeUseCase")
class ListUserTypesUseCaseTest {

    @Mock
    private UserTypeGateway userTypeGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListUserTypesUseCase listUserTypesUseCase;

    @Test
    @DisplayName("Deve retornar todos os UserTypes com sucesso")
    void shouldReturnAllUserTypesSuccessfully() {
        UserType userType1 = new UserType(1L, "Administrator", Set.of(new Role(1L, "ADMIN")));
        UserType userType2 = new UserType(2L, "User", Set.of(new Role(2L, "USER")));
        Set<UserType> expectedUserTypes = Set.of(userType1, userType2);

        given(loggedUserGateway.hasRole(UserTypeRoles.VIEW_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findAll()).willReturn(expectedUserTypes);

        Set<UserType> result = listUserTypesUseCase.execute();

        assertThat(result).isNotNull().hasSize(2).containsExactlyInAnyOrder(userType1, userType2);

        then(loggedUserGateway).should().hasRole(UserTypeRoles.VIEW_USER_TYPE);
        then(userTypeGateway).should().findAll();
    }

    @Test
    @DisplayName("Deve retornar conjunto vazio quando não houver UserTypes")
    void shouldReturnEmptySetWhenNoUserTypesFound() {
        given(loggedUserGateway.hasRole(UserTypeRoles.VIEW_USER_TYPE)).willReturn(true);
        given(userTypeGateway.findAll()).willReturn(Collections.emptySet());

        Set<UserType> result = listUserTypesUseCase.execute();

        assertThat(result).isNotNull().isEmpty();

        then(loggedUserGateway).should().hasRole(UserTypeRoles.VIEW_USER_TYPE);
        then(userTypeGateway).should().findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        given(loggedUserGateway.hasRole(UserTypeRoles.VIEW_USER_TYPE)).willReturn(false);

        assertThatThrownBy(() -> listUserTypesUseCase.execute())
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to get all user types.");

        then(loggedUserGateway).should().hasRole(UserTypeRoles.VIEW_USER_TYPE);
        then(userTypeGateway).should(never()).findAll();
    }
}
