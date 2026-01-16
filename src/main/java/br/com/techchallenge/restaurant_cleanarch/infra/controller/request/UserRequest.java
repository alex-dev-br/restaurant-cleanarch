package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserRequest {
    @NotEmpty
    @Length(min = 3, max = 255)
    private String name;

    @NotEmpty
    @Email
    private String email;

    private AddressRequest address;

    @NotNull
    @Positive
    private Long userTypeId;

    @NotEmpty
    @Length(min = 8, max = 100)
    private String password;
}
