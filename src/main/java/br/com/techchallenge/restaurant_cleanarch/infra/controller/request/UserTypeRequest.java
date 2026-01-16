package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class UserTypeRequest {
    @NotEmpty
    @Length(min = 3, max = 100)
    private String name;

    @NotEmpty
    private List<String> roles;
}
