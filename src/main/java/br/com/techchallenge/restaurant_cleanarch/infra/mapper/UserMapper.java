package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserTypeMapper.class, AddressMapper.class})
public interface UserMapper {
    UserEntity toEntity(User domain);
    User toDomain(UserEntity entity);
}
