package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import br.com.techchallenge.restaurant_cleanarch.core.controller.UserTypeController;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.mapper.UserTypeRestMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.UserTypeRequest;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

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
}
