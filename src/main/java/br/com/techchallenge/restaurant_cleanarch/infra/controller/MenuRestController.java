package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.controller.MenuItemController;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper.MenuItemRestMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.MenuItemRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.MenuItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/restaurants/{restaurant-id}/menu")
public class MenuRestController {

    private final MenuItemController controller;
    private final MenuItemRestMapper menuItemRestMapper;

    public MenuRestController(MenuItemController controller, MenuItemRestMapper menuItemRestMapper) {
        this.controller = controller;
        this.menuItemRestMapper = menuItemRestMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MenuItemResponse> findAll (@PathVariable("restaurant-id") Long restaurantId,
        @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
        return controller.findByRestaurant(restaurantId, pageNumber, pageSize).mapItems(menuItemRestMapper::toResponse);
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> addInMenu (@PathVariable("restaurant-id") Long restaurantId,
                                                       @RequestBody @Valid MenuItemRequest menuItemRequest,
                                                       UriComponentsBuilder uriComponentsBuilder) {
        var createMenuItemInput = menuItemRestMapper.toCreateInput(menuItemRequest, restaurantId);
        var menuItemOutput = controller.addItemInMenu(createMenuItemInput);
        var uri = uriComponentsBuilder.path("/restaurants/{restaurant-id}/menu/{menu-id}")
                .buildAndExpand(restaurantId, menuItemOutput.id())
                .toUri();
        return ResponseEntity.created(uri).body(menuItemRestMapper.toResponse(menuItemOutput));
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateItem(@PathVariable("id") Long id, @RequestBody @Valid MenuItemRequest menuItemRequest) {
        var updateInput = menuItemRestMapper.toUpdateInput(menuItemRequest, id);
        controller.updateMenuItem(updateInput);
    }
}
