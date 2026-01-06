package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Address;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.AddressEmbeddableEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressEmbeddableEntity toEntity(Address domain);
    Address toDomain(AddressEmbeddableEntity entity);
}
