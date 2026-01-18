package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserTypeRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserTypeEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RoleRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class UserTypeRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long savedManagerId;
    private Set<String> managerRolesName;

    @BeforeEach
    void setUp() {
        managerRolesName = Set.of("VIEW_USER_TYPE", "CREATE_USER_TYPE");
        var roles = roleRepository.findByNameIn(managerRolesName);
        var manager = new UserTypeEntity();
        manager.setName("MANAGER");
        manager.setRoles(new HashSet<>(roles));
        var savedManager = userTypeRepository.save(manager);
        savedManagerId = savedManager.getId();
    }

    @AfterEach
    void tearDown() {
        userTypeRepository.deleteById(savedManagerId);
    }

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
    @DisplayName("Deve criar lançar erro ao tentar criar UserType existente")
    void shouldReturnBadRequestWhenCreateUserTypeWithNameIsUse() throws Exception {
        var userTypeRequest = new UserTypeRequest();
        List<String> rolesRequest = List.of (
            MenuItemRoles.CREATE_MENU_ITEM.getRoleName(), MenuItemRoles.UPDATE_MENU_ITEM.getRoleName(), MenuItemRoles.VIEW_MENU_ITEM.getRoleName()
        );
        userTypeRequest.setRoles(rolesRequest);
        userTypeRequest.setName("RESTAURANT_OWNER");

        mockMvc.perform(post("/user-types")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtil.parseToString(userTypeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", is(equalTo("User type name is already in use."))));
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
    @WithMockUser(authorities = {"VIEW_USER_TYPE"})
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

    @Test
    @WithMockUser(authorities = {"VIEW_USER_TYPE"})
    @DisplayName("Deve buscar tipo de usuário por id")
    void shouldFindUserTypeById() throws Exception {
        mockMvc.perform(get("/user-types/{id}", savedManagerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedManagerId))
                .andExpect(jsonPath("$.name").value("MANAGER"))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles", containsInAnyOrder(managerRolesName.toArray())));
    }

    @Test
    @WithMockUser(authorities = {"VIEW_USER_TYPE"})
    @DisplayName("Deve retornar 404 ao buscar ID inexistente")
    void shouldReturn404WhenIdNotFound() throws Exception {
        Long idInexistente = Long.MAX_VALUE;

        mockMvc.perform(get("/user-types/{id}", idInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"VIEW_USER_TYPE"})
    @DisplayName("Deve buscar todos os tipos de usuários")
    void shouldListAllUserTyped() throws Exception {
        mockMvc.perform(get("/user-types").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[*].name", hasItems("RESTAURANT_OWNER", "CUSTOMER", "MANAGER"))) // flyway cria tipos padrão
                .andExpect(jsonPath("$[?(@.name == 'MANAGER')].roles[*]", containsInAnyOrder(managerRolesName.toArray())));
    }
}