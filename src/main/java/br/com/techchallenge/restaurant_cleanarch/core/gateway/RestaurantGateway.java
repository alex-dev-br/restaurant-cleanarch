package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;

public interface RestaurantGateway {
    Restaurant save(Restaurant restaurant);
    boolean existsRestaurantWithName(String name);
}
