package br.com.techchallenge.restaurant_cleanarch.core.usecase.menu;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Menu;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Pagination;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuGateway;

public class GetAllMenusUseCase {
    public GetAllMenusUseCase(MenuGateway menuGateway, LoggedUserGateway loggedUser) {
    }

    public Pagination<Menu> execute() {
        return null;
    }
}
