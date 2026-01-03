package br.com.techchallenge.restaurant_cleanarch.core.domain.roles;

public enum RoleRoles implements ForGettingRoleName {
    VIEW_ROLE;

    @Override
    public String getRoleName() {
        return name();
    }
}
