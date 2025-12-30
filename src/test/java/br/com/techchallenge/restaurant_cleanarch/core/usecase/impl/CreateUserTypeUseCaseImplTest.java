package br.com.techchallenge.restaurant_cleanarch.core.usecase.impl;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Deve criar UserType válido e salvar via gateway")
    void deveCriarUserTypeValido() {
        // Arrange
        UserType input = new UserType(null, "Dono de Restaurante");
        UserType saved = new UserType(1L, "Dono de Restaurante");;
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
//        UserType invalid = UserType.builder().name("").build();

        // Act & Assert
        assertThatThrownBy(() -> new UserType(null, " "))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Name cannot be blank.");
    }
}