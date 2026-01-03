package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.RoleMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleAdapter implements RoleGateway {

    private final RoleRepository repository;
    private final RoleMapper roleMapper;

    public RoleAdapter(RoleRepository repository, RoleMapper roleMapper) {
        this.repository = repository;
        this.roleMapper = roleMapper;
    }

    @Override
    public Set<Role> getRolesByName(Set<String> rolesName) {
        Objects.requireNonNull(rolesName, "RolesName cannot be null.");
        if (rolesName.isEmpty()) return Set.of();
        return repository.findByNameIn(rolesName).stream().map(roleMapper::toDomain).collect(Collectors.toSet());
    }

    @Override
    public Set<Role> findAll() {
        return repository.findAll().stream().map(roleMapper::toDomain).collect(Collectors.toSet());
    }
}
