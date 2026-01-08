package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MenuItemMapper.class, OpeningHoursMapper.class, UserMapper.class, AddressMapper.class})
public interface RestaurantMapper {
    RestaurantEntity toEntity(Restaurant domain);
    Restaurant toDomain(RestaurantEntity entity);

    @AfterMapping
    default void linkChildren(@MappingTarget RestaurantEntity restaurant) {
        if (restaurant.getMenu() != null) {
            restaurant.getMenu().forEach(mi -> mi.setRestaurant(restaurant));
        }
        if (restaurant.getOpeningHours() != null) {
            restaurant.getOpeningHours().forEach(oh -> oh.setRestaurant(restaurant));
        }
    }
}
