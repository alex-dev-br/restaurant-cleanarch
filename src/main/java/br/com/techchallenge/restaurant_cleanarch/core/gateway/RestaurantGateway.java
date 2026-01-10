package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;

import java.util.List;
import java.util.Optional;

public interface RestaurantGateway {

    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long id);

    /**
     * Verifica se já existe um restaurante com o mesmo nome
     * (útil para evitar duplicidade no cadastro)
     */
    boolean existsRestaurantWithName(String name);

    /**
     * Verifica se já existe um restaurante com o mesmo nome, exceto o próprio (para update)
     */
    boolean existsRestaurantWithNameExcludingId(String name, Long excludingId);

    List<Restaurant> findAll();

    void delete(Long id);

    Page<Restaurant> findByCuisineType(PagedQuery<String> query);
}
