package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.MenuItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    MenuItemEntity toEntity(MenuItem domain);
    MenuItem toDomain(MenuItemEntity entity);
}
