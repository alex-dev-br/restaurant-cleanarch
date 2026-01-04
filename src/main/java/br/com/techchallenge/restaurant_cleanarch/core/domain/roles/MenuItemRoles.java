package br.com.techchallenge.restaurant_cleanarch.core.domain.roles;

public enum MenuItemRoles implements ForGettingRoleName {
    CREATE_MENU_ITEM,
    UPDATE_MENU_ITEM,
    DELETE_MENU_ITEM,
    VIEW_MENU_ITEM;

    @Override
    public String getRoleName() {
        return name();
    }
}
