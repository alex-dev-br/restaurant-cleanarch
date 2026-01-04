package br.com.techchallenge.restaurant_cleanarch.core.domain.roles;

public enum RestaurantRoles implements ForGettingRoleName {
    CREATE_RESTAURANT,
    UPDATE_RESTAURANT,
    DELETE_RESTAURANT,
    VIEW_RESTAURANT;

    @Override
    public String getRoleName() {
        return name();
    }
}
