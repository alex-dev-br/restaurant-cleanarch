package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.MenuItemMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.*;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MenuItemGatewayAdapter implements MenuItemGateway {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper mapper;

    public MenuItemGatewayAdapter(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository, MenuItemMapper mapper) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
    }

    @Override
    public MenuItem save(MenuItem menuItem, Long restaurantId) {
        RestaurantEntity restaurantEntity = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restaurantId));
        MenuItemEntity entity = mapper.toEntity(menuItem, restaurantEntity);
        entity = menuItemRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return menuItemRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<MenuItem> findByRestaurantId(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        menuItemRepository.deleteById(id);
    }

    @Override
    public boolean existsByNameAndRestaurantId(String name, Long restaurantId) {
        return menuItemRepository.existsByNameAndRestaurantId(name.trim(), restaurantId);
    }

    @Override
    public Optional<Long> findRestaurantIdByItemId(Long itemId) {
        return menuItemRepository.findById(itemId)
                .map(entity -> entity.getRestaurant().getId());
    }

}
