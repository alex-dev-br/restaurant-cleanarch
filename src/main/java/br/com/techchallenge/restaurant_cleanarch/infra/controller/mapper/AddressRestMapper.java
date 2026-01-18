package br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.AddressInput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.AddressRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.AddressResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressRestMapper {
    AddressInput toInput(AddressRequest addressRequest);
    AddressResponse toResponse(AddressInput addressInput);
}
