package br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantManagementOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantPublicOutput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.RestaurantRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.RestaurantResponse;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.RestaurantSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OpeningHoursRestMapper.class, AddressRestMapper.class, MenuItemRestMapper.class, UserRestMapper.class})
public interface RestaurantRestMapper {

    CreateRestaurantInput toInput(RestaurantRequest request);

    RestaurantResponse toResponse(RestaurantManagementOutput output);

    RestaurantSummaryResponse toResponseSummary(RestaurantPublicOutput output);

    @Mapping(target = "id", expression = "java(id)")
    UpdateRestaurantInput toUpdateInput(RestaurantRequest request, Long id);
}
