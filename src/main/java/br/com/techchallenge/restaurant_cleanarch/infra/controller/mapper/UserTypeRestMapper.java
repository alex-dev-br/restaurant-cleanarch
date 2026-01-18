package br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserTypeRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.UserTypeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserTypeRestMapper {
    UserTypeResponse toResponse(UserTypeOutput output);
    CreateUserTypeInput toInput(UserTypeRequest response);
}
