package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.CreateUserTypeUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UserTypeController")
class UserTypeControllerTest {

    @Mock
    private CreateUserTypeUseCase createUserTypeUseCase;

    @InjectMocks
    private UserTypeController userTypeController;

    @Test
    @DisplayName("Deve criar UserType com sucesso e retornar UserTypeOutput")
    void shouldCreateUserTypeSuccessfully() {
        String roleName = "ADMIN";
        String userTypeName = "Administrator";
        UserTypeInput input = new UserTypeInput(userTypeName, Set.of(roleName));
        
        Role role = new Role(1L, roleName);
        UserType userType = new UserType(1L, userTypeName, Set.of(role));
        
        given(createUserTypeUseCase.execute(input)).willReturn(userType);

        UserTypeOutput result = userTypeController.createUserType(input);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userType.getId());
        assertThat(result.name()).isEqualTo(userType.getName());
        assertThat(result.roles()).containsExactly(roleName);

        then(createUserTypeUseCase).should().execute(any(UserTypeInput.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CreateUserTypeUseCase lança exceção")
    void shouldThrowExceptionWhenUseCaseThrowsException() {
        UserTypeInput input = new UserTypeInput("Admin", Set.of("ADMIN"));
        RuntimeException expectedException = new RuntimeException("Error creating user type");
        
        given(createUserTypeUseCase.execute(input)).willThrow(expectedException);

        assertThatThrownBy(() -> userTypeController.createUserType(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error creating user type");

        then(createUserTypeUseCase).should().execute(input);
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com UseCase nulo")
    void shouldThrowExceptionWhenUseCaseIsNull() {
        assertThatThrownBy(() -> new UserTypeController(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateUserTypeUseCase cannot be null.");
    }
}
