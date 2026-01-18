package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.*;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.*;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.*;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

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

    @Autowired
    private TestEntityManager testEntityManager;

    private User ownerDomain;

    @BeforeEach
    void setUp() {
        var userTypeEntity = userTypeRepository.findByName("RESTAURANT_OWNER")
                .orElseThrow(() -> new RuntimeException("UserType not found"));

        var ownerEntity = new UserEntity();
        ownerEntity.setUserType(userTypeEntity);
        ownerEntity.setName("Owner");
        ownerEntity.setEmail("owner_" + java.util.UUID.randomUUID() + "@email.com");
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
                ownerDomain
        );

        // When
        Restaurant savedRestaurant = adapter.save(restaurant);

        // Then
        assertThat(savedRestaurant).isNotNull();
        assertThat(savedRestaurant.getId()).isNotNull();
        assertThat(savedRestaurant.getName()).isEqualTo("Tasty Food");

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
                ownerDomain
        );
        Restaurant savedRestaurant = adapter.save(restaurant);
        Long restaurantId = savedRestaurant.getId();

        testEntityManager.clear(); // Clear cache to avoid stale state

        Restaurant toUpdate = adapter.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found after save"));

        Restaurant updatedRestaurantDomain = new Restaurant(
                restaurantId,
                "Updated Name",
                toUpdate.getAddress(),
                "Updated Cuisine",
                toUpdate.getOwner()
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
    @DisplayName("Deve retornar true se restaurante com nome existe excluindo ID específico")
    void shouldReturnTrueIfRestaurantWithNameExistsExcludingId() {
        // Arrange - Cria UM restaurante (não podemos criar duplicado por causa da unique constraint)
        Restaurant restaurant = new Restaurant(
                null,
                "Unique Restaurant Name",
                new AddressBuilder().build(),
                "Italian",
                ownerDomain
        );
        Restaurant saved = adapter.save(restaurant);
        Long existingId = saved.getId();

        // Caso 1: Exclui um ID DIFERENTE → deve encontrar o restaurante → true
        boolean existsWithDifferentId = adapter.existsRestaurantWithNameExcludingId("Unique Restaurant Name", 999L);

        // Caso 2: Exclui o PRÓPRIO ID → não deve considerar → false
        boolean existsWithOwnId = adapter.existsRestaurantWithNameExcludingId("Unique Restaurant Name", existingId);

        // Caso 3: Nome que NÃO existe → stream vazia → false
        boolean existsWithNonExistentName = adapter.existsRestaurantWithNameExcludingId("Non Existent Name", existingId);

        // Asserts - Cobertura completa do lambda
        assertThat(existsWithDifferentId)
                .as("Deve retornar true quando há match e o ID é diferente (branch !equals = true)")
                .isTrue();

        assertThat(existsWithOwnId)
                .as("Deve retornar false quando o único match é o próprio ID (branch !equals = false)")
                .isFalse();

        assertThat(existsWithNonExistentName)
                .as("Deve retornar false quando stream é vazia (anyMatch false)")
                .isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se restaurante com nome não existe excluindo ID")
    void shouldReturnFalseIfRestaurantWithNameDoesNotExistExcludingId() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Unique Name",
                new AddressBuilder().build(),
                "Italian",
                ownerDomain
        );
        Restaurant saved = adapter.save(restaurant);
        Long id = saved.getId();

        // When
        boolean exists = adapter.existsRestaurantWithNameExcludingId("Non Existent Name", id);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se nome existe mas pertence ao mesmo ID excluído")
    void shouldReturnFalseIfNameExistsButBelongsToExcludedId() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Same Name",
                new AddressBuilder().build(),
                "Italian",
                ownerDomain
        );
        Restaurant saved = adapter.save(restaurant);
        Long id = saved.getId();

        // When
        boolean exists = adapter.existsRestaurantWithNameExcludingId("Same Name", id);

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
                ownerDomain
        );
        Restaurant restaurant2 = new Restaurant(
                null,
                "Restaurant 2",
                new AddressBuilder().build(),
                "Japanese",
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
    @DisplayName("Deve retornar página de restaurantes paginados")
    void shouldReturnPagedRestaurants() {
        // Given
        Restaurant restaurant1 = new Restaurant(
                null,
                "Restaurant A",
                new AddressBuilder().build(),
                "Italian",
                ownerDomain
        );
        Restaurant restaurant2 = new Restaurant(
                null,
                "Restaurant B",
                new AddressBuilder().build(),
                "Japanese",
                ownerDomain
        );
        Restaurant restaurant3 = new Restaurant(
                null,
                "Restaurant C",
                new AddressBuilder().build(),
                "Mexican",
                ownerDomain
        );
        adapter.save(restaurant1);
        adapter.save(restaurant2);
        adapter.save(restaurant3);

        PagedQuery<Void> query = new PagedQuery<>(null, 0, 2);

        // When
        Page<Restaurant> result = adapter.findAll(query);

        // Then
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(3L);
        assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.pageNumber()).isZero();
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

    @Test
    @DisplayName("Deve encontrar restaurantes por tipo de cozinha")
    void shouldFindRestaurantsByCuisineType() {
        // Given
        adapter.save(new Restaurant(null, "Italian Place", new AddressBuilder().build(), "Italian", ownerDomain));
        adapter.save(new Restaurant(null, "Super Italian", new AddressBuilder().build(), "Italian", ownerDomain));
        adapter.save(new Restaurant(null, "Japanese Place", new AddressBuilder().build(), "Japanese", ownerDomain));

        PagedQuery<String> query = new PagedQuery<>("Italian", 0, 10);

        // When
        Page<Restaurant> result = adapter.findByCuisineType(query);

        // Then
        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).extracting(Restaurant::getCuisineType).containsOnly("Italian");
    }

    @Test
    @DisplayName("Deve retornar página vazia se nenhum restaurante corresponder ao tipo de cozinha")
    void shouldReturnEmptyPageWhenNoRestaurantMatchesCuisineType() {
        // Given
        adapter.save(new Restaurant(null, "Japanese Place", new AddressBuilder().build(), "Japanese", ownerDomain));
        PagedQuery<String> query = new PagedQuery<>("Mexican", 0, 10);

        // When
        Page<Restaurant> result = adapter.findByCuisineType(query);

        // Then
        assertThat(result.content()).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar restaurantes por tipo de cozinha ignorando case")
    void shouldFindRestaurantsByCuisineTypeIgnoringCase() {
        // Given
        adapter.save(new Restaurant(null, "Italian Place", new AddressBuilder().build(), "Italian", ownerDomain));
        PagedQuery<String> query = new PagedQuery<>("italian", 0, 10);

        // When
        Page<Restaurant> result = adapter.findByCuisineType(query);

        // Then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().getCuisineType()).isEqualTo("Italian");
    }

    @Test
    @DisplayName("Deve paginar os resultados corretamente")
    void shouldPaginateResultsCorrectly() {
        // Given
        adapter.save(new Restaurant(null, "Italian 1", new AddressBuilder().build(), "Italian", ownerDomain));
        adapter.save(new Restaurant(null, "Italian 2", new AddressBuilder().build(), "Italian", ownerDomain));
        adapter.save(new Restaurant(null, "Italian 3", new AddressBuilder().build(), "Italian", ownerDomain));

        PagedQuery<String> firstPageQuery = new PagedQuery<>("Italian", 0, 2);
        PagedQuery<String> secondPageQuery = new PagedQuery<>("Italian", 1, 2);

        // When
        Page<Restaurant> firstPage = adapter.findByCuisineType(firstPageQuery);
        Page<Restaurant> secondPage = adapter.findByCuisineType(secondPageQuery);

        // Then
        assertThat(firstPage.content()).hasSize(2);
        assertThat(firstPage.totalElements()).isEqualTo(3L);
        assertThat(firstPage.totalPages()).isEqualTo(2);
        assertThat(firstPage.pageNumber()).isZero();

        assertThat(secondPage.content()).hasSize(1);
        assertThat(secondPage.totalElements()).isEqualTo(3L);
        assertThat(secondPage.totalPages()).isEqualTo(2);
        assertThat(secondPage.pageNumber()).isOne();
    }

    @Test
    @DisplayName("Deve encontrar restaurante por ID")
    void shouldFindById() {
        // Arrange
        Restaurant restaurant = new Restaurant(
                null,
                "Test Restaurant",
                new AddressBuilder().build(),
                "Test Cuisine",
                ownerDomain
        );
        Restaurant saved = adapter.save(restaurant);
        Long id = saved.getId();

        // Act
        Optional<Restaurant> result = adapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getName()).isEqualTo("Test Restaurant");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void shouldReturnEmptyWhenFindByNonExistentId() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        Optional<Restaurant> result = adapter.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar restaurante por ID com dados de management")
    void shouldFindByIdWithManagement() {
        // Arrange
        Restaurant restaurant = new Restaurant(
                null,
                "Management Test",
                new AddressBuilder().build(),
                "Test Cuisine",
                ownerDomain
        );
        Restaurant saved = adapter.save(restaurant);
        Long id = saved.getId();

        // Act
        Optional<Restaurant> result = adapter.findByIdWithManagement(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getName()).isEqualTo("Management Test");
        // Verificar se dados de management estão presentes (ex: owner carregado)
        assertThat(result.get().getOwner()).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente com management")
    void shouldReturnEmptyWhenFindByNonExistentIdWithManagement() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        Optional<Restaurant> result = adapter.findByIdWithManagement(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve ignorar employee sem ID ao salvar restaurante")
    void shouldIgnoreEmployeeWithoutIdWhenSavingRestaurant() {
        // Arrange
        Restaurant restaurant = new Restaurant(
                null,
                "Restaurant With Invalid Employee",
                new AddressBuilder().build(),
                "Italian",
                ownerDomain
        );

        // employee sem ID
        User employeeWithoutId = new UserBuilder()
                .withId(null)
                .build();

        restaurant.addEmployee(employeeWithoutId);

        // Act
        Restaurant saved = adapter.save(restaurant);

        // Assert
        RestaurantEntity entity = restaurantRepository.findById(saved.getId()).orElseThrow();
        assertThat(entity.getEmployeeLinks()).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar restaurante sem funcionários quando employees estiver vazio")
    void shouldSaveRestaurantWithEmptyEmployees() {
        Restaurant restaurant = new Restaurant(
                null,
                "Restaurant No Employees",
                new AddressBuilder().build(),
                "Italian",
                ownerDomain
        );

        Restaurant saved = adapter.save(restaurant);

        RestaurantEntity entity = restaurantRepository.findById(saved.getId()).orElseThrow();
        assertThat(entity.getEmployeeLinks()).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar restaurante quando employees for null")
    void shouldSaveRestaurantWhenEmployeesIsNull() {
        Restaurant restaurant = new Restaurant(
                null,
                "Restaurant Null Employees",
                new AddressBuilder().build(),
                "Italian",
                ownerDomain
        );


        Restaurant saved = adapter.save(restaurant);

        assertThat(saved).isNotNull();
    }

}