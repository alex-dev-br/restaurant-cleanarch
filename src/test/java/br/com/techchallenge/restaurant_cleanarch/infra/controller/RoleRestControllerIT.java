package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class RoleRestControllerIT {

    private static final String ROLES_PACKAGE = "br.com.techchallenge.restaurant_cleanarch.core.domain.roles";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = {"VIEW_ROLE"})
    void shouldReturnAllRolesWhenGetRolesIsCalled() throws Exception {
        var allRoles = findAllRoleConstants();

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", containsInAnyOrder(allRoles)));
    }

    @Test
    @WithMockUser(roles = {"NO_VIEW_ROLE"})
    void shouldReturnForbiddenWhenGetRolesIsCalledWithoutRole() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(jsonPath("$.message", containsString("current user does not have permission")));
    }


    private String[] findAllRoleConstants() {
        var roles = new ArrayList<String>();
        Reflections reflections = new Reflections(ROLES_PACKAGE);
        Set<Class<? extends ForGettingRoleName>> roleClasses = reflections.getSubTypesOf(ForGettingRoleName.class);

        for (Class<? extends ForGettingRoleName> clazz : roleClasses) {
            if (clazz.isEnum()) {
                roles.addAll(Stream.of(clazz.getEnumConstants()).map(ForGettingRoleName::getRoleName).toList());
            }
        }
        return roles.toArray(new String[0]);
    }
}
