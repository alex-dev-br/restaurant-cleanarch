package br.com.techchallenge.restaurant_cleanarch;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.*;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menu.CreateMenuUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menu.GetAllMenusUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menu.GetByIdMenuUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menu.UpdateMenuUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.CreateMenuItemUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.GetAllMenuItemsUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.GetByIdMenuItemUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.UpdateMenuItemUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.CreateRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.GetAllRestaurantsUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.GetByIdRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.UpdateRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.role.GetAllRolesUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.user.CreateUserUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.user.GetAllUsersUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.user.GetByIdUser;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.user.UpdateUserUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestDomain {

    public static void main(String[] args) {
        var restaurantOwnerRole = new Role(1L, UserRoles.RESTAURANT_OWNER.getRoleName());
        var userType = new UserType(1L, "Dono de Restaurante", Set.of(restaurantOwnerRole));
        var emailAddress = new EmailAddress("test@test.com");
        var userAddress = new Address(
            "Rua Teste", "123", "São Paulo", "SP", "12345-678", "Casa"
        );
        var user = new User (
            UUID.randomUUID(), "Test", emailAddress, userAddress, userType
        );

        var restaurantAddress = new Address(
            "Rua Teste", "250", "São Paulo", "SP", "12345-678", "Segundo andar"
        );

        var wednesday = new OpeningHours(1L, DayOfWeek.WEDNESDAY, LocalTime.of(18, 0), LocalTime.of(21, 0));
        var tuesday = new OpeningHours(1L, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(22, 0));
        var friday = new OpeningHours(1L, DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(22, 0));
        var openingHours = Set.of(wednesday, tuesday, friday);

        var temaki = new MenuItem(1L, "Temaki", "Temaki de Salmão", new BigDecimal("29.00"), true, "temaki_salmao.jpg");
        var yakisoba = new MenuItem(1L, "Yakisoba", "Yakisoba de Camarão", new BigDecimal("36.99"), true, "yakisoba_camarao.jpg");
        var menuItems = List.of(temaki, yakisoba);

        var menu = new Menu(1L, menuItems);

        var restaurant = new Restaurant(
            1L, "Test Restaurant", restaurantAddress, "Japonesa", openingHours, menu, user
        );

        // use cases

        LoggedUserGateway loggedUser = roleName -> true;

        // role
        RoleGateway roleGateway = null;
        new GetAllRolesUseCase(loggedUser, roleGateway).execute();

        //user type
        UserTypeGateway userTypeGateway = null;
        new CreateUserTypeUseCase(roleGateway, userTypeGateway, loggedUser).execute(new CreateUserTypeInput(null, null));
        new UpdateUserTypeUseCase(roleGateway, userTypeGateway, loggedUser).execute(new UpdateUserTypeInput(null, null, null));
        new GetByIdUserTypeUseCase(userTypeGateway,loggedUser).execute(0L);
        new GetAllUserTypesUseCase(userTypeGateway, loggedUser).execute();
        new DeleteUserTypeUseCase(userTypeGateway, loggedUser).execute(0L);

        //user
        UserGateway userGateway = null;
        new CreateUserUseCase(userGateway, userTypeGateway, loggedUser).execute(new CreateUserInput());
        new UpdateUserUseCase(userGateway, userTypeGateway, loggedUser).execute(new UpdateUserInput());
        new GetByIdUser(userGateway, loggedUser).execute(UUID.randomUUID());
        new GetAllUsersUseCase(userGateway, loggedUser).execute(); // paged

        //restaurant
        //qnd criar/altera restaurant que vem com menu e itens do menu deveriamos validar se o usuario tem permissão para criar/alterar menu e itens menu
        RestaurantGateway restaurantGateway = null;
        new CreateRestaurantUseCase(restaurantGateway, userGateway, loggedUser)
                .execute(new CreateRestaurantInput(null, null, null, null, null, null));
        new UpdateRestaurantUseCase(restaurantGateway, userGateway, loggedUser)
                .execute(new UpdateRestaurantInput(null, null, null, null, null, null, null));
        new GetByIdRestaurantUseCase(restaurantGateway, loggedUser).execute(0L);
        new GetAllRestaurantsUseCase(restaurantGateway, loggedUser).execute(); // paged

        //menu
        //qnd criar/altera menu que vem com itens do menu deveriamos validar se o usuario tem permissão para criar/alterar itens menu
        MenuGateway menuGateway = null;
        // restaurantId pode fica dentro do input ou ser um parametro
        // na saida o controller pode colocar o id no output
        new CreateMenuUseCase(menuGateway, restaurantGateway, loggedUser).execute(new CreateMenuInput());
        new UpdateMenuUseCase(menuGateway, restaurantGateway, loggedUser).execute(new UpdateMenuInput());
        new GetByIdMenuUseCase(menuGateway, loggedUser).execute(0L);
        new GetAllMenusUseCase(menuGateway, loggedUser).execute(); // paged

        //menu item
        MenuItemGateway menuItemGateway = null;
        //com classe nova de menu pode se trocar o id restaurant por id menu no input
        new CreateMenuItemUseCase(menuItemGateway, menuGateway, loggedUser).execute(new CreateMenuItemInput(null, null, null, null, null, null));
        new UpdateMenuItemUseCase(menuItemGateway, menuGateway, loggedUser).execute(new UpdateMenuItemInput(null, null, null, null, null, null, null));
        new GetByIdMenuItemUseCase(menuItemGateway, loggedUser).execute(0L);
        new GetAllMenuItemsUseCase(menuItemGateway, loggedUser).execute(); // paged


    }
}
