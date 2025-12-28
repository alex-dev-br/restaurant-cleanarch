package br.com.techchallenge.restaurant_cleanarch.core.usecase.impl;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserTypeUseCaseImplTest {

    @Mock
    private UserTypeGateway gateway;

    @InjectMocks
    private CreateUserTypeUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar UserType válido e salvar via gateway")
    void deveCriarUserTypeValido() {
        // Arrange
        UserType input = UserType.builder().name("Dono de Restaurante").build();
        UserType saved = input.toBuilder().id(UUID.randomUUID()).build();
        when(gateway.save(any(UserType.class))).thenReturn(saved);

        // Act
        UserType result = useCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        verify(gateway).save(any(UserType.class));
    }

    @Test
    @DisplayName("Deve lançar exceção com nome inválido")
    void deveLancarExcecaoNomeInvalido() {
        // Arrange
        UserType invalid = UserType.builder().name("").build();

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(invalid))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do tipo de usuário é obrigatório.");
    }
}