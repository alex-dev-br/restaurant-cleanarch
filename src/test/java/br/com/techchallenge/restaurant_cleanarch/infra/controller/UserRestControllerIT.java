package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.AddressRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    private UserEntity user;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserBuilder().withoutId().buildEntity();
        user = userRepository.save(userEntity);

        var addressBuilder = new AddressBuilder();
        addressRequest = addressBuilder.buildRequest();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteById(user.getId());
    }

    @Test
    @WithMockUser(authorities = {"CREATE_USER"})
    @DisplayName("Deve criar um novo usuário com sucesso")
    void shouldCreateUserWithSuccess() throws Exception {
        var userRequest = new UserRequest();
        userRequest.setName("Maria Oliveira");
        userRequest.setEmail("maria@teste.com");
        userRequest.setPassword("secret&Str0nG");
        userRequest.setUserTypeId(1L);
        userRequest.setAddress(addressRequest);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(JsonUtil.parseToString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/user/*"))
                .andExpect(jsonPath("$.id", is(matchesPattern("[a-fA-F0-9\\-]{36}"))))
                .andExpect(jsonPath("$.name", is(equalTo(userRequest.getName()))))
                .andExpect(jsonPath("$.email", is(equalTo(userRequest.getEmail()))))
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.address.street", is(equalTo(addressRequest.getStreet()))))
                .andExpect(jsonPath("$.address.number", is(equalTo(addressRequest.getNumber()))))
                .andExpect(jsonPath("$.address.city", is(equalTo(addressRequest.getCity()))))
                .andExpect(jsonPath("$.address.state", is(equalTo(addressRequest.getState()))))
                .andExpect(jsonPath("$.address.zipCode", is(equalTo(addressRequest.getZipCode()))))
                .andExpect(jsonPath("$.address.complement", is(equalTo(addressRequest.getComplement()))))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"CREATE_USER"})
    @DisplayName("Deve devolver erro se o email já estiver em uso")
    void deveDevolverErroSeEmailEstiverEmUso() throws Exception {
        var userRequest = new UserRequest();
        userRequest.setName("Maria Oliveira");
        userRequest.setEmail(user.getEmail());
        userRequest.setPassword("secret&Str0nG");
        userRequest.setUserTypeId(1L);
        userRequest.setAddress(addressRequest);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.parseToString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(equalTo("Email is already in use."))));
    }

    @Test
    @WithMockUser(authorities = {"CREATE_USER"})
    @DisplayName("Deve devolver erro se o user type for inválido")
    void deveDevolverErroSeUserTypeForInvalido() throws Exception {
        var userTypeId = Long.MAX_VALUE;
        var userRequest = new UserRequest();
        userRequest.setName("Maria Oliveira");
        userRequest.setEmail("marie.olv@mail.com");
        userRequest.setPassword("secret&Str0nG");
        userRequest.setUserTypeId(userTypeId);
        userRequest.setAddress(addressRequest);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.parseToString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(equalTo("User type with ID " + userTypeId + " not found."))));
    }
}