package br.com.techchallenge.restaurant_cleanarch.core.domain.roles;

public enum UserRoles implements ForGettingRoleName {
    RESTAURANT_OWNER;

    @Override
    public String getRoleName() {
        return name();
    }
}
