package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;

public class MenuItemPresenter {
    private MenuItemPresenter(){}

    public static MenuItemOutput toOutput(MenuItem menuItem) {
        return new MenuItemOutput(menuItem.getId(), menuItem.getName(), menuItem.getDescription(), menuItem.getPrice(), menuItem.getRestaurantOnly(), menuItem.getPhotoPath());
    }
}
