package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserTypeRequest {

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotEmpty
    private List<String> roles;
}
