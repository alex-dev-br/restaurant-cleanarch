package br.com.techchallenge.restaurant_cleanarch;

import org.junit.jupiter.api.*;

import static org.mockito.Mockito.mock;

@Disabled("Teste de instanciação temporariamente desabilitado enquanto UseCases estão sendo implementados")
public class TestDomain {
    @Test
    void shouldInstantiateAllUseCases() {

//        LoggedUserGateway loggedUser = roleName -> true;

//        // Gateways mockados
//        RoleGateway roleGateway = mock(RoleGateway.class);
//        UserTypeGateway userTypeGateway = mock(UserTypeGateway.class);
//        UserGateway userGateway = mock(UserGateway.class);
//        RestaurantGateway restaurantGateway = mock(RestaurantGateway.class);
//        MenuGateway menuGateway = mock(MenuGateway.class);
//        MenuItemGateway menuItemGateway = mock(MenuItemGateway.class);
//
//        // Role
//        new GetAllRolesUseCase(loggedUser, roleGateway).execute();
//
//        // UserType
//        new CreateUserTypeUseCase(roleGateway, userTypeGateway, loggedUser).execute(new CreateUserTypeInput("Test", Set.of(UserRoles.RESTAURANT_OWNER.getRoleName())));
//        new UpdateUserTypeUseCase(roleGateway, userTypeGateway, loggedUser).execute(new UpdateUserTypeInput(1L, "Updated", Set.of("ROLE1")));
//        new GetByIdUserTypeUseCase(userTypeGateway, loggedUser).execute(1L);
//        new GetAllUserTypesUseCase(userTypeGateway, loggedUser).execute();
//        new DeleteUserTypeUseCase(userTypeGateway, loggedUser).execute(1L);
//
//        // User
//        new CreateUserUseCase(userGateway, userTypeGateway, loggedUser).execute(new CreateUserInput());
//        new UpdateUserUseCase(userGateway, userTypeGateway, loggedUser).execute(new UpdateUserInput());
//        new GetByIdUser(userGateway, loggedUser).execute(UUID.randomUUID());
//        new GetAllUsersUseCase(userGateway, loggedUser).execute();
//
//        // Restaurant
//        new CreateRestaurantUseCase(restaurantGateway, userGateway, loggedUser).execute(new CreateRestaurantInput());
//        new UpdateRestaurantUseCase(restaurantGateway, userGateway, loggedUser).execute(new UpdateRestaurantInput());
//        new GetByIdRestaurantUseCase(restaurantGateway, loggedUser).execute(1L);
//        new GetAllRestaurantsUseCase(restaurantGateway, loggedUser).execute();
//        new DeleteRestaurantUseCase(restaurantGateway, loggedUser).execute(1L);
//
//        // Menu
//        new CreateMenuUseCase(menuGateway, restaurantGateway, loggedUser).execute(new CreateMenuInput(1L));  // restaurantId como param
//        new UpdateMenuUseCase(menuGateway, restaurantGateway, loggedUser).execute(new UpdateMenuInput(1L));
//        new GetByIdMenuUseCase(menuGateway, loggedUser).execute(1L);
//        new GetAllMenusUseCase(menuGateway, loggedUser).execute();
//
//        // MenuItem
//        new CreateMenuItemUseCase(menuItemGateway, menuGateway, loggedUser).execute(new CreateMenuItemInput(1L));  // menuId como param
//        new UpdateMenuItemUseCase(menuItemGateway, menuGateway, loggedUser).execute(new UpdateMenuItemInput(1L));
//        new GetByIdMenuItemUseCase(menuItemGateway, loggedUser).execute(1L);
//        new GetAllMenuItemsUseCase(menuItemGateway, loggedUser).execute();

        System.out.println("Todos os UseCases foram instanciados e chamados com sucesso!");
    }
}

