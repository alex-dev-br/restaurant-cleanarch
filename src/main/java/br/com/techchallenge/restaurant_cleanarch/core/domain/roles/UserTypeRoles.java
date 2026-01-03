package br.com.techchallenge.restaurant_cleanarch.core.domain.roles;

public enum UserTypeRoles implements ForGettingRoleName {
    CREATE_USER_TYPE,
    UPDATE_USER_TYPE,
    DELETE_USER_TYPE,
    VIEW_USER_TYPE;

    @Override
    public String getRoleName() {
        return name();
    }
}
