package br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.MenuItemRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.MenuItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemRestMapper {
    MenuItemResponse toResponse(MenuItemOutput output);

    @Mapping(target = "restaurantId", expression = "java(restaurantId)")
    CreateMenuItemInput toCreateInput(MenuItemRequest menuItemRequest, Long restaurantId);
}
