package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.MenuItemMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.MenuItemEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.MenuItemRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import({MenuItemGatewayAdapter.class})
@ComponentScan(basePackageClasses = {MenuItemMapper.class})
@DisplayName("Testes de Integração para MenuItemGatewayAdapter")
class MenuItemGatewayAdapterTest {

    @Autowired
    private MenuItemGatewayAdapter adapter;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemMapper mapper;

    private RestaurantEntity restaurant;

    @BeforeEach
    void setUp() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();

        restaurant = new RestaurantEntity();
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Test Cuisine");
        restaurant = restaurantRepository.save(restaurant);
    }

    @Test
    @DisplayName("Deve salvar um item de menu com sucesso")
    void shouldSaveMenuItemSuccessfully() {
        // Given
        MenuItem menuItem = new MenuItem(null, "Pizza", "Delicious pizza", BigDecimal.valueOf(25.0), true, "path/to/photo");

        // When
        MenuItem savedMenuItem = adapter.save(menuItem, restaurant.getId());

        // Then
        assertThat(savedMenuItem).isNotNull();
        assertThat(savedMenuItem.getId()).isNotNull();
        assertThat(savedMenuItem.getName()).isEqualTo("Pizza");
        assertThat(menuItemRepository.findById(savedMenuItem.getId())).isPresent();
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar item de menu com restaurante inexistente")
    void shouldThrowExceptionWhenSavingMenuItemWithNonExistentRestaurant() {
        // Given
        MenuItem menuItem = new MenuItem(null, "Burger", "Juicy burger", BigDecimal.valueOf(15.0), true, "path/to/photo");

        // When & Then
        assertThatThrownBy(() -> adapter.save(menuItem, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurante não encontrado: 999");
    }

    @Test
    @DisplayName("Deve encontrar item de menu por ID")
    void shouldFindMenuItemById() {
        // Given
        MenuItemEntity entity = new MenuItemEntity();
        entity.setName("Pasta");
        entity.setDescription("Italian pasta");
        entity.setPrice(BigDecimal.valueOf(30.0));
        entity.setRestaurant(restaurant);
        entity.setRestaurantOnly(false);
        entity.setPhotoPath("path/to/photo");
        entity = menuItemRepository.save(entity);
        Long menuItemId = entity.getId();

        // When
        Optional<MenuItem> foundMenuItem = adapter.findById(menuItemId);

        // Then
        assertThat(foundMenuItem).isPresent();
        assertThat(foundMenuItem.get().getId()).isEqualTo(menuItemId);
        assertThat(foundMenuItem.get().getName()).isEqualTo("Pasta");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID de item de menu inexistente")
    void shouldReturnEmptyOptionalForNonExistentMenuItemId() {
        // When
        Optional<MenuItem> foundMenuItem = adapter.findById(999L);

        // Then
        assertThat(foundMenuItem).isNotPresent();
    }

    @Test
    @DisplayName("Deve encontrar itens de menu por ID do restaurante")
    void shouldFindMenuItemsByRestaurantId() {
        // Given
        var salad = new MenuItemEntity();
        salad.setName("Salad");
        salad.setDescription("Fresh salad");
        salad.setPrice(BigDecimal.valueOf(30.0));
        salad.setRestaurant(restaurant);
        salad.setRestaurantOnly(false);
        salad.setPhotoPath("path/to/photo");

        var soup = new MenuItemEntity();
        soup.setName("Soup");
        soup.setDescription("Hot soup");
        soup.setPrice(BigDecimal.valueOf(30.0));
        soup.setRestaurant(restaurant);
        soup.setRestaurantOnly(false);
        soup.setPhotoPath("path/to/photo");

        menuItemRepository.save(salad);
        menuItemRepository.save(soup);

        // When
        List<MenuItem> menuItems = adapter.findByRestaurantId(restaurant.getId());

        // Then
        assertThat(menuItems).hasSize(2);
        assertThat(menuItems).extracting(MenuItem::getName).containsExactlyInAnyOrder("Salad", "Soup");
    }

    @Test
    @DisplayName("Deve retornar lista vazia se não houver itens de menu para o restaurante")
    void shouldReturnEmptyListIfNoMenuItemsForRestaurant() {
        // When
        List<MenuItem> menuItems = adapter.findByRestaurantId(restaurant.getId());

        // Then
        assertThat(menuItems).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar item de menu por ID")
    void shouldDeleteMenuItemById() {
        // Given
        var fries = new MenuItemEntity();
        fries.setName("Fries");
        fries.setDescription("Crispy fries");
        fries.setPrice(BigDecimal.valueOf(30.0));
        fries.setRestaurant(restaurant);
        fries.setRestaurantOnly(false);
        fries.setPhotoPath("path/to/photo");
        MenuItemEntity entity = menuItemRepository.save(fries);
        Long menuItemId = entity.getId();

        // When
        adapter.deleteById(menuItemId);

        // Then
        assertThat(menuItemRepository.findById(menuItemId)).isNotPresent();
    }

    @Test
    @DisplayName("Deve verificar se item de menu existe por nome e ID do restaurante")
    void shouldCheckIfMenuItemExistsByNameAndRestaurantId() {
        // Given
        var iceCream = new MenuItemEntity();
        iceCream.setName("Ice Cream");
        iceCream.setDescription("Cold and sweet");
        iceCream.setPrice(BigDecimal.valueOf(30.0));
        iceCream.setRestaurant(restaurant);
        iceCream.setRestaurantOnly(false);
        iceCream.setPhotoPath("path/to/photo");
        menuItemRepository.save(iceCream);

        // When
        boolean exists = adapter.existsByNameAndRestaurantId("Ice Cream", restaurant.getId());
        boolean notExists = adapter.existsByNameAndRestaurantId("Cake", restaurant.getId());

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Deve encontrar o ID do restaurante pelo ID do item de menu")
    void shouldFindRestaurantIdByMenuItemId() {
        // Given
        var steak = new MenuItemEntity();
        steak.setName("Steak");
        steak.setDescription("Grilled steak");
        steak.setPrice(BigDecimal.valueOf(30.0));
        steak.setRestaurant(restaurant);
        steak.setRestaurantOnly(false);
        steak.setPhotoPath("path/to/photo");
        MenuItemEntity entity = menuItemRepository.save(steak);
        Long menuItemId = entity.getId();

        // When
        Optional<Long> foundRestaurantId = adapter.findRestaurantIdByItemId(menuItemId);

        // Then
        assertThat(foundRestaurantId).isPresent().contains(restaurant.getId());
    }

    @Test
    @DisplayName("Deve encontrar itens de menu por restaurante com paginação")
    void shouldFindByRestaurantWithPagination() {
        // Given
        var steak = new MenuItemEntity();
        steak.setName("Steak");
        steak.setDescription("Grilled steak");
        steak.setPrice(BigDecimal.valueOf(30.0));
        steak.setRestaurant(restaurant);
        steak.setRestaurantOnly(false);
        steak.setPhotoPath("path/to/photo");

        var fries = new MenuItemEntity();
        fries.setName("Fries");
        fries.setDescription("Crispy fries");
        fries.setPrice(BigDecimal.valueOf(30.0));
        fries.setRestaurant(restaurant);
        fries.setRestaurantOnly(false);
        fries.setPhotoPath("path/to/photo");

        menuItemRepository.save(steak);
        menuItemRepository.save(fries);

        int pageNumber = 0;
        int pageSize = 10;
        PagedQuery<Long> query = new PagedQuery<>(restaurant.getId(), pageNumber, pageSize);

        // When
        var result = adapter.findByRestaurant(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.currentPage()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalPages()).isOne();
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.content()).hasSize(2);
    }
}
