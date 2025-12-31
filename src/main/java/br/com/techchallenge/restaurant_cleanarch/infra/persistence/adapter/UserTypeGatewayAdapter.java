package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserTypeMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserTypeEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserTypeRepository;
import org.springframework.stereotype.Component;

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
        return false;
    }

}
