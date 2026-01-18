package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.controller.RestaurantController;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantManagementOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantPublicOutput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper.RestaurantRestMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.RestaurantRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.RestaurantResponse;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.RestaurantSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/restaurants")
public class RestaurantRestController {

    private final RestaurantController controller;
    private final RestaurantRestMapper restaurantRestMapper;

    public RestaurantRestController(RestaurantController controller, RestaurantRestMapper restaurantRestMapper) {
        this.controller = controller;
        this.restaurantRestMapper = restaurantRestMapper;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> create(@RequestBody @Valid RestaurantRequest request, UriComponentsBuilder uriComponentsBuilder) {
        var restaurantInput = restaurantRestMapper.toInput(request);
        RestaurantManagementOutput restaurant = controller.createRestaurant(restaurantInput);
        var uri = uriComponentsBuilder.path("/restaurants/{id}").buildAndExpand(restaurant.id()).toUri();
        return ResponseEntity.created(uri).body(restaurantRestMapper.toResponse(restaurant));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @RequestBody @Valid RestaurantRequest  input) {
        var restaurantInput = restaurantRestMapper.toUpdateInput(input, id);
        controller.updateRestaurant(restaurantInput);
    }

    // Público
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantSummaryResponse> getPublicById(@PathVariable("id") Long id) {
        return controller.findById(id).map(restaurantRestMapper::toResponseSummary).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Gestão
    @GetMapping("/{id}/management")
    public ResponseEntity<RestaurantResponse> getManagementById(@PathVariable Long id) {
        return controller.findManagementById(id).map(restaurantRestMapper::toResponse).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<RestaurantPublicOutput> listPaged(
            @RequestParam(required = false) String cuisineType,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (cuisineType != null && !cuisineType.isBlank()) {
            return controller.findByCuisineType(cuisineType, pageNumber, pageSize);
        }
        return controller.findAll(pageNumber, pageSize);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        controller.deleteById(id);
    }
}
