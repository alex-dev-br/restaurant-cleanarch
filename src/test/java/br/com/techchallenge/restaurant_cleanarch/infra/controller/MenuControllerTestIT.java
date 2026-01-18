package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.*;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class MenuControllerTestIT {
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

    private RestaurantEntity restaurant;

    @BeforeEach
    void setUp() {
        var userType = userTypeRepo.findByName(USER_TYPE_RESTAURANT_OWNER).orElseThrow(() -> new RuntimeException(USER_TYPE_RESTAURANT_OWNER + " not found"));
        var ownerEntity = new UserEntity();
        ownerEntity.setName("Owner");
        ownerEntity.setPasswordHash("S3cr&tP@55");
        ownerEntity.setEmail("ownerEntity@mail.com.br");
        ownerEntity.setUserType(userType);
        UserEntity owner = userRepository.save(ownerEntity);

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
        newRestaurant.setMenu(Set.of());

        restaurant = restaurantRepo.save(newRestaurant);

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
        baiaoDeDois.setRestaurant(restaurant);
        baiaoDeDois.setPrice(new BigDecimal("50"));
        baiaoDeDois.setDescription("Prato típico nordestino brasileiro, feito com arroz, feijão de corda, queijo coalho e carnes como carne seca e bacon");
        baiaoDeDois.setRestaurantOnly(false);
        baiaoDeDois.setPhotoPath("/fotos-menu/baiao-de-dois.jpg");
        menuRepo.save(baiaoDeDois);

        var strogonoff = new MenuItemEntity();
        strogonoff.setName("Strogonoff de Frango");
        strogonoff.setRestaurant(restaurant);
        strogonoff.setPrice(new BigDecimal("28"));
        strogonoff.setDescription("Cubos de peito de frango, envolvidos em um molho de creme de leite, toque de ketchup, mostarda Dijon e cogumelos fatiados (champignon). Servido com o tradicional arroz branco soltinho e a crocância indispensável da batata palha extrafina.");
        strogonoff.setRestaurantOnly(false);
        strogonoff.setPhotoPath("/foto-menu/strogonoff-frago.jpg");

        menuRepo.save(strogonoff);
    }

    @Test
    @DisplayName("Deve buscar por restaurante e retornar página de MenuItemOutput")
    void deveBuscaOsItensDoMenu() throws Exception {
        var pageNumber = 0;
        var pageSize = 10;
        Long restaurantId = restaurant.getId();
        var itensMenu = restaurant.getMenu().stream().map(MenuItemEntity::getName).toArray();
        mockMvc.perform(get("/restaurants/{id}/menu?pageNumber={pageNumber}&pageSize={pageSize}", restaurantId, pageNumber, pageSize)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.pageNumber", is(equalTo(pageNumber))))
                .andExpect(jsonPath("$.pageSize", is(equalTo(pageSize))))
                .andExpect(jsonPath("$.totalElements", is(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content[*].name", hasItems(itensMenu)));
    }
}