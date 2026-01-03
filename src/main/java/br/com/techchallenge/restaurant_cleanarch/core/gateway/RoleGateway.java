package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;

import java.util.Set;

public interface RoleGateway {
    Set<Role> getRolesByName(Set<String> rolesName);
    Set<Role> findAll();
}
