package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.AddressRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.OpeningHoursRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.RestaurantRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.AddressEmbeddableEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.RestaurantRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class RestaurantRestControllerTestIT {

    private static final String USER_TYPE_RESTAURANT_OWNER = "RESTAURANT_OWNER";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserTypeRepository userTypeRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @MockitoBean
    private LoggedUserGateway loggedUserGateway;

    private RestaurantEntity restaurant;
    private Long restaurantId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        var userType = userTypeRepo.findByName(USER_TYPE_RESTAURANT_OWNER).orElseThrow(() -> new RuntimeException(USER_TYPE_RESTAURANT_OWNER + " not found"));
        var ownerEntity = new UserEntity();
        ownerEntity.setName("Owner");
        ownerEntity.setPasswordHash("S3cr&tP@55");
        ownerEntity.setEmail("ownerEntity@mail.com.br");
        ownerEntity.setUserType(userType);
        UserEntity owner = userRepository.save(ownerEntity);
        ownerId = owner.getId();

        var addressEntity = new AddressEmbeddableEntity();
        addressEntity.setStreet("Rua Ipanema");
        addressEntity.setNumber("1025");
        addressEntity.setCity("Rio grande do Leste");
        addressEntity.setState("RL");
        addressEntity.setZipCode("00000-000");
        addressEntity.setComplement("N/A");

        var newRestaurant = new RestaurantEntity();
        newRestaurant.setName("Owner's");
        newRestaurant.setCuisineType("Tradicional");
        newRestaurant.setAddress(addressEntity);
        newRestaurant.setOwner(owner);

        restaurant = restaurantRepo.save(newRestaurant);
        restaurantId = restaurant.getId();

        var roles = userType.getRoles().stream().map(r -> new Role(r.getId(), r.getName())).collect(Collectors.toSet());
        var loggedOwner = new User(ownerId, owner.getName(), owner.getEmail(), null, new UserType(userType.getId(), userType.getName(), roles), owner.getPasswordHash());
        given(loggedUserGateway.requireCurrentUser()).willReturn(loggedOwner);
        given(loggedUserGateway.hasRole(ArgumentMatchers.any(ForGettingRoleName.class))).willReturn(true);
    }

    @AfterEach
    void tearDown() {
        restaurantRepo.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = {"CREATE_RESTAURANT"})
    @DisplayName("Deve criar restaurante com sucesso")
    void shouldCreateRestaurantSuccessfully() throws Exception {
        var request = new RestaurantRequest();
        request.setName("New Restaurant");
        request.setCuisineType("Italian");
        var address = new AddressRequest();
        address.setStreet("Street");
        address.setNumber("123");
        address.setCity("City");
        address.setState("ST");
        address.setZipCode("12345-678");
        request.setAddress(address);
        request.setOwnerId(ownerId);
        request.setOpeningHours(Collections.singletonList(new OpeningHoursRequest(DayOfWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(23, 0))));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.parseToString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("New Restaurant")))
                .andExpect(jsonPath("$.cuisineType", is("Italian")));
    }

    @Test
    @DisplayName("Deve buscar restaurante por id publico")
    void shouldGetPublicRestaurantById() throws Exception {
        mockMvc.perform(get("/restaurants/{id}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(restaurantId.intValue())))
                .andExpect(jsonPath("$.name", is(restaurant.getName())))
                .andExpect(jsonPath("$.cuisineType", is(restaurant.getCuisineType())));
    }
    
    @Test
    @WithMockUser(authorities = {"VIEW_RESTAURANT"})
    @DisplayName("Deve buscar restaurante por id para gestao")
    void shouldGetManagementRestaurantById() throws Exception {
        mockMvc.perform(get("/restaurants/{id}/management", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(restaurantId.intValue())))
                .andExpect(jsonPath("$.name", is(restaurant.getName())))
                .andExpect(jsonPath("$.cuisineType", is(restaurant.getCuisineType())))
                .andExpect(jsonPath("$.owner.id", is(ownerId.toString())));
    }

    @Test
    @DisplayName("Deve listar restaurantes paginados")
    void shouldListRestaurantsPaged() throws Exception {
        mockMvc.perform(get("/restaurants")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(restaurant.getName())));
    }

    @Test
    @DisplayName("Deve listar restaurantes por tipo de cozinha")
    void shouldListRestaurantsByCuisineType() throws Exception {
        mockMvc.perform(get("/restaurants")
                        .param("cuisineType", "Tradicional")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(restaurant.getName())));
    }

    @Test
    @WithMockUser(authorities = {"UPDATE_RESTAURANT"})
    @DisplayName("Deve atualizar restaurante com sucesso")
    void shouldUpdateRestaurantSuccessfully() throws Exception {
        var request = new RestaurantRequest();
        request.setName("Updated Restaurant");
        request.setCuisineType("Japanese");
        var address = new AddressRequest();
        address.setStreet("New Street");
        address.setNumber("456");
        address.setCity("New City");
        address.setState("NS");
        address.setZipCode("98765-432");
        request.setAddress(address);
        request.setOwnerId(ownerId);
        request.setOpeningHours(Collections.singletonList(new OpeningHoursRequest(DayOfWeek.TUESDAY, LocalTime.of(19, 0), LocalTime.of(23, 30))));

        mockMvc.perform(put("/restaurants/{id}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.parseToString(request)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/restaurants/{id}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Restaurant")))
                .andExpect(jsonPath("$.cuisineType", is("Japanese")));
    }

    @Test
    @WithMockUser(authorities = {"DELETE_RESTAURANT"})
    @DisplayName("Deve deletar restaurante com sucesso")
    void shouldDeleteRestaurantSuccessfully() throws Exception {
        mockMvc.perform(delete("/restaurants/{id}", restaurantId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/restaurants/{id}", restaurantId))
                .andExpect(status().isNotFound());
    }
}
