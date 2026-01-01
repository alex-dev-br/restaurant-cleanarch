package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.CreateUserTypeUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UpdateUserTypeUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UserTypeController")
class UserTypeControllerTest {

    @Mock
    private CreateUserTypeUseCase createUserTypeUseCase;

    @Mock
    private UpdateUserTypeUseCase updateUserTypeUseCase;

    @InjectMocks
    private UserTypeController userTypeController;

    @Captor
    private ArgumentCaptor<UserTypeInput> userTypeInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateUserTypeInput> updateUserTypeInputCaptor;

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

        then(createUserTypeUseCase).should().execute(userTypeInputCaptor.capture());
        UserTypeInput capturedInput = userTypeInputCaptor.getValue();
        assertThat(capturedInput).isEqualTo(input);
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
    @DisplayName("Deve atualizar UserType com sucesso")
    void shouldUpdateUserTypeSuccessfully() {
        Long id = 1L;
        String roleName = "ADMIN";
        String userTypeName = "Administrator";
        UpdateUserTypeInput input = new UpdateUserTypeInput(id, userTypeName, Set.of(roleName));

        userTypeController.updateUserType(input);

        then(updateUserTypeUseCase).should().execute(updateUserTypeInputCaptor.capture());
        UpdateUserTypeInput capturedInput = updateUserTypeInputCaptor.getValue();
        assertThat(capturedInput).isNotNull();
        assertThat(capturedInput.id()).isNotNull().isEqualTo(input.id());
        assertThat(capturedInput.name()).isNotNull().isEqualTo(input.name());
        assertThat(capturedInput.roles()).isNotNull().containsExactlyInAnyOrder(roleName);
    }

    @Test
    @DisplayName("Deve lançar exceção quando UpdateUserTypeUseCase lança exceção")
    void shouldThrowExceptionWhenUpdateUseCaseThrowsException() {
        UpdateUserTypeInput input = new UpdateUserTypeInput(1L, "Admin", Set.of("ADMIN"));
        RuntimeException expectedException = new RuntimeException("Error updating user type");

        willThrow(expectedException).given(updateUserTypeUseCase).execute(input);

        assertThatThrownBy(() -> userTypeController.updateUserType(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error updating user type");

        then(updateUserTypeUseCase).should().execute(input);
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com CreateUserTypeUseCase nulo")
    void shouldThrowExceptionWhenCreateUseCaseIsNull() {
        assertThatThrownBy(() -> new UserTypeController(null, updateUserTypeUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateUserTypeUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com UpdateUserTypeUseCase nulo")
    void shouldThrowExceptionWhenUpdateUseCaseIsNull() {
        assertThatThrownBy(() -> new UserTypeController(createUserTypeUseCase, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateUserTypeUseCase cannot be null.");
    }
}
