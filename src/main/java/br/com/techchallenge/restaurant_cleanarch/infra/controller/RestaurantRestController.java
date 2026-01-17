package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.controller.RestaurantController;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantManagementOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantPublicOutput;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
public class RestaurantRestController {

    private final RestaurantController controller;

    public RestaurantRestController(RestaurantController controller) {
        this.controller = controller;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantPublicOutput create(@RequestBody CreateRestaurantInput input) {
        return controller.createRestaurant(input);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody UpdateRestaurantInput input) {
        controller.updateRestaurant(input);
    }

    // Público
    @GetMapping("/{id}")
    public RestaurantPublicOutput getPublicById(@PathVariable Long id) {
        return controller.findById(id);
    }

    // Gestão
    @GetMapping("/{id}/management")
    public RestaurantManagementOutput getManagementById(@PathVariable Long id) {
        return controller.findManagementById(id);
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
