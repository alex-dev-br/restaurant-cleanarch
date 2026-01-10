package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.MenuItemMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.MenuItemEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.MenuItemRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RestaurantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + restaurantId));
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

    @Override
    public Page<MenuItem> findByRestaurant(PagedQuery<Long> query) {
        var page = PageRequest.of(query.pageNumber(), query.pageSize());
        var pagedResult = menuItemRepository.findByRestaurantId(query.filter(), page);
        return new Page<> (
                pagedResult.getNumber(),
                pagedResult.getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent().stream().map(mapper::toDomain).toList()
        );
    }
}
