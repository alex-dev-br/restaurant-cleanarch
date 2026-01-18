package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RoleEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserTypeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import({UserGatewayAdapter.class})
@ComponentScan(basePackageClasses = {UserMapper.class})
@DisplayName("Testes de Integração para UserGatewayAdapter")
class UserGatewayAdapterTest {

    @Autowired
    private UserGatewayAdapter userGatewayAdapter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    private String userEmail;
    private UUID userUuid;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEmail = "user.gateway@mail.com";
        var userType = userTypeRepository.findById(1L).orElseThrow(RuntimeException::new);
        var user = new UserBuilder().withEmail(userEmail).withRole(UserManagementRoles.VIEW_USER).withoutId().buildEntity();
        user.setUserType(userType);
        userEntity = userRepository.save(user);
        userUuid = userEntity.getId();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteById(userUuid);
    }

    @Test
    @DisplayName("Deve verificar se o email está em uso e retorna false")
    void deveVerificarSeEmailEstaEmUsoERetornaFalse() {
        boolean result = userGatewayAdapter.existsUserWithEmail("freeemail@mail.com");
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve verificar se o email está em uso e retorna true")
    void deveVerificarSeEmailEstaEmUsoERetornaTrue() {
        boolean result = userGatewayAdapter.existsUserWithEmail(userEmail);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Deve retornar erro se o email for nulo")
    void deveRetornarErroSeEmailForNulo() {
        assertThatThrownBy(() -> userGatewayAdapter.existsUserWithEmail(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("email cannot be null");
    }

    @Test
    @DisplayName("Deve criar usuario com sucesso")
    void deveCriarUsuarioComSucesso() {
        var user = new UserBuilder().withRole(UserManagementRoles.VIEW_USER).withoutId().build();

        var savedUser = userGatewayAdapter.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(user.getName());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getPasswordHash()).isEqualTo(user.getPasswordHash());
        assertThat(savedUser.getUserType()).isEqualTo(user.getUserType());
        assertThat(savedUser.getAddress()).isEqualTo(user.getAddress());
    }

    @Test
    @DisplayName("Deve criar usuario sem endereco com sucesso")
    void deveCriarUsuarioSemEnderecoComSucesso() {
        var user = new UserBuilder().withRole(UserManagementRoles.VIEW_USER).withAddress(null).withoutId().build();

        var savedUser = userGatewayAdapter.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(user.getName());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getPasswordHash()).isEqualTo(user.getPasswordHash());
        assertThat(savedUser.getUserType()).isEqualTo(user.getUserType());
        assertThat(savedUser.getAddress()).isEqualTo(user.getAddress());
    }

    @Test
    @DisplayName("Deve retornar erro se o usuario for nulo")
    void deveRetornarErroSeUsuarioForNulo() {
        assertThatThrownBy(() -> userGatewayAdapter.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("user cannot be null");
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        userGatewayAdapter.deleteById(userUuid);

        var result = userRepository.findById(userUuid);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar erro se uuid for nulo ao deletar")
    void deveRetornarErroSeUuidForNuloAoDeletar() {
        assertThatThrownBy(() -> userGatewayAdapter.deleteById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id cannot be null");
    }

    @Test
    @DisplayName("Deve buscar usuário por id com sucesso")
    void deveBuscarUsuarioPeloIdComSucesso() {
        var result = userGatewayAdapter.findById(userUuid);
        assertThat(result).isNotEmpty();
        var user = result.get();
        assertThat(user.getId()).isEqualTo(userUuid);
        assertThat(user.getName()).isEqualTo(userEntity.getName());
        assertThat(user.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(user.getPasswordHash()).isEqualTo(userEntity.getPasswordHash());
        assertThat(user.getAddress()).usingRecursiveComparison().isEqualTo(userEntity.getAddress());
        assertThat(user.getUserType()).usingRecursiveComparison().ignoringFields("roles").isEqualTo(userEntity.getUserType());
        assertThat(user.getUserType().getRoles()).extracting(Role::name)
                .containsExactlyInAnyOrderElementsOf(userEntity.getUserType().getRoles().stream().map(RoleEntity::getName).toList());
    }

    @Test
    @DisplayName("Deve buscar usuário por id com sucesso")
    void deveRetornarOptionalVazioAoBuscarUsuarioInexistente() {
        assertThat(userGatewayAdapter.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar erro ser uuid for nulo")
    void deveRetornarErroSeUuidForNuloAoBuscar() {
        assertThatThrownBy(() -> userGatewayAdapter.deleteById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id cannot be null");
    }

    @Test
    @DisplayName("Deve buscar todos os usuários")
    void deveBuscarTodosOsUsuarios() {
        var pagedQuery = new PagedQuery<Void>(null, 0, 10);
        var result = userGatewayAdapter.findAll(pagedQuery);

        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isZero();
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalPages()).isGreaterThanOrEqualTo(1);
        assertThat(result.totalElements()).isGreaterThanOrEqualTo(1);
        assertThat(result.content()).isNotEmpty().extracting(User::getId).contains(userEntity.getId());
    }
}