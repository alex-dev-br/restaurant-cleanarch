package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.RestaurantMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.*;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class RestaurantGatewayAdapter implements RestaurantGateway {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantGatewayAdapter(
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            RestaurantMapper restaurantMapper
    ) {
        this.restaurantRepository = Objects.requireNonNull(restaurantRepository, "restaurantRepository cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.restaurantMapper = Objects.requireNonNull(restaurantMapper, "restaurantMapper cannot be null");
    }

    @Override
    @Transactional
    public Restaurant save(Restaurant restaurant) {
        Objects.requireNonNull(restaurant, "restaurant cannot be null");

        RestaurantEntity entity = restaurantMapper.toEntity(restaurant);

        UUID ownerId = restaurant.getOwner().getId();
        UserEntity ownerRef = userRepository.getReferenceById(ownerId);
        entity.setOwner(ownerRef);

        if (restaurant.getId() != null) {
            entity.setId(restaurant.getId());
        }

        RestaurantEntity saved = restaurantRepository.save(entity);

        saved.clearEmployees();
        for (User employee : restaurant.getEmployees()) {
            UUID employeeId = employee.getId();
            if (employeeId == null) continue;
            UserEntity employeeRef = userRepository.getReferenceById(employeeId);
            saved.addEmployee(employeeRef);
        }

        saved = restaurantRepository.save(saved);

        // saved aqui pode não estar com fetch join; se você quiser garantir,
        // poderia recarregar via findByIdWithManagement(saved.getId()).
        return restaurantMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurant> findById(Long id) {
        Objects.requireNonNull(id, "id cannot be null");
        return restaurantRepository.findByIdWithPublicData(id)
                .map(restaurantMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurant> findByIdWithManagement(Long id) {
        Objects.requireNonNull(id, "id cannot be null");
        return restaurantRepository.findByIdWithManagement(id)
                .map(restaurantMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsRestaurantWithName(String name) {
        Objects.requireNonNull(name, "name cannot be null");

        var probe = new RestaurantEntity();
        probe.setName(name);

        var matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);

        return restaurantRepository.exists(Example.of(probe, matcher));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsRestaurantWithNameExcludingId(String name, Long excludingId) {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(excludingId, "excludingId cannot be null");

        var probe = new RestaurantEntity();
        probe.setName(name);

        var matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase());

        return restaurantRepository.findAll(Example.of(probe, matcher))
                .stream()
                .anyMatch(e -> e.getId() != null && !e.getId().equals(excludingId));
    }

    /**
     * Compatibilidade: mantém o contrato antigo, mas com um limite.
     * (Sugestão: use controller paginado e deixe esse método só para uso interno pontual.)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> findAll() {
        var page = findAll(new PagedQuery<>(null, 0, 100));
        return page.content();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Restaurant> findAll(PagedQuery<Void> query) {
        Objects.requireNonNull(query, "query cannot be null");

        var pageable = PageRequest.of(
                query.pageNumber(),
                query.pageSize(),
                Sort.by(Sort.Direction.ASC, "name")
        );

        // 1) pagina só IDs
        var idPage = restaurantRepository.findAllIds(pageable);
        var ids = idPage.getContent();

        if (ids.isEmpty()) {
            return new Page<>(
                    idPage.getNumber(),
                    idPage.getSize(),
                    idPage.getTotalElements(),
                    idPage.getTotalPages(),
                    List.of()
            );
        }

        // 2) fetch join pelos IDs
        var entities = restaurantRepository.findAllByIdInWithPublicData(ids);

        // 3) reorder (IN não garante ordem)
        var byId = new HashMap<Long, RestaurantEntity>(entities.size());
        for (var e : entities) byId.put(e.getId(), e);

        var orderedEntities = ids.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .toList();

        var page = new Page<>(
                idPage.getNumber(),
                idPage.getSize(),
                idPage.getTotalElements(),
                idPage.getTotalPages(),
                orderedEntities
        );

        return page.mapItems(restaurantMapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Objects.requireNonNull(id, "id cannot be null");
        restaurantRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Restaurant> findByCuisineType(PagedQuery<String> query) {
        Objects.requireNonNull(query, "query cannot be null");

        var pageable = PageRequest.of(
                query.pageNumber(),
                query.pageSize(),
                Sort.by(Sort.Direction.ASC, "cuisineType")
        );

        var idPage = restaurantRepository.findIdsByCuisineType(query.filter(), pageable);
        var ids = idPage.getContent();

        if (ids.isEmpty()) {
            return new Page<>(
                    idPage.getNumber(),
                    idPage.getSize(),
                    idPage.getTotalElements(),
                    idPage.getTotalPages(),
                    List.of()
            );
        }

        var entities = restaurantRepository.findAllByIdInWithPublicData(ids);

        var byId = new HashMap<Long, RestaurantEntity>(entities.size());
        for (var e : entities) byId.put(e.getId(), e);

        var orderedEntities = ids.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .toList();

        var page = new Page<>(
                idPage.getNumber(),
                idPage.getSize(),
                idPage.getTotalElements(),
                idPage.getTotalPages(),
                orderedEntities
        );

        return page.mapItems(restaurantMapper::toDomain);
    }
}
