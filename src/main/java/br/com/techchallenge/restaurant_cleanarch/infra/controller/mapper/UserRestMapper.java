package br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserOutput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {AddressRestMapper.class})
public interface UserRestMapper {
    UserResponse toResponse(UserOutput output);
    CreateUserInput toInput(UserRequest response);
    @Mapping(target = "id", source = "id")
    UpdateUserInput toUpdateInput(UserRequest response, UUID id);
}
