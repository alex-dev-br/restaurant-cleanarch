package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.RestaurantPresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.CreateRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.DeleteRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.GetAllRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.GetByIdRestaurantUseCase;

import java.util.List;
import java.util.Objects;

public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final GetByIdRestaurantUseCase getByIdRestaurantUseCase;
    private final GetAllRestaurantUseCase getAllRestaurantUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase,
                                GetByIdRestaurantUseCase getByIdRestaurantUseCase,
                                GetAllRestaurantUseCase getAllRestaurantUseCase,
                                DeleteRestaurantUseCase deleteRestaurantUseCase) {
        Objects.requireNonNull(createRestaurantUseCase, "CreateRestaurantUseCase cannot be null.");
        Objects.requireNonNull(getByIdRestaurantUseCase, "GetByIdRestaurantUseCase cannot be null.");
        Objects.requireNonNull(getAllRestaurantUseCase, "GetAllRestaurantUseCase cannot be null.");
        Objects.requireNonNull(deleteRestaurantUseCase, "DeleteRestaurantUseCase cannot be null.");
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.getByIdRestaurantUseCase = getByIdRestaurantUseCase;
        this.getAllRestaurantUseCase = getAllRestaurantUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
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

    public List<RestaurantOutput> findAll() {
        return getAllRestaurantUseCase.execute()
                .stream()
                .map(RestaurantPresenter::toOutput)
                .toList();
    }

    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");
        deleteRestaurantUseCase.execute(id);
    }
}
