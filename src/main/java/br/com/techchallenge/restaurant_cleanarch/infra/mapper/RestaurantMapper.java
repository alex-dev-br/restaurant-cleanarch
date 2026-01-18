package br.com.techchallenge.restaurant_cleanarch.infra.mapper;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper para conversão bidirecional entre Restaurant (domínio) e RestaurantEntity (infra).
 * - Domain → Entity: MapStruct
 * - Entity → Domain: manual (para respeitar imutabilidade/validações e evitar problemas com coleções imutáveis)
 */
@Mapper(
        componentModel = "spring",
        uses = {
                MenuItemMapper.class,
                OpeningHoursMapper.class,
                UserMapper.class,
                AddressMapper.class
        }
)
public abstract class RestaurantMapper {

    @Autowired protected MenuItemMapper menuItemMapper;
    @Autowired protected OpeningHoursMapper openingHoursMapper;
    @Autowired protected UserMapper userMapper;
    @Autowired protected AddressMapper addressMapper;

    // Domain → Entity: MapStruct gera automático
    @Mapping(target = "menu", source = "menu")
    @Mapping(target = "openingHours", source = "openingHours")
    @Mapping(target = "employeeLinks", ignore = true)  // Populado no RestaurantGatewayAdapter.save()
    public abstract RestaurantEntity toEntity(Restaurant domain);

    /**
     * Entity → Domain: manual.
     * Assim o MapStruct NÃO gera implementação e você evita warnings como "menu".
     */
    public Restaurant toDomain(RestaurantEntity entity) {
        if (entity == null) return null;

        Address address = addressMapper.toDomain(entity.getAddress());

        if (entity.getOwner() == null) {
            throw new IllegalStateException(
                    "Restaurante sem dono associado no banco - violação de integridade de domínio"
            );
        }

        User owner = userMapper.toDomain(entity.getOwner());

        Restaurant restaurant = new Restaurant(
                entity.getId(),
                entity.getName(),
                address,
                entity.getCuisineType(),
                owner
        );

        restaurant.addEmployees(mapEmployees(entity.employeesView()));
        restaurant.addOpeningHours(mapOpeningHours(entity.getOpeningHours()));
        restaurant.addMenuItems(mapMenuItems(entity.getMenu()));

        return restaurant;
    }

    @AfterMapping
    protected void linkChildren(@MappingTarget RestaurantEntity restaurant) {
        if (restaurant.getMenu() == null) restaurant.setMenu(new HashSet<>());
        if (restaurant.getOpeningHours() == null) restaurant.setOpeningHours(new HashSet<>());

        restaurant.getMenu().forEach(mi -> mi.setRestaurant(restaurant));
        restaurant.getOpeningHours().forEach(oh -> oh.setRestaurant(restaurant));
    }

    private Set<User> mapEmployees(Set<UserEntity> entities) {
        return (entities != null && !entities.isEmpty())
                ? entities.stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toSet())
                : Set.of();
    }


    private Set<OpeningHours> mapOpeningHours(Set<OpeningHoursEntity> entities) {
        return (entities != null && !entities.isEmpty())
                ? entities.stream().map(openingHoursMapper::toDomain).collect(Collectors.toSet())
                : Set.of();
    }

    private Set<MenuItem> mapMenuItems(Set<MenuItemEntity> entities) {
        return (entities != null && !entities.isEmpty())
                ? entities.stream().map(menuItemMapper::toDomain).collect(Collectors.toSet())
                : Set.of();
    }
}
