package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    @Size(min = 1, max = 255)
    private String street;
    @NotBlank
    private String number;
    @NotBlank
    private String city;
    @NotBlank
    private String state;
    @NotBlank
    private String zipCode;
    private String complement;
}
