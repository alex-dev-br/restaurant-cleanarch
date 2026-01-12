package br.com.techchallenge.restaurant_cleanarch.infra.auth;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.*;

import java.util.*;

public class FakeUsers {

    private static final String PASSWORD_HASH = "{fake}dev-password-hash";

    private static final UUID DEV_OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");


    public static User devAdminOwnerUser() {
        Set<Role> roles = Set.of(
                new Role(null, UserRoles.RESTAURANT_OWNER.getRoleName()),

                new Role(null, RestaurantRoles.CREATE_RESTAURANT.getRoleName()),
                new Role(null, RestaurantRoles.UPDATE_RESTAURANT.getRoleName()),
                new Role(null, RestaurantRoles.DELETE_RESTAURANT.getRoleName()),
                new Role(null, RestaurantRoles.VIEW_RESTAURANT.getRoleName()),

                new Role(null, UserManagementRoles.CREATE_USER.getRoleName()),
                new Role(null, UserManagementRoles.UPDATE_USER.getRoleName()),
                new Role(null, UserManagementRoles.DELETE_USER.getRoleName()),
                new Role(null, UserManagementRoles.VIEW_USER.getRoleName()),

                new Role(null, UserTypeRoles.CREATE_USER_TYPE.getRoleName()),
                new Role(null, UserTypeRoles.UPDATE_USER_TYPE.getRoleName()),
                new Role(null, UserTypeRoles.DELETE_USER_TYPE.getRoleName()),
                new Role(null, UserTypeRoles.VIEW_USER_TYPE.getRoleName())
        );

        // TODO (ajustar): O método isRestaurantOwner() compara com  "Dono de Restaurante"
        UserType type = new UserType(1L, "Dono de Restaurante", roles);

        return new User(
                DEV_OWNER_ID,
                "Dev Owner",
                "dev.owner@dev.local",
                null,
                type,
                PASSWORD_HASH
        );
    }
}
