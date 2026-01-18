package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserTypeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class UserTypeRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = {"CREATE_USER_TYPE"})
    @DisplayName("Deve criar um novo tipo de usuário com sucesso")
    void shouldCreateUserTypeWithSuccess() throws Exception {
        var userTypeRequest = new UserTypeRequest();
        userTypeRequest.setName("EMPLOYEE");
        List<String> rolesRequest = List.of (
            MenuItemRoles.CREATE_MENU_ITEM.getRoleName(), MenuItemRoles.UPDATE_MENU_ITEM.getRoleName(), MenuItemRoles.VIEW_MENU_ITEM.getRoleName()
        );
        userTypeRequest.setRoles(rolesRequest);

        mockMvc.perform(post("/user-types")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonUtil.parseToString(userTypeRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(redirectedUrlPattern("**/user-types/{[0-9]+}"))
                .andExpect(jsonPath("$.name", is(equalTo(userTypeRequest.getName()))))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles", hasSize(3)))
                .andExpect(jsonPath("$.roles", containsInAnyOrder(rolesRequest.toArray())));

    }

    @Test
    @WithMockUser(authorities = {"CREATE_USER_TYPE"})
    @DisplayName("Deve criar lançar erro ao tentar criar sem nome")
    void shouldReturnBadRequestWhenCreateUserTypeWithoutName() throws Exception {
        var userTypeRequest = new UserTypeRequest();
        List<String> rolesRequest = List.of (
            MenuItemRoles.CREATE_MENU_ITEM.getRoleName(), MenuItemRoles.UPDATE_MENU_ITEM.getRoleName(), MenuItemRoles.VIEW_MENU_ITEM.getRoleName()
        );
        userTypeRequest.setRoles(rolesRequest);

        mockMvc.perform(post("/user-types")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtil.parseToString(userTypeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].field", is(equalTo("name"))))
                .andExpect(jsonPath("$[0].message", is(equalTo("must not be blank"))));
    }

    @Test
    @WithMockUser(authorities = {"CREATE_USER_TYPE"})
    @DisplayName("Deve criar lançar erro ao tentar criar roles")
    void shouldReturnBadRequestWhenCreateUserTypeWithoutRoles() throws Exception {
        var userTypeRequest = new UserTypeRequest();
        userTypeRequest.setName("EMPLOYEE");

        mockMvc.perform(post("/user-types")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtil.parseToString(userTypeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].field", is(equalTo("roles"))))
                .andExpect(jsonPath("$[0].message", is(equalTo("must not be empty"))));
    }

    @Test
    @WithMockUser(authorities = {"CREATE_USER_TYPE"})
    @DisplayName("Deve criar lançar erro ao tentar criar role invalida")
    void shouldReturnBadRequestWhenCreateUserTypeWithInvalidRoleName() throws Exception {
        var userTypeRequest = new UserTypeRequest();
        List<String> rolesRequest = List.of ("ALL");
        userTypeRequest.setRoles(rolesRequest);
        userTypeRequest.setName("EMPLOYEE");

        mockMvc.perform(post("/user-types")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtil.parseToString(userTypeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", is(equalTo("User type must have at least one role valid."))));
    }

    @Test
    @WithMockUser(roles = {"VIEW_USER_TYPE"})
    @DisplayName("Deve retorna forbidden quando tentar criar um novo tipo de usuário sem permissão")
    void shouldReturnForbiddenWhenCreateUserTypeWithoutPermission() throws Exception {
        var userTypeRequest = new UserTypeRequest();
        userTypeRequest.setName("EMPLOYEE");
        List<String> rolesRequest = List.of (
                MenuItemRoles.CREATE_MENU_ITEM.getRoleName(), MenuItemRoles.UPDATE_MENU_ITEM.getRoleName(), MenuItemRoles.VIEW_MENU_ITEM.getRoleName()
        );
        userTypeRequest.setRoles(rolesRequest);
        mockMvc.perform(post("/user-types")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonUtil.parseToString(userTypeRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(jsonPath("$.message", containsString("current user does not have permission")));
    }
}