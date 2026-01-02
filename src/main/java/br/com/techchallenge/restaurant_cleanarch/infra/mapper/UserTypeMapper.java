package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserTypeMapper {
    UserTypeEntity toEntity(UserType domain);
    UserType toDomain(UserTypeEntity entity);
}
