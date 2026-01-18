package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.MenuItemRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.*;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.*;
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

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class MenuRestControllerTestIT {
    private static final String USER_TYPE_RESTAURANT_OWNER = "RESTAURANT_OWNER";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserTypeRepository userTypeRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @Autowired
    private MenuItemRepository menuRepo;

    @Autowired
    private OpeningHoursRepository openingHoursRepo;

    @MockitoBean
    private LoggedUserGateway loggedUserGateway;

    private RestaurantEntity restaurant;
    private Long restaurantId;
    private UUID ownerId;
    private Long baiaoDeDoisId;

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

        var monday = new OpeningHoursEntity();
        monday.setDayOfWeek(DayOfWeek.MONDAY);
        monday.setOpenHour(LocalTime.of(11, 0));
        monday.setCloseHour(LocalTime.of(15,0));
        monday.setRestaurant(restaurant);

        var tuesday = new OpeningHoursEntity();
        tuesday.setDayOfWeek(DayOfWeek.TUESDAY);
        tuesday.setOpenHour(LocalTime.of(11, 0));
        tuesday.setCloseHour(LocalTime.of(15,0));
        tuesday.setRestaurant(restaurant);

        openingHoursRepo.saveAll(List.of(monday, tuesday));

        var baiaoDeDois = new MenuItemEntity();
        baiaoDeDois.setName("Baião De Dois");
        baiaoDeDois.setDescription("Prato típico nordestino brasileiro, feito com arroz, feijão de corda, queijo coalho e carnes como carne seca e bacon");
        baiaoDeDois.setRestaurant(restaurant);
        baiaoDeDois.setPrice(new BigDecimal("50"));
        baiaoDeDois.setRestaurantOnly(false);
        baiaoDeDois.setPhotoPath("/fotos-menu/baiao-de-dois.jpg");
        baiaoDeDoisId = menuRepo.save(baiaoDeDois).getId();

        var strogonoff = new MenuItemEntity();
        strogonoff.setName("Strogonoff de Frango");
        strogonoff.setRestaurant(restaurant);
        strogonoff.setPrice(new BigDecimal("28"));
        strogonoff.setDescription("Cubos de peito de frango, envolvidos em um molho de creme de leite, toque de ketchup, mostarda Dijon e cogumelos fatiados (champignon). Servido com o tradicional arroz branco soltinho e a crocância indispensável da batata palha extrafina.");
        strogonoff.setRestaurantOnly(false);
        strogonoff.setPhotoPath("/foto-menu/strogonoff-frago.jpg");

        menuRepo.save(strogonoff);

        //internamente valida se é dono ou funcionario, tive que mockar aqui
        var roles = userType.getRoles().stream().map(r -> new Role(r.getId(), r.getName())).collect(Collectors.toSet());
        var loggedOwner = new User(ownerId, owner.getName(), owner.getEmail(), null, new UserType(userType.getId(), userType.getName(), roles), owner.getPasswordHash());
        given(loggedUserGateway.requireCurrentUser()).willReturn(loggedOwner);
        given(loggedUserGateway.hasRole(ArgumentMatchers.any(ForGettingRoleName.class))).willReturn(true);
    }

    @AfterEach
    void tearDown() {
        restaurantRepo.deleteById(restaurantId);
        userRepository.deleteById(ownerId);
    }

    @Test
    @DisplayName("Deve buscar por restaurante e retornar página de MenuItemOutput")
    void deveBuscaOsItensDoMenu() throws Exception {
        var pageNumber = 0;
        var pageSize = 10;
        var itensMenu = restaurant.getMenu().stream().map(MenuItemEntity::getName).toArray();
        mockMvc.perform(get("/restaurants/{id}/menu?pageNumber={pageNumber}&pageSize={pageSize}", restaurant.getId(), pageNumber, pageSize)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.pageNumber", is(equalTo(pageNumber))))
                .andExpect(jsonPath("$.pageSize", is(equalTo(pageSize))))
                .andExpect(jsonPath("$.totalElements", is(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content[*].id").exists())
                .andExpect(jsonPath("$.content[*].name", hasItems(itensMenu)));
    }

    @Test
    @WithMockUser(authorities = {"CREATE_MENU_ITEM"})
    @DisplayName("Deve adicionar novo item no menu")
    void deveAdicionarNovoItemAoMenu() throws Exception {
        var camaraoRequest = new MenuItemRequest();
        camaraoRequest.setName("Risoto de Camarão ao Limão Siciliano");
        camaraoRequest.setDescription("Arroz arbóreo cremoso com camarões grelhados no azeite de ervas, finalizado com raspas de limão siciliano, queijo parmesão e brotos frescos.");
        camaraoRequest.setPrice(new BigDecimal("98"));
        camaraoRequest.setRestaurantOnly(true);
        camaraoRequest.setPhotoPath("/fotos-menu/risoto-camarao.jpg");

        mockMvc.perform(post("/restaurants/{id}/menu", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(JsonUtil.parseToString(camaraoRequest)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/restaurants/"+restaurantId+"/menu/*"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(equalTo(camaraoRequest.getName()))))
                .andExpect(jsonPath("$.description", is(equalTo(camaraoRequest.getDescription()))))
                .andExpect(jsonPath("$.price", comparesEqualTo(camaraoRequest.getPrice().intValue())))
                .andExpect(jsonPath("$.restaurantOnly", is(equalTo(camaraoRequest.getRestaurantOnly()))))
                .andExpect(jsonPath("$.photoPath", is(equalTo(camaraoRequest.getPhotoPath()))));

    }

    @Test
    @WithMockUser(authorities = {"CREATE_MENU_ITEM"})
    @DisplayName("Deve retornar erro se o restaurante não existir")
    void deveRetornarErroSeRestaurantNaoExistir() throws Exception {
        var camaraoRequest = new MenuItemRequest();
        camaraoRequest.setName("Risoto de Camarão ao Limão Siciliano");
        camaraoRequest.setDescription("Arroz arbóreo cremoso com camarões grelhados no azeite de ervas, finalizado com raspas de limão siciliano, queijo parmesão e brotos frescos.");
        camaraoRequest.setPrice(new BigDecimal("98"));
        camaraoRequest.setRestaurantOnly(true);
        camaraoRequest.setPhotoPath("/fotos-menu/risoto-camarao.jpg");

        mockMvc.perform(post("/restaurants/{id}/menu", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.parseToString(camaraoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(equalTo("Restaurante não encontrado com ID: " + Long.MAX_VALUE))));
    }

    @Test
    @WithMockUser(authorities = {"CREATE_MENU_ITEM"})
    @DisplayName("Deve retornar erro se o restaurante o item já existe")
    void deveRetornarErroSeItemJaExiste() throws Exception {
        var camaraoRequest = new MenuItemRequest();
        camaraoRequest.setName("Baião De Dois");
        camaraoRequest.setDescription("Prato típico nordestino brasileiro, feito com arroz, feijão de corda, queijo coalho e carnes como carne seca e bacon");
        camaraoRequest.setPrice(new BigDecimal("98"));
        camaraoRequest.setRestaurantOnly(true);
        camaraoRequest.setPhotoPath("/fotos-menu/risoto-camarao.jpg");

        mockMvc.perform(post("/restaurants/{id}/menu", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.parseToString(camaraoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(equalTo("Já existe um item de cardápio com o nome '%s' no restaurante '%s'.".formatted(camaraoRequest.getName(), restaurant.getName())))));
    }

}