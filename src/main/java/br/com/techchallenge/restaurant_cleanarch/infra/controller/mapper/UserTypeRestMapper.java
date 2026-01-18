package br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserTypeRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.UserTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserTypeRestMapper {
    UserTypeResponse toResponse(UserTypeOutput output);
    CreateUserTypeInput toInput(UserTypeRequest response);
    @Mapping(target = "id", source = "id")
    UpdateUserTypeInput toUpdateInput(UserTypeRequest response, Long id);
}
