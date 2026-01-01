package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.RoleMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserTypeMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RoleEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserTypeEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RoleRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({UserTypeGatewayAdapter.class})
@ComponentScan(basePackageClasses = {UserTypeMapper.class, RoleMapper.class})
@DisplayName("Testes de Integração para UserTypeGatewayAdapter")
class UserTypeGatewayAdapterTest {

    @Autowired
    private UserTypeGatewayAdapter adapter;

    @Autowired
    private UserTypeRepository userTypeRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Deve salvar UserType com sucesso no banco de dados")
    void shouldSaveUserTypeSuccessfully() {
        // Given
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ADMIN");
        roleEntity = roleRepository.save(roleEntity);
        
        Role role = new Role(roleEntity.getId(), roleEntity.getName());
        UserType userType = new UserType(null, "Administrator", Set.of(role));

        // When
        UserType savedUserType = adapter.save(userType);

        // Then
        assertThat(savedUserType).isNotNull();
        assertThat(savedUserType.getId()).isNotNull();
        assertThat(savedUserType.getName()).isEqualTo("Administrator");
        assertThat(savedUserType.getRoles()).hasSize(1);
        assertThat(savedUserType.getRoles().iterator().next().name()).isEqualTo("ADMIN");
        
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
}
