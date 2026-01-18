package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserTypeRequest;
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
}