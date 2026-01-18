package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserGatewayAdapter implements UserGateway {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserGatewayAdapter(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.userMapper = Objects.requireNonNull(userMapper, "userMapper cannot be null");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        Objects.requireNonNull(id, "id cannot be null");
        return userRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(PagedQuery<Void> input) {
        var pageable = PageRequest.of(input.pageNumber(), input.pageSize());
        var resultPaged = userRepository.findAll(pageable);
        return new Page<> (
            resultPaged.getNumber(),
            resultPaged.getSize(),
            resultPaged.getTotalElements(),
            resultPaged.getContent().stream().map(userMapper::toDomain).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUserWithEmail(String email) {
        Objects.requireNonNull(email, "email cannot be null");
        var probe = new UserEntity();
        probe.setEmail(email);
        var example = ExampleMatcher.matching()
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.exact());
        return userRepository.exists(Example.of(probe, example));
    }


    @Override
    @Transactional
    public User save(User user) {
        Objects.requireNonNull(user, "user cannot be null");
        var entity = userMapper.toEntity(user);
        var saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Objects.requireNonNull(id, "id cannot be null");
        userRepository.deleteById(id);
    }
}

