package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UserTypeController")
class UserTypeControllerTest {

    @Mock
    private CreateUserTypeUseCase createUserTypeUseCase;

    @Mock
    private UpdateUserTypeUseCase updateUserTypeUseCase;

    @Mock
    private DeleteUserTypeUseCase deleteUserTypeUseCase;

    @Mock
    private GetUserTypeByIdUseCase getUserTypeByIdUseCase;

    @Mock
    private ListUserTypesUseCase listUserTypesUseCase;

    @InjectMocks
    private UserTypeController userTypeController;

    @Captor
    private ArgumentCaptor<CreateUserTypeInput> userTypeInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateUserTypeInput> updateUserTypeInputCaptor;

    @Test
    @DisplayName("Deve criar UserType com sucesso e retornar UserTypeOutput")
    void shouldCreateUserTypeSuccessfully() {
        String roleName = "ADMIN";
        String userTypeName = "Administrator";
        CreateUserTypeInput input = new CreateUserTypeInput(userTypeName, Set.of(roleName));
        
        Role role = new Role(1L, roleName);
        UserType userType = new UserType(1L, userTypeName, Set.of(role));
        
        given(createUserTypeUseCase.execute(input)).willReturn(userType);

        UserTypeOutput result = userTypeController.createUserType(input);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userType.getId());
        assertThat(result.name()).isEqualTo(userType.getName());
        assertThat(result.roles()).containsExactly(roleName);

        then(createUserTypeUseCase).should().execute(userTypeInputCaptor.capture());
        CreateUserTypeInput capturedInput = userTypeInputCaptor.getValue();
        assertThat(capturedInput).isEqualTo(input);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CreateUserTypeUseCase lança exceção")
    void shouldThrowExceptionWhenUseCaseThrowsException() {
        CreateUserTypeInput input = new CreateUserTypeInput("Admin", Set.of("ADMIN"));
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
    @DisplayName("Deve deletar UserType com sucesso")
    void shouldDeleteUserTypeSuccessfully() {
        Long id = 1L;

        userTypeController.deleteUserType(id);

        then(deleteUserTypeUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando DeleteUserTypeUseCase lança exceção")
    void shouldThrowExceptionWhenDeleteUseCaseThrowsException() {
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("Error deleting user type");

        willThrow(expectedException).given(deleteUserTypeUseCase).execute(id);

        assertThatThrownBy(() -> userTypeController.deleteUserType(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error deleting user type");

        then(deleteUserTypeUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve retornar UserType por ID com sucesso")
    void shouldGetUserTypeByIdSuccessfully() {
        Long id = 1L;
        String roleName = "ADMIN";
        String userTypeName = "Administrator";
        Role role = new Role(1L, roleName);
        UserType userType = new UserType(id, userTypeName, Set.of(role));

        given(getUserTypeByIdUseCase.execute(id)).willReturn(userType);

        UserTypeOutput result = userTypeController.getUserTypeById(id);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo(userTypeName);
        assertThat(result.roles()).containsExactly(roleName);

        then(getUserTypeByIdUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando GetByIdUserTypeUseCase lança exceção")
    void shouldThrowExceptionWhenGetByIdUseCaseThrowsException() {
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("User type not found");

        given(getUserTypeByIdUseCase.execute(id)).willThrow(expectedException);

        assertThatThrownBy(() -> userTypeController.getUserTypeById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User type not found");

        then(getUserTypeByIdUseCase).should().execute(id);
    }

    @Test
    @DisplayName("Deve retornar todos os UserTypes com sucesso")
    void shouldGetAllUserTypesSuccessfully() {
        String roleName1 = "ADMIN";
        String userTypeName1 = "Administrator";
        Role role1 = new Role(1L, roleName1);
        UserType userType1 = new UserType(1L, userTypeName1, Set.of(role1));

        String roleName2 = "USER";
        String userTypeName2 = "User";
        Role role2 = new Role(2L, roleName2);
        UserType userType2 = new UserType(2L, userTypeName2, Set.of(role2));

        given(listUserTypesUseCase.execute()).willReturn(Set.of(userType1, userType2));

        List<UserTypeOutput> result = userTypeController.getAllUserTypes();

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result).extracting(UserTypeOutput::name).containsExactlyInAnyOrder(userTypeName1, userTypeName2);

        then(listUserTypesUseCase).should().execute();
    }

    @Test
    @DisplayName("Deve lançar exceção quando GetAllUserTypeUseCase lança exceção")
    void shouldThrowExceptionWhenGetAllUseCaseThrowsException() {
        RuntimeException expectedException = new RuntimeException("Error fetching user types");

        given(listUserTypesUseCase.execute()).willThrow(expectedException);

        assertThatThrownBy(() -> userTypeController.getAllUserTypes())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error fetching user types");

        then(listUserTypesUseCase).should().execute();
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com CreateUserTypeUseCase nulo")
    void shouldThrowExceptionWhenCreateUseCaseIsNull() {
        assertThatThrownBy(() -> new UserTypeController(null, updateUserTypeUseCase, deleteUserTypeUseCase, getUserTypeByIdUseCase, listUserTypesUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateUserTypeUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com UpdateUserTypeUseCase nulo")
    void shouldThrowExceptionWhenUpdateUseCaseIsNull() {
        assertThatThrownBy(() -> new UserTypeController(createUserTypeUseCase, null, deleteUserTypeUseCase, getUserTypeByIdUseCase, listUserTypesUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateUserTypeUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com DeleteUserTypeUseCase nulo")
    void shouldThrowExceptionWhenDeleteUseCaseIsNull() {
        assertThatThrownBy(() -> new UserTypeController(createUserTypeUseCase, updateUserTypeUseCase, null, getUserTypeByIdUseCase, listUserTypesUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DeleteUserTypeUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetByIdUserTypeUseCase nulo")
    void shouldThrowExceptionWhenGetByIdUseCaseIsNull() {
        assertThatThrownBy(() -> new UserTypeController(createUserTypeUseCase, updateUserTypeUseCase, deleteUserTypeUseCase, null, listUserTypesUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetByIdUserTypeUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetAllUserTypesUseCase nulo")
    void shouldThrowExceptionWhenGetAllUseCaseIsNull() {
        assertThatThrownBy(() -> new UserTypeController(createUserTypeUseCase, updateUserTypeUseCase, deleteUserTypeUseCase, getUserTypeByIdUseCase, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetAllUserTypesUseCase cannot be null.");
    }
}
