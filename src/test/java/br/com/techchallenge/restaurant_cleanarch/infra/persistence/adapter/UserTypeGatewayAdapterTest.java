package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.RoleMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserTypeMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RoleEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserTypeEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RoleRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({UserTypeGatewayAdapter.class})
@ComponentScan(basePackageClasses = {UserTypeMapper.class, RoleMapper.class})
@Sql(scripts = {"/roles/CREATE_ROLES.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/roles/CLEAR_ROLES.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@DisplayName("Testes de Integração para UserTypeGatewayAdapter")
class UserTypeGatewayAdapterTest {

    @Autowired
    private UserTypeGatewayAdapter adapter;

    @Autowired
    private UserTypeRepository userTypeRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        roleEntity = roleRepository.findByName(UserTypeRoles.CREATE_USER_TYPE.getRoleName()).orElseThrow(() -> new RuntimeException("Roles não foram criadas"));
    }

    @Test
    @DisplayName("Deve salvar UserType com sucesso no banco de dados")
    void shouldSaveUserTypeSuccessfully() {
        // Given
        Role role = new Role(roleEntity.getId(), roleEntity.getName());
        UserType userType = new UserType(null, "Administrator", Set.of(role));

        // When
        UserType savedUserType = adapter.save(userType);

        // Then
        assertThat(savedUserType).isNotNull();
        assertThat(savedUserType.getId()).isNotNull();
        assertThat(savedUserType.getName()).isEqualTo("Administrator");
        assertThat(savedUserType.getRoles()).hasSize(1);
        assertThat(savedUserType.getRoles().iterator().next().name()).isEqualTo(UserTypeRoles.CREATE_USER_TYPE.getRoleName());
        
        // Verify in repository
        UserTypeEntity savedEntity = userTypeRepository.findById(savedUserType.getId()).orElseThrow();
        assertThat(savedEntity.getName()).isEqualTo("Administrator");
    }

    @Test
    @DisplayName("Deve retornar true se UserType com nome existe no banco")
    void shouldReturnTrueIfUserTypeWithNameExists() {
        // Given
        UserTypeEntity entity = new UserTypeEntity();
        entity.setName("Administrator");
        userTypeRepository.save(entity);

        // When
        boolean exists = adapter.existsUserTypeWithName("Administrator");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se UserType com nome não existe no banco")
    void shouldReturnFalseIfUserTypeWithNameDoesNotExist() {
        // When
        boolean exists = adapter.existsUserTypeWithName("NonExistent");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve encontrar UserType por nome quando existe")
    void shouldFindUserTypeByNameWhenExists() {
        // Given
        UserTypeEntity entity = new UserTypeEntity();
        entity.setName("Manager");
        entity.setRoles(Set.of(roleEntity));
        userTypeRepository.save(entity);

        // When
        Optional<UserType> foundUserType = adapter.findByName("Manager");

        // Then
        assertThat(foundUserType).isPresent();
        assertThat(foundUserType.get().getName()).isEqualTo("Manager");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar UserType por nome inexistente")
    void shouldReturnEmptyWhenFindingUserTypeByNonExistentName() {
        // When
        Optional<UserType> foundUserType = adapter.findByName("NonExistent");

        // Then
        assertThat(foundUserType).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar UserType por ID quando existe")
    void shouldFindUserTypeByIdWhenExists() {
        // Given
        UserTypeEntity entity = new UserTypeEntity();
        entity.setName("Supervisor");
        entity.setRoles(Set.of(roleEntity));
        entity = userTypeRepository.save(entity);

        // When
        Optional<UserType> foundUserType = adapter.findById(entity.getId());

        // Then
        assertThat(foundUserType).isPresent();
        assertThat(foundUserType.get().getId()).isEqualTo(entity.getId());
        assertThat(foundUserType.get().getName()).isEqualTo("Supervisor");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar UserType por ID inexistente")
    void shouldReturnEmptyWhenFindingUserTypeByNonExistentId() {
        // When
        Optional<UserType> foundUserType = adapter.findById(999L);

        // Then
        assertThat(foundUserType).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar UserType com sucesso")
    void shouldDeleteUserTypeSuccessfully() {
        // Given
        UserTypeEntity entity = new UserTypeEntity();
        entity.setName("ToDelete");
        entity.setRoles(Set.of(roleEntity));
        entity = userTypeRepository.save(entity);
        Long id = entity.getId();

        // When
        adapter.delete(id);

        // Then
        Optional<UserTypeEntity> deletedEntity = userTypeRepository.findById(id);
        assertThat(deletedEntity).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar true se UserType está em uso")
    void shouldReturnTrueIfUserTypeIsInUse() {
        // Given
        UserTypeEntity userTypeEntity = new UserTypeEntity();
        userTypeEntity.setName("InUse");
        userTypeEntity.setRoles(Set.of(roleEntity));
        userTypeEntity = userTypeRepository.save(userTypeEntity);

        UserEntity userEntity = new UserEntity();
        userEntity.setUserType(userTypeEntity);
        userRepository.save(userEntity);

        // When
        boolean inUse = adapter.isInUse(userTypeEntity.getId());

        // Then
        assertThat(inUse).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se UserType não está em uso")
    void shouldReturnFalseIfUserTypeIsNotInUse() {
        // Given
        UserTypeEntity userTypeEntity = new UserTypeEntity();
        userTypeEntity.setName("NotInUse");
        userTypeEntity.setRoles(Set.of(roleEntity));
        userTypeEntity = userTypeRepository.save(userTypeEntity);

        // When
        boolean inUse = adapter.isInUse(userTypeEntity.getId());

        // Then
        assertThat(inUse).isFalse();
    }

    @Test
    @DisplayName("Deve retornar todos os UserTypes")
    void shouldReturnAllUserTypes() {
        // Given
        UserTypeEntity entity1 = new UserTypeEntity();
        entity1.setName("Type1");
        entity1.setRoles(Set.of(roleEntity));
        userTypeRepository.save(entity1);

        UserTypeEntity entity2 = new UserTypeEntity();
        entity2.setName("Type2");
        entity2.setRoles(Set.of(roleEntity));
        userTypeRepository.save(entity2);

        // When
        Set<UserType> allUserTypes = adapter.findAll();

        // Then
        assertThat(allUserTypes).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allUserTypes).extracting(UserType::getName).contains("Type1", "Type2");
    }
}
