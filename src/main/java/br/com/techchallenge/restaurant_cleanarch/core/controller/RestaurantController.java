package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.*;
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
    private final GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase;
    private final ListRestaurantsPagedUseCase listRestaurantsPagedUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase,
                                UpdateRestaurantUseCase updateRestaurantUseCase,
                                GetRestaurantByIdUseCase getRestaurantByIdUseCase,
                                ListRestaurantsUseCase listRestaurantsUseCase,
                                DeleteRestaurantUseCase deleteRestaurantUseCase,
                                ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase,
                                GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase,
                                ListRestaurantsPagedUseCase listRestaurantsPagedUseCase) {
        Objects.requireNonNull(createRestaurantUseCase, "CreateRestaurantUseCase cannot be null.");
        Objects.requireNonNull(updateRestaurantUseCase, "UpdateRestaurantUseCase cannot be null.");
        Objects.requireNonNull(getRestaurantByIdUseCase, "GetByIdRestaurantUseCase cannot be null.");
        Objects.requireNonNull(listRestaurantsUseCase, "GetAllRestaurantUseCase cannot be null.");
        Objects.requireNonNull(deleteRestaurantUseCase, "DeleteRestaurantUseCase cannot be null.");
        Objects.requireNonNull(listRestaurantsByCuisineTypeUseCase, "ListRestaurantsByCuisineTypeUseCase cannot be null.");
        Objects.requireNonNull(getRestaurantManagementByIdUseCase, "GetRestaurantManagementByIdUseCase cannot be null.");
        Objects.requireNonNull(listRestaurantsPagedUseCase, "ListRestaurantsPagedUseCase cannot be null.");
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
        this.getRestaurantByIdUseCase = getRestaurantByIdUseCase;
        this.listRestaurantsUseCase = listRestaurantsUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
        this.listRestaurantsByCuisineTypeUseCase = listRestaurantsByCuisineTypeUseCase;
        this.getRestaurantManagementByIdUseCase = getRestaurantManagementByIdUseCase;
        this.listRestaurantsPagedUseCase = listRestaurantsPagedUseCase;
    }

    public RestaurantPublicOutput createRestaurant(CreateRestaurantInput createRestaurantInput) {
        Objects.requireNonNull(createRestaurantInput, "CreateRestaurantInput cannot be null.");
        var restaurant = createRestaurantUseCase.execute(createRestaurantInput);
        return RestaurantPresenter.toOutput(restaurant);
    }

    public void updateRestaurant(UpdateRestaurantInput updateRestaurantInput) {
        Objects.requireNonNull(updateRestaurantInput, "UpdateRestaurantInput cannot be null.");
        updateRestaurantUseCase.execute(updateRestaurantInput);
    }

    public RestaurantPublicOutput findById(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");
        var restaurant = getRestaurantByIdUseCase.execute(id);
        return RestaurantPresenter.toOutput(restaurant);
    }

    public RestaurantManagementOutput findManagementById(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");
        var restaurant = getRestaurantManagementByIdUseCase.execute(id);
        return RestaurantPresenter.toManagementOutput(restaurant);
    }


    // Mantido por compatibilidade
    public List<RestaurantPublicOutput> findAll() {
        return listRestaurantsUseCase.execute()
                .stream()
                .map(RestaurantPresenter::toOutput)
                .toList();
    }

    public Page<RestaurantPublicOutput> findAll(int pageNumber, int pageSize) {
        var pagedQuery = new PagedQuery<Void>(null, pageNumber, pageSize);
        var page = listRestaurantsPagedUseCase.execute(pagedQuery);
        return page.mapItems(RestaurantPresenter::toOutput);
    }

    public Page<RestaurantPublicOutput> findByCuisineType(String cuisineType, int pageNumber, int pageSize) {
        var pagedQuery = new PagedQuery<>(cuisineType, pageNumber, pageSize);
        var page = listRestaurantsByCuisineTypeUseCase.execute(pagedQuery);
        return page.mapItems(RestaurantPresenter::toOutput);
    }

    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");
        deleteRestaurantUseCase.execute(id);
    }
}
