package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;

import java.util.*;

public interface MenuItemGateway {
    MenuItem save(MenuItem menuItem, Long restaurantId);
    Optional<MenuItem> findById(Long id);
    List<MenuItem> findByRestaurantId(Long restaurantId);
    void deleteById(Long id);
    boolean existsByNameAndRestaurantId(String name, Long restaurantId);   // Para evitar itens duplicados no cardápio de um restaurante
    Optional<Long> findRestaurantIdByItemId(Long itemId);

}
