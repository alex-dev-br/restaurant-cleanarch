package br.com.techchallenge.restaurant_cleanarch.infra.config;

import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.ListMenuItemsByRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant.*;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.role.ListRolesUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.user.*;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    /* ============================
       RESTAURANT
       ============================ */

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(
            LoggedUserGateway loggedUserGateway,
            RestaurantGateway restaurantGateway,
            UserGateway userGateway
    ) {
        return new CreateRestaurantUseCase(loggedUserGateway, restaurantGateway, userGateway);
    }

    @Bean
    public UpdateRestaurantUseCase updateRestaurantUseCase(
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway,
            UserGateway userGateway
    ) {
        return new UpdateRestaurantUseCase(loggedUserGateway, restaurantGateway, userGateway);
    }

    @Bean
    public GetRestaurantByIdUseCase getRestaurantByIdUseCase(
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new GetRestaurantByIdUseCase(restaurantGateway, loggedUserGateway);
    }

    @Bean
    public GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase(
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new GetRestaurantManagementByIdUseCase(restaurantGateway, loggedUserGateway);
    }

    @Bean
    public ListRestaurantsUseCase listRestaurantsUseCase(
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new ListRestaurantsUseCase(restaurantGateway, loggedUserGateway);
    }

    @Bean
    public ListRestaurantsPagedUseCase listRestaurantsPagedUseCase(
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new ListRestaurantsPagedUseCase(restaurantGateway, loggedUserGateway);
    }

    @Bean
    public ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase(
            LoggedUserGateway loggedUserGateway,
            RestaurantGateway restaurantGateway
    ) {
        return new ListRestaurantsByCuisineTypeUseCase(loggedUserGateway, restaurantGateway);
    }

    @Bean
    public DeleteRestaurantUseCase deleteRestaurantUseCase(
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new DeleteRestaurantUseCase(restaurantGateway, loggedUserGateway);
    }

    /* ============================
       MENU ITEM
       ============================ */

    @Bean
    public ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase(LoggedUserGateway loggedUserGateway, MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        return new ListMenuItemsByRestaurantUseCase(loggedUserGateway, menuItemGateway, restaurantGateway);
    }

    /* ============================
       ROLES
       ============================ */

    @Bean
    public ListRolesUseCase listRolesUseCase(LoggedUserGateway loggedUserGateway, RoleGateway roleGateway) {
        return new ListRolesUseCase(loggedUserGateway, roleGateway);
    }

    /* ============================
       USER TYPE
       ============================ */

    @Bean
    public CreateUserTypeUseCase createUserTypeUseCase(
            RoleGateway roleGateway,
            UserTypeGateway userGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new CreateUserTypeUseCase(roleGateway, userGateway, loggedUserGateway);
    }

    @Bean
    public UpdateUserTypeUseCase updateUserTypeUseCase(
            RoleGateway roleGateway,
            UserTypeGateway userGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new UpdateUserTypeUseCase(roleGateway, userGateway, loggedUserGateway);
    }

    @Bean
    public DeleteUserTypeUseCase deleteUserTypeUseCase(
            UserTypeGateway userTypeGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new DeleteUserTypeUseCase(userTypeGateway, loggedUserGateway);
    }

    @Bean
    public GetUserTypeByIdUseCase getUserTypeByIdUseCase(LoggedUserGateway loggedUserGateway, UserTypeGateway userTypeGateway) {
        return new GetUserTypeByIdUseCase(userTypeGateway, loggedUserGateway);
    }

    @Bean
    public ListUserTypesUseCase listUserTypesUseCase(UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        return new ListUserTypesUseCase(userTypeGateway, loggedUserGateway);
    }

    /* ============================
       USER
       ============================ */

    @Bean
    public CreateUserUseCase createUserUseCase(
            UserGateway userGateway,
            UserTypeGateway userTypeGateway,
            LoggedUserGateway loggedUserGateway,
            PasswordHasherGateway passwordHasherGateway
    ) {
        return new CreateUserUseCase(
                userGateway,
                userTypeGateway,
                loggedUserGateway,
                passwordHasherGateway
        );
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(
            UserGateway userGateway,
            UserTypeGateway userTypeGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new UpdateUserUseCase(userGateway, userTypeGateway, loggedUserGateway);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(
            UserGateway userGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new DeleteUserUseCase(userGateway, loggedUserGateway);
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase(
            UserGateway userGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new GetUserByIdUseCase(userGateway, loggedUserGateway);
    }

    @Bean
    public ListUsersUseCase listUsersUseCase(
            UserGateway userGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        return new ListUsersUseCase(userGateway, loggedUserGateway);
    }
}

