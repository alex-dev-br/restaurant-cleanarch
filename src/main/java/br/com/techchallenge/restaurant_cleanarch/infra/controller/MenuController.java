package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.controller.MenuItemController;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper.MenuItemRestMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.MenuItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants/{restaurant-id}/menu")
public class MenuController {

    private final MenuItemController controller;
    private final MenuItemRestMapper menuItemRestMapper;

    public MenuController(MenuItemController controller, MenuItemRestMapper menuItemRestMapper) {
        this.controller = controller;
        this.menuItemRestMapper = menuItemRestMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MenuItemResponse> findAll (
        @PathVariable("restaurant-id") Long restaurantId,
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        return controller.findByRestaurant(restaurantId, pageNumber, pageSize).mapItems(menuItemRestMapper::toResponse);
    }
}
