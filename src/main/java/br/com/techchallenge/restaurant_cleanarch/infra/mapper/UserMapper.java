package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserTypeMapper.class, AddressMapper.class})
public interface UserMapper {

    @Mapping(target = "passwordHash", source = "passwordHash")
    UserEntity toEntity(User domain);

    @Mapping(target = "passwordHash", source = "passwordHash")
    User toDomain(UserEntity entity);
}
