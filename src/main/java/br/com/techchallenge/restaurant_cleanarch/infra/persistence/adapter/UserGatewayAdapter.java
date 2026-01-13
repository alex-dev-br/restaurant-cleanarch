package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public List<User> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUserWithEmail(String email) {
        return existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        Objects.requireNonNull(email, "email cannot be null");

        String normalized = email.trim();
        if (normalized.isBlank()) return false;

        return userRepository.existsByEmailIgnoreCase(normalized);
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

