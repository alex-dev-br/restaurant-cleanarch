package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.RestaurantPresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.CreateRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.GetByIdRestaurantUseCase;

import java.util.Objects;

public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final GetByIdRestaurantUseCase getByIdRestaurantUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase, GetByIdRestaurantUseCase getByIdRestaurantUseCase) {
        Objects.requireNonNull(createRestaurantUseCase, "CreateRestaurantUseCase cannot be null.");
        Objects.requireNonNull(getByIdRestaurantUseCase, "GetByIdRestaurantUseCase cannot be null.");
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.getByIdRestaurantUseCase = getByIdRestaurantUseCase;
    }

    public RestaurantOutput createRestaurant(CreateRestaurantInput createRestaurantInput) {
        Objects.requireNonNull(createRestaurantInput, "CreateRestaurantInput cannot be null.");
        var restaurant = createRestaurantUseCase.execute(createRestaurantInput);
        return RestaurantPresenter.toOutput(restaurant);
    }

    public RestaurantOutput findById(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");
        var restaurant = getByIdRestaurantUseCase.execute(id);
        return RestaurantPresenter.toOutput(restaurant);
    }
}
