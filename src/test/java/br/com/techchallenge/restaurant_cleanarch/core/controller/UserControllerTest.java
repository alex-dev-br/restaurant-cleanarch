package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserOutput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UserController")
class UserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @Mock
    private GetUserByIdUseCase getUserByIdUseCase;

    @Mock
    private ListUsersUseCase listUsersUseCase;

    @InjectMocks
    private UserController userController;

    @Captor
    private ArgumentCaptor<CreateUserInput> createUserCaptor;

    @Captor
    private ArgumentCaptor<UpdateUserInput> updateUserCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        var userBuilder = new UserBuilder();
        User expectedUser = userBuilder.build();
        var userInput = userBuilder.buildInput();

        given(createUserUseCase.execute(userInput)).willReturn(expectedUser);

        var createdUser = userController.create(userInput);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.id()).isNotNull();
        assertThat(createdUser.name()).isEqualTo(expectedUser.getName());
        assertThat(createdUser.email()).isEqualTo(expectedUser.getEmail());
        assertThat(createdUser.userType()).usingRecursiveComparison().ignoringFields("roles").isEqualTo(expectedUser.getUserType());
        assertThat(createdUser.userType().roles()).containsExactlyInAnyOrderElementsOf(expectedUser.getUserType().getRoles().stream().map(Role::name).toList());
        assertThat(createdUser.address()).usingRecursiveComparison().isEqualTo(expectedUser.getAddress());

        then(createUserUseCase).should().execute(createUserCaptor.capture());
        var userCaptorValue = createUserCaptor.getValue();
        assertThat(userCaptorValue).isNotNull();
        assertThat(userCaptorValue.name()).isEqualTo(userInput.name());
        assertThat(userCaptorValue.email()).isEqualTo(userInput.email());
        assertThat(userCaptorValue.password()).isEqualTo(userInput.password());
        assertThat(userCaptorValue.userTypeId()).isEqualTo(userInput.userTypeId());
        assertThat(userCaptorValue.address()).isEqualTo(userInput.address());
    }

    @Test
    @DisplayName("Deve alterar usuário com sucesso")
    void deveAlterarUsuarioComSucesso() {
        var userBuilder = new UserBuilder();
        UUID uuid = UUID.randomUUID();
        var userUpdateInput = userBuilder.withId(uuid).buildUpdateInput();

        userController.update(userUpdateInput);

        then(updateUserUseCase).should().execute(updateUserCaptor.capture());
        var userCaptorValue = updateUserCaptor.getValue();
        assertThat(userCaptorValue).isNotNull();
        assertThat(userUpdateInput.id()).isEqualTo(uuid);
        assertThat(userCaptorValue.name()).isEqualTo(userUpdateInput.name());
        assertThat(userCaptorValue.email()).isEqualTo(userUpdateInput.email());
        assertThat(userCaptorValue.userTypeId()).isEqualTo(userUpdateInput.userTypeId());
        assertThat(userCaptorValue.address()).isEqualTo(userUpdateInput.address());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        UUID uuid = UUID.randomUUID();
        userController.deleteById(uuid);

        then(deleteUserUseCase).should().execute(uuidArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Deve buscar usuário por Id")
    void deveBuscarUsuarioPorId() {
        var userBuilder = new UserBuilder();
        var uuid = UUID.randomUUID();
        var user = userBuilder.withId(uuid).build();

        given(getUserByIdUseCase.execute(uuid)).willReturn(Optional.of(user));

        var result = userController.findById(uuid);

        assertThat(result).isNotEmpty();
        UserOutput userOutput = result.get();
        assertThat(userOutput.id()).isEqualTo(uuid);

        then(getUserByIdUseCase).should().execute(uuidArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Deve buscar todos os usuários")
    void deveBuscarTodosOsUsuarios() {
        var userBuilder = new UserBuilder();
        var user = userBuilder.build();
        int pageNumber = 0;
        int pageSize = 10;
        PagedQuery<Void> pagedQuery = new PagedQuery<>(null, pageNumber, pageSize);
        var page = new Page<>(pageNumber, pageSize, 1, List.of(user));

        given(listUsersUseCase.execute(pagedQuery)).willReturn(page);

        var result = userController.findAll(pageNumber, pageSize);

        assertThat(result.pageNumber()).isZero();
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalPages()).isOne();
        assertThat(result.totalElements()).isOne();
        assertThat(result.content()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com CreateUserUseCase nulo")
    void shouldThrowExceptionWhenCreateUseCaseIsNull() {
        assertThatThrownBy(() -> new UserController(null, updateUserUseCase, deleteUserUseCase, getUserByIdUseCase, listUsersUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateUserUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com UpdateUserUseCase nulo")
    void shouldThrowExceptionWhenUpdateUseCaseIsNull() {
        assertThatThrownBy(() -> new UserController(createUserUseCase, null, deleteUserUseCase, getUserByIdUseCase, listUsersUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateUserUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com DeleteUserUseCase nulo")
    void shouldThrowExceptionWhenUpdateDeleteUseCaseIsNull() {
        assertThatThrownBy(() -> new UserController(createUserUseCase, updateUserUseCase, null, getUserByIdUseCase, listUsersUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DeleteUserUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com GetUserByIdUseCase nulo")
    void shouldThrowExceptionWhenGetUserByIdUseCaseUseCaseIsNull() {
        assertThatThrownBy(() -> new UserController(createUserUseCase, updateUserUseCase, deleteUserUseCase, null, listUsersUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetUserByIdUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com ListUsersUseCase nulo")
    void shouldThrowExceptionWhenListUsersUseCaseIsNull() {
        assertThatThrownBy(() -> new UserController(createUserUseCase, updateUserUseCase, deleteUserUseCase, getUserByIdUseCase, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ListUsersUseCase cannot be null.");
    }
}