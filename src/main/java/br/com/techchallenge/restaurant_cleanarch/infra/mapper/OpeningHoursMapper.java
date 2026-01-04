package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.OpeningHoursEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OpeningHoursMapper {
    OpeningHoursEntity toEntity(OpeningHours domain);
    OpeningHours toDomain(OpeningHoursEntity entity);
}
