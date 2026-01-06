package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import lombok.Getter;

import java.util.List;

@Getter
public class Menu {
    private Long id;
    private List<MenuItem> menuItems;

    public Menu(Long id, List<MenuItem> menuItems) {
        this.id = id;
        this.menuItems = menuItems;
    }
}
