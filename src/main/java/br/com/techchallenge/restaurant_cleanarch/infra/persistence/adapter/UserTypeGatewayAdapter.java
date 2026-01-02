package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserTypeMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserTypeEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserTypeRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component   // Cria um bean gerenciado pelo Spring
public class UserTypeGatewayAdapter implements UserTypeGateway {

    private final UserTypeRepository repository;
    private final UserTypeMapper mapper;   // Usar MapStruct para conversão

    public UserTypeGatewayAdapter(UserTypeRepository repository, UserTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserType save(UserType userType) {
        UserTypeEntity entity = mapper.toEntity(userType);
        entity = repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public boolean existsUserTypeWithName(String name) {
        var probe = new UserTypeEntity();
        probe.setName(name);

        var matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "roles")
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.exact());

        return repository.exists(Example.of(probe, matcher));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean isInUse(Long id) {
        return repository.countUsersByTypeId(id) > 0;
    }

    @Override
    public Optional<UserType> findByName(String name) {
        return repository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public Optional<UserType> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
