package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;

import java.util.Optional;

public interface MenuItemGateway {
    MenuItem save(MenuItem menuItem);
    Optional<MenuItem> findByUuid(Long id);
    void delete(Long id);
    boolean existsByNameAndRestaurant(String name, Long restaurantId);   // Para evitar itens duplicados no cardápio de um restaurante
    // Opcional futuro: List<MenuItem> findByRestaurantId(Long restaurantId);

}
