package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.RestaurantMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RestaurantRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.List;
import java.util.Optional;

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
    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantMapper::toDomain);
    }

    @Override
    public boolean existsRestaurantWithName(String name) {
        var probe = new RestaurantEntity();
        probe.setName(name);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.EXACT);

        return restaurantRepository.exists(Example.of(probe, matcher));
    }

    @Override
    public boolean existsRestaurantWithNameExcludingId(String name, Long excludingId) {
        RestaurantEntity probe = new RestaurantEntity();
        probe.setName(name);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase());

        return restaurantRepository.findAll(Example.of(probe, matcher))
                .stream()
                .anyMatch(entity -> !entity.getId().equals(excludingId));
    }

    @Override
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Long id) {
        restaurantRepository.deleteById(id);
    }

    @Override
    public Page<Restaurant> findByCuisineType(PagedQuery<String> query) {
        return null;
    }
}
