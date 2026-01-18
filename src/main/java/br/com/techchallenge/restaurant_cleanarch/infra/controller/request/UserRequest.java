package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserRequest {
    @NotBlank
    @Length(min = 3, max = 255)
    private String name;

    @NotBlank
    @Email
    private String email;

    private AddressRequest address;

    @NotNull
    @Positive
    private Long userTypeId;

    @NotBlank
    @Length(min = 8, max = 100)
    private String password;
}
