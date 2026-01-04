package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.*;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RoleEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserTypeEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RestaurantRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RoleRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(RestaurantGatewayAdapter.class)
@ComponentScan(basePackageClasses = {
        RestaurantMapper.class,
        MenuItemMapper.class,
        OpeningHoursMapper.class,
        UserTypeMapper.class,
        RoleMapper.class
})
@Sql(scripts = {"/roles/CREATE_ROLES.sql", "/user_type/CREATE_USER_TYPE.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/user_type/CLEAR_USER_TYPE.sql", "/roles/CLEAR_ROLES.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@DisplayName("Testes de Integração para RestaurantGatewayAdapter")
class RestaurantGatewayAdapterTest {

    @Autowired
    private RestaurantGatewayAdapter adapter;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private UserMapper userMapper;

    private UserEntity ownerEntity;
    private User ownerDomain;

    @BeforeEach
    void setUp() {
        var userTypeEntity = userTypeRepository.findByName("RESTAURANT_OWNER").orElseThrow(() -> new RuntimeException("UserType not found"));
        // Setup UserEntity
        ownerEntity = new UserEntity();
        ownerEntity.setUserType(userTypeEntity);
        ownerEntity.setName("Owner");
        ownerEntity.setEmail("owner@email.com");
        ownerEntity.setUserType(userTypeEntity);
        ownerEntity = userRepository.save(ownerEntity);

        ownerDomain = userMapper.toDomain(ownerEntity);
    }

    @Test
    @DisplayName("Deve salvar restaurante com sucesso")
    void shouldSaveRestaurantSuccessfully() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Tasty Food",
                new AddressBuilder().build(),
                "Brazilian",
                Collections.emptySet(),
                Collections.emptySet(),
                ownerDomain
        );

        // When
        Restaurant savedRestaurant = adapter.save(restaurant);

        // Then
        assertThat(savedRestaurant).isNotNull();
        assertThat(savedRestaurant.getId()).isNotNull();
        assertThat(savedRestaurant.getName()).isEqualTo("Tasty Food");
        
        // Verify persistence
        assertThat(restaurantRepository.findById(savedRestaurant.getId())).isPresent();
    }

    @Test
    @DisplayName("Deve retornar true se restaurante com nome existe")
    void shouldReturnTrueIfRestaurantWithNameExists() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Existing Restaurant",
                new AddressBuilder().build(),
                "Italian",
                Collections.emptySet(),
                Collections.emptySet(),
                ownerDomain
        );
        adapter.save(restaurant);

        // When
        boolean exists = adapter.existsRestaurantWithName("Existing Restaurant");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se restaurante com nome não existe")
    void shouldReturnFalseIfRestaurantWithNameDoesNotExist() {
        // When
        boolean exists = adapter.existsRestaurantWithName("Non Existent");

        // Then
        assertThat(exists).isFalse();
    }
}
