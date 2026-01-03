package br.com.techchallenge.restaurant_cleanarch.core.usecase;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetByIdUserTypeUseCase")
class GetByIdUserTypeUseCaseTest {

    @Mock
    private UserTypeGateway userTypeGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private GetByIdUserTypeUseCase getByIdUserTypeUseCase;

    @Test
    @DisplayName("Deve retornar UserType com sucesso quando encontrado")
    void shouldReturnUserTypeSuccessfully() {
        Long id = 1L;
        UserType expectedUserType = new UserType(id, "Administrator", Set.of(new Role(1L, "ADMIN")));

        given(loggedUserGateway.hasRole(GetByIdUserTypeUseCase.GET_BY_ID_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.of(expectedUserType));

        UserType result = getByIdUserTypeUseCase.execute(id);

        assertThat(result).isNotNull().isEqualTo(expectedUserType);

        then(loggedUserGateway).should().hasRole(GetByIdUserTypeUseCase.GET_BY_ID_USER_TYPE_ROLE);
        then(userTypeGateway).should().findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        Long id = 1L;
        given(loggedUserGateway.hasRole(GetByIdUserTypeUseCase.GET_BY_ID_USER_TYPE_ROLE)).willReturn(false);

        assertThatThrownBy(() -> getByIdUserTypeUseCase.execute(id))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to get user types.");

        then(loggedUserGateway).should().hasRole(GetByIdUserTypeUseCase.GET_BY_ID_USER_TYPE_ROLE);
        then(userTypeGateway).should(never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando UserType não é encontrado")
    void shouldThrowExceptionWhenUserTypeNotFound() {
        Long id = 1L;
        given(loggedUserGateway.hasRole(GetByIdUserTypeUseCase.GET_BY_ID_USER_TYPE_ROLE)).willReturn(true);
        given(userTypeGateway.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getByIdUserTypeUseCase.execute(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User type not found.");

        then(loggedUserGateway).should().hasRole(GetByIdUserTypeUseCase.GET_BY_ID_USER_TYPE_ROLE);
        then(userTypeGateway).should().findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> getByIdUserTypeUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Id of user type cannot be null.");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(userTypeGateway).should(never()).findById(any());
    }
}
