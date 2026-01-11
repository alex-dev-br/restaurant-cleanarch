package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.RestaurantPresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.*;

import java.util.List;
import java.util.Objects;

public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final GetRestaurantByIdUseCase getRestaurantByIdUseCase;
    private final ListRestaurantsUseCase listRestaurantsUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;
    private final ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase,
                                UpdateRestaurantUseCase updateRestaurantUseCase,
                                GetRestaurantByIdUseCase getRestaurantByIdUseCase,
                                ListRestaurantsUseCase listRestaurantsUseCase,
                                DeleteRestaurantUseCase deleteRestaurantUseCase,
                                ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase) {
        Objects.requireNonNull(createRestaurantUseCase, "CreateRestaurantUseCase cannot be null.");
        Objects.requireNonNull(updateRestaurantUseCase, "UpdateRestaurantUseCase cannot be null.");
        Objects.requireNonNull(getRestaurantByIdUseCase, "GetByIdRestaurantUseCase cannot be null.");
        Objects.requireNonNull(listRestaurantsUseCase, "GetAllRestaurantUseCase cannot be null.");
        Objects.requireNonNull(deleteRestaurantUseCase, "DeleteRestaurantUseCase cannot be null.");
        Objects.requireNonNull(listRestaurantsByCuisineTypeUseCase, "ListRestaurantsByCuisineTypeUseCase cannot be null.");
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
        this.getRestaurantByIdUseCase = getRestaurantByIdUseCase;
        this.listRestaurantsUseCase = listRestaurantsUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
        this.listRestaurantsByCuisineTypeUseCase = listRestaurantsByCuisineTypeUseCase;
    }

    public RestaurantOutput createRestaurant(CreateRestaurantInput createRestaurantInput) {
        Objects.requireNonNull(createRestaurantInput, "CreateRestaurantInput cannot be null.");
        var restaurant = createRestaurantUseCase.execute(createRestaurantInput);
        return RestaurantPresenter.toOutput(restaurant);
    }

    public void updateRestaurant(UpdateRestaurantInput updateRestaurantInput) {
        Objects.requireNonNull(updateRestaurantInput, "UpdateRestaurantInput cannot be null.");
        updateRestaurantUseCase.execute(updateRestaurantInput);
    }

    public RestaurantOutput findById(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");
        var restaurant = getRestaurantByIdUseCase.execute(id);
        return RestaurantPresenter.toOutput(restaurant);
    }

    public List<RestaurantOutput> findAll() {
        return listRestaurantsUseCase.execute()
                .stream()
                .map(RestaurantPresenter::toOutput)
                .toList();
    }

    public Page<RestaurantOutput> findByCuisineType(String cuisineType, int pageNumber, int pageSize) {
        var pagedQuery = new PagedQuery<>(cuisineType, pageNumber, pageSize);
        var page = listRestaurantsByCuisineTypeUseCase.execute(pagedQuery);
        return page.mapItems(RestaurantPresenter::toOutput);
    }

    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");
        deleteRestaurantUseCase.execute(id);
    }
}
