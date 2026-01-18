package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @NotBlank
    @Email
    private String email;

    @Valid
    private AddressRequest address;

    @NotNull
    @Positive
    private Long userTypeId;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
