package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.*;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RestaurantRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    private User ownerDomain;

    @BeforeEach
    void setUp() {
        var userTypeEntity = userTypeRepository.findByName("RESTAURANT_OWNER")
                .orElseThrow(() -> new RuntimeException("UserType not found"));
        // Setup UserEntity
        var ownerEntity = new UserEntity();
        ownerEntity.setUserType(userTypeEntity);
        ownerEntity.setName("Owner");
        ownerEntity.setEmail("ownerId@email.com");
//        ownerEntity.setAddress(new AddressBuilder().buildEmbeddableEntity());
        ownerEntity.setPasswordHash("HASHED_TEST");

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
    @DisplayName("Deve atualizar restaurante com sucesso")
    void shouldUpdateRestaurantSuccessfully() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Original Name",
                new AddressBuilder().build(),
                "Original Cuisine",
                Collections.emptySet(),
                Collections.emptySet(),
                ownerDomain
        );
        Restaurant savedRestaurant = adapter.save(restaurant);
        Long restaurantId = savedRestaurant.getId();

        Restaurant updatedRestaurantDomain = new Restaurant(
                restaurantId,
                "Updated Name",
                savedRestaurant.getAddress(),
                "Updated Cuisine",
                savedRestaurant.getOpeningHours(),
                savedRestaurant.getMenu(),
                savedRestaurant.getOwner()
        );

        // When
        Restaurant result = adapter.save(updatedRestaurantDomain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(restaurantId);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getCuisineType()).isEqualTo("Updated Cuisine");

        Optional<RestaurantEntity> persistedEntity = restaurantRepository.findById(restaurantId);
        assertThat(persistedEntity).isPresent();
        assertThat(persistedEntity.get().getName()).isEqualTo("Updated Name");
        assertThat(persistedEntity.get().getCuisineType()).isEqualTo("Updated Cuisine");
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

    @Test
    @DisplayName("Deve retornar todos os restaurantes")
    void shouldReturnAllRestaurants() {
        // Given
        Restaurant restaurant1 = new Restaurant(
                null,
                "Restaurant 1",
                new AddressBuilder().build(),
                "Italian",
                Collections.emptySet(),
                Collections.emptySet(),
                ownerDomain
        );
        Restaurant restaurant2 = new Restaurant(
                null,
                "Restaurant 2",
                new AddressBuilder().build(),
                "Japanese",
                Collections.emptySet(),
                Collections.emptySet(),
                ownerDomain
        );
        adapter.save(restaurant1);
        adapter.save(restaurant2);

        // When
        List<Restaurant> restaurants = adapter.findAll();

        // Then
        assertThat(restaurants).hasSizeGreaterThanOrEqualTo(2);
        assertThat(restaurants).extracting(Restaurant::getName)
                .contains("Restaurant 1", "Restaurant 2");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver restaurantes")
    void shouldReturnEmptyListWhenNoRestaurants() {
        // Given
        restaurantRepository.deleteAll();

        // When
        List<Restaurant> restaurants = adapter.findAll();

        // Then
        assertThat(restaurants).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar restaurante com sucesso")
    void shouldDeleteRestaurantSuccessfully() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Restaurant to Delete",
                new AddressBuilder().build(),
                "Mexican",
                Collections.emptySet(),
                Collections.emptySet(),
                ownerDomain
        );
        Restaurant savedRestaurant = adapter.save(restaurant);
        Long id = savedRestaurant.getId();

        // When
        adapter.delete(id);

        // Then
        assertThat(restaurantRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Deve não lançar exceção ao deletar restaurante inexistente")
    void shouldNotThrowExceptionWhenDeletingNonExistentRestaurant() {
        // Given
        Long nonExistentId = 999L;

        // When
        adapter.delete(nonExistentId);

        // Then
        assertThat(restaurantRepository.findById(nonExistentId)).isEmpty();
    }
}
