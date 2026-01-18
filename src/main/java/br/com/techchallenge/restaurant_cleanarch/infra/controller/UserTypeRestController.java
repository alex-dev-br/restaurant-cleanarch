package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.controller.UserTypeController;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper.UserTypeRestMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserTypeRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/user-types")
public class UserTypeRestController {

    private final UserTypeController userTypeController;
    private final UserTypeRestMapper userTypeRestMapper;

    public UserTypeRestController(UserTypeController userTypeController, UserTypeRestMapper userTypeRestMapper) {
        this.userTypeController = userTypeController;
        this.userTypeRestMapper = userTypeRestMapper;
    }

    @PostMapping
    public ResponseEntity<UserTypeResponse> createUsertype(@RequestBody @Valid UserTypeRequest userTypeRequest, UriComponentsBuilder uriComponentsBuilder) {
        var createUserTypeInput = userTypeRestMapper.toInput(userTypeRequest);
        var createUserTypeOutput = userTypeController.createUserType(createUserTypeInput);
        var uri = uriComponentsBuilder.path("/user-types/{id}").buildAndExpand(createUserTypeOutput.id()).toUri();
        return ResponseEntity.created(uri).body(userTypeRestMapper.toResponse(createUserTypeOutput));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTypeResponse> getUserTypeById(@PathVariable("id") Long id) {
        return userTypeController.getUserTypeById(id)
                .map(userTypeRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserTypeResponse> listUserTypes() {
        return userTypeController.listAllUserTypes().stream().map(userTypeRestMapper::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUserType(@PathVariable("id") Long id,
                                               @RequestBody @Valid UserTypeRequest userTypeRequest) {
        var updateUserTypeInput = userTypeRestMapper.toUpdateInput(userTypeRequest, id);
        userTypeController.updateUserType(updateUserTypeInput);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserType(@PathVariable("id") Long id) {
        userTypeController.deleteUserType(id);
        return ResponseEntity.noContent().build();
    }
}
