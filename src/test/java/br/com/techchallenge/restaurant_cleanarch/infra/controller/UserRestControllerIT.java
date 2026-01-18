package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.AddressRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RoleEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    private UserEntity user;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        var userType = userTypeRepository.findById(1L).orElseThrow(() -> new RuntimeException("User type not found"));
        UserEntity userEntity = new UserBuilder().withoutId().buildEntity();
        userEntity.setUserType(userType);
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(equalTo("User type with ID " + userTypeId + " not found."))));
    }

    @Test
    @WithMockUser(authorities = {"UPDATE_USER"})
    @DisplayName("Deve alterar usuário com usuário")
    void deveAlterarUsuarioComSucesso() throws Exception {
        var userTypeId = 1L;
        var userRequest = new UserRequest();
        userRequest.setName("Maria Oliveira");
        userRequest.setEmail("marie.olv@mail.com");
        userRequest.setPassword("secret&Str0nG");
        userRequest.setUserTypeId(userTypeId);
        userRequest.setAddress(addressRequest);

        mockMvc.perform(put("/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.parseToString(userRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"UPDATE_USER"})
    @DisplayName("Deve retornar erro se o usuário sendo alterado não existir")
    void deveDarErroAoAlterarUsuarioSeEleNaoExistir() throws Exception {
        var userTypeId = 1L;
        var userRequest = new UserRequest();
        userRequest.setName("Maria Oliveira");
        userRequest.setEmail("marie.olv@mail.com");
        userRequest.setPassword("secret&Str0nG");
        userRequest.setUserTypeId(userTypeId);
        userRequest.setAddress(addressRequest);

        UUID randomUUID = UUID.randomUUID();
        mockMvc.perform(put("/user/{id}", randomUUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.parseToString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(equalTo("User with ID " + randomUUID + " not found."))));
    }

    @Test
    @WithMockUser(authorities = {"UPDATE_USER"})
    @DisplayName("Deve retornar erro ao tentar alterar o email para um em uso")
    void deveDarErroAoAlterarUsuarioParaEmailEmUso() throws Exception {
        String inUseEmail = "in.use@mail.com";
        UserEntity userEntity = new UserBuilder().withEmail(inUseEmail).withoutId().buildEntity();
        userRepository.save(userEntity);

        var userTypeId = 1L;

        var userRequest = new UserRequest();
        userRequest.setName("Maria Oliveira");
        userRequest.setEmail(inUseEmail);
        userRequest.setPassword("secret&Str0nG");
        userRequest.setUserTypeId(userTypeId);
        userRequest.setAddress(addressRequest);

        mockMvc.perform(put("/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.parseToString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(equalTo("Email " + inUseEmail + " is already in use."))));
    }

    @Test
    @WithMockUser(authorities = {"UPDATE_USER"})
    @DisplayName("Deve retornar erro ao tentar alterar o userType para um inválido")
    void deveRetornarErroAoAlterarUsuarioParaUserTypeInvalido() throws Exception {
        var userTypeId = Long.MAX_VALUE;
        var userRequest = new UserRequest();
        userRequest.setName("Maria Oliveira");
        userRequest.setEmail("marie.olv@mail.com");
        userRequest.setPassword("secret&Str0nG");
        userRequest.setUserTypeId(userTypeId);
        userRequest.setAddress(addressRequest);

        mockMvc.perform(put("/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(JsonUtil.parseToString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(equalTo("User type with ID " + userTypeId + " not found."))));
    }

    @Test
    @WithMockUser(authorities = {"DELETE_USER"})
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() throws Exception {
        mockMvc.perform(delete("/user/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"DELETE_USER"})
    @DisplayName("Deve retornar erro ao tentar deletar usuário inexistente")
    void deveDevolverErroAoDeletarUsuarioInexistente() throws Exception {
        UUID randomUUID = UUID.randomUUID();
        mockMvc.perform(delete("/user/{id}", randomUUID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", is(equalTo("User with ID " + randomUUID + " not found."))));
    }

    @Test
    @WithMockUser(authorities = {"VIEW_USER"})
    @DisplayName("Deve buscar usuário por id com sucesso")
    void deveBuscarPeloIdUsuarioComSucesso() throws Exception {
        mockMvc.perform(get("/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(equalTo(user.getId().toString()))))
                .andExpect(jsonPath("$.name", is(equalTo(user.getName()))))
                .andExpect(jsonPath("$.email", is(equalTo(user.getEmail()))))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.address.street", is(equalTo(user.getAddress().getStreet()))))
                .andExpect(jsonPath("$.address.number", is(equalTo(user.getAddress().getNumber()))))
                .andExpect(jsonPath("$.address.city", is(equalTo(user.getAddress().getCity()))))
                .andExpect(jsonPath("$.address.state", is(equalTo(user.getAddress().getState()))))
                .andExpect(jsonPath("$.address.zipCode", is(equalTo(user.getAddress().getZipCode()))))
                .andExpect(jsonPath("$.address.complement", is(equalTo(user.getAddress().getComplement()))))
                .andExpect(jsonPath("$.userType.id", is(equalTo(user.getUserType().getId().intValue()))))
                .andExpect(jsonPath("$.userType.name", is(equalTo(user.getUserType().getName()))))
                .andExpect(jsonPath("$.userType.roles").isArray())
                .andExpect(jsonPath("$.userType.roles", hasSize(user.getUserType().getRoles().size())))
                .andExpect(jsonPath("$.userType.roles", hasItems(user.getUserType().getRoles()
                        .stream().map(RoleEntity::getName).toArray())));

    }

    @Test
    @WithMockUser(authorities = {"VIEW_USER"})
    @DisplayName("Deve retornar notfound ao buscar usuário inexistente")
    void deveDevolverRetornarNotFoundAoBuscarUsuarioPorIdInexistente() throws Exception {
        UUID randomUUID = UUID.randomUUID();
        mockMvc.perform(get("/user/{id}", randomUUID).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"VIEW_USER"})
    @DisplayName("Deve buscar todos os usuarios com sucesso")
    void deveBuscarTodosUsuariosComSucesso() throws Exception {
        var pageNumber = 0;
        var pageSize = 10;
        mockMvc.perform(get("/user?pageNumber={pageNumber}&pageSize={pageSize}", pageNumber, pageSize)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber", is(equalTo(pageNumber))))
                .andExpect(jsonPath("$.pageSize", is(equalTo(pageSize))))
                .andExpect(jsonPath("$.totalElements", is(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(user.getId().toString())))
                .andExpect(jsonPath("$.content[*].name", hasItem(user.getName())))
                .andExpect(jsonPath("$.content[*].email", hasItem(user.getEmail())))
                .andExpect(jsonPath("$.content[*].password").doesNotExist());

    }
}
