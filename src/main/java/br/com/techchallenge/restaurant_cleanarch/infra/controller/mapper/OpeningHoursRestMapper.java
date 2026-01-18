package br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.OpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.OpeningHoursRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OpeningHoursRestMapper {
    OpeningHoursInput toInput(OpeningHoursRequest request);
}
