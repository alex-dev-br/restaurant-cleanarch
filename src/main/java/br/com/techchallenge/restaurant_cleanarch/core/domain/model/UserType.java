package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UserType {
    private Long id;
    private String name;   // "Dono de Restaurante", "Cliente"

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Nome do tipo de usuário é obrigatório.");
        }
    }

}
