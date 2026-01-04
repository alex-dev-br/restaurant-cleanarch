package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.MenuItemMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.OpeningHoursMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.RestaurantMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.MenuItemRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.OpeningHoursRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RestaurantRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

public class RestaurantGatewayAdapter implements RestaurantGateway {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantGatewayAdapter(
            RestaurantRepository restaurantRepository,
            RestaurantMapper restaurantMapper
    ) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }


    @Override
    public Restaurant save(Restaurant restaurant) {
        var entity = restaurantMapper.toEntity(restaurant);
        entity = restaurantRepository.save(entity);
        return restaurantMapper.toDomain(entity);
    }

    @Override
    public boolean existsRestaurantWithName(String name) {
        var probe = new RestaurantEntity();
        probe.setName(name);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.EXACT);

        return restaurantRepository.exists(Example.of(probe, matcher));
    }
}
