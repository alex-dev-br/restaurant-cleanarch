package br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.MenuItemResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuItemRestMapper {
    MenuItemResponse toResponse(MenuItemOutput output);
}
