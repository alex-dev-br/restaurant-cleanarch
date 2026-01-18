package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.controller.UserController;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper.UserRestMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserRestController {

    private final UserController userController;
    private final UserRestMapper userRestMapper;

    public UserRestController(UserController userController, UserRestMapper userRestMapper) {
        this.userController = userController;
        this.userRestMapper = userRestMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest, UriComponentsBuilder uriComponentsBuilder) {
        var createUserInput = userRestMapper.toInput(userRequest);
        var createUserOutput = userController.create(createUserInput);
        var uri = uriComponentsBuilder.path("/user/{id}").buildAndExpand(createUserOutput.id()).toUri();
        return ResponseEntity.created(uri).body(userRestMapper.toResponse(createUserOutput));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable("id") UUID id, @RequestBody @Valid UserRequest userRequest) {
        var updateUserInput = userRestMapper.toUpdateInput(userRequest, id);
        userController.update(updateUserInput);
        return ResponseEntity.noContent().build();
    }
}
