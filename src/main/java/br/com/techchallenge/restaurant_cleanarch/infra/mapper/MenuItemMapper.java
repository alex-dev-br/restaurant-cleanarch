package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {

    @Mapping(target = "restaurant", ignore = true)
    MenuItemEntity toEntity(MenuItem domain);

    @Mapping(target = "restaurant", source = "restaurantEntity")
    MenuItemEntity toEntity(MenuItem domain, RestaurantEntity restaurantEntity);

    MenuItem toDomain(MenuItemEntity entity);
}
