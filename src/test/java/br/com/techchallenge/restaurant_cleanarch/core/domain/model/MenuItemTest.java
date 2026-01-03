package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes para MenuItem")
class MenuItemTest {

    @Test
    @DisplayName("Deve criar MenuItem válido sem lançar exceção")
    void deveCriarMenuItemValido() {
        // Arrange
        var builder = new MenuItemBuilder()
                .withName("Pizza Margherita")
                .withDescription("Pizza clássica")
                .withPrice(new BigDecimal("30"))
                .withRestaurantOnly(false)
                .withPhotoPath("/photos/pizza.jpg");

        // Act
        MenuItem item = builder.build();

        // Assert
        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("Pizza Margherita");
        assertThat(item.getDescription()).isEqualTo("Pizza clássica");
        assertThat(item.getPrice()).isEqualTo(new BigDecimal("30"));
        assertThat(item.getRestaurantOnly()).isFalse();
        assertThat(item.getPhotoPath()).isEqualTo("/photos/pizza.jpg");
    }

    @Test
    @DisplayName("Deve lançar exceção quando name for nulo")
    void deveLancarExcecaoQuandoNameForNulo() {
        // Arrange
        var builder = new MenuItemBuilder().withName(null);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O nome do item não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando name for blank")
    void deveLancarExcecaoQuandoNameForBlank() {
        // Arrange
        var builder = new MenuItemBuilder().withName("   ");

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O nome do item não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando price for nulo")
    void deveLancarExcecaoQuandoPriceForNulo() {
        // Arrange
        var builder = new MenuItemBuilder().withPrice(null);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O preço não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando price for zero ou negativo")
    void deveLancarExcecaoQuandoPriceInvalido() {
        // Arrange
        var builderZero = new MenuItemBuilder().withPrice(BigDecimal.ZERO);

        // Act & Assert - zero
        assertThatThrownBy(builderZero::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O preço deve ser maior que zero.");

        // Arrange - negativo
        var builderNegativo = new MenuItemBuilder().withPrice(new BigDecimal("-10"));

        // Act & Assert - negativo
        assertThatThrownBy(builderNegativo::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O preço deve ser maior que zero.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando restaurantOnly for nulo")
    void deveLancarExcecaoQuandoRestaurantOnlyForNulo() {
        // Arrange
        var builder = new MenuItemBuilder().withRestaurantOnly(null);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("A disponibilidade para restaurante apenas não pode ser nula.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando photoPath for nulo ou blank")
    void deveLancarExcecaoQuandoPhotoPathInvalido() {
        // Arrange - nulo
        var builderNulo = new MenuItemBuilder().withPhotoPath(null);

        // Act & Assert - nulo
        assertThatThrownBy(builderNulo::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O caminho da foto não pode ser nulo.");

        // Arrange - blank
        var builderBlank = new MenuItemBuilder().withPhotoPath("   ");

        // Act & Assert - blank
        assertThatThrownBy(builderBlank::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O caminho da foto não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve permitir description nula")
    void devePermitirDescriptionNula() {
        // Arrange
        var builder = new MenuItemBuilder()
                .withDescription(null);  // explicitamente null

        // Act
        MenuItem item = builder.build();

        // Assert
        assertThat(item.getDescription()).isNull();
    }

    @Test
    @DisplayName("Deve retornar true para equals com mesmo objeto")
    void deveSerIgualParaMesmoObjeto() {
        // Arrange
        MenuItem item = new MenuItemBuilder().build();

        // Act & Assert
        assertThat(item.equals(item)).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false para equals com objeto de outra classe")
    void deveSerDiferenteParaOutraClasse() {
        // Arrange
        MenuItem item = new MenuItemBuilder().build();

        // Act & Assert
        assertThat(item.equals("Outra coisa")).isFalse();
        assertThat(item.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Deve retornar true para equals quando ids são iguais")
    void deveSerIgualQuandoIdsIguais() {
        // Arrange
        MenuItem item1 = new MenuItemBuilder().withId(1L).build();
        MenuItem item2 = new MenuItemBuilder().withId(1L).build();

        // Act & Assert
        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false para equals quando ids diferentes ou mistos")
    void deveSerDiferenteQuandoIdsDiferentesOuMistos() {
        // Arrange
        MenuItem item1 = new MenuItemBuilder().withId(1L).build();
        MenuItem item2 = new MenuItemBuilder().withId(null).build();
        MenuItem item3 = new MenuItemBuilder().withId(2L).build();

        // Act & Assert
        assertThat(item1).isNotEqualTo(item2);
        assertThat(item1).isNotEqualTo(item3);
        assertThat(item2).isNotEqualTo(item3);
    }

    @Test
    @DisplayName("Deve respeitar contrato equals com id nulo")
    void deveRespeitarContratoComIdNulo() {
        // Arrange
        MenuItem item1 = new MenuItemBuilder().withId(null).build();
        MenuItem item2 = new MenuItemBuilder().withId(null).build();

        // Act & Assert
        assertThat(item1).isNotEqualTo(item2); // diferentes instâncias
        assertThat(item1.equals(item1)).isTrue(); // reflexividade
    }

    @Test
    @DisplayName("Deve considerar diferentes quando um tem id nulo e outro não")
    void deveSerDiferenteQuandoUmIdNuloEOutroNao() {
        // Arrange
        MenuItem itemWithId = new MenuItemBuilder().withId(1L).build();
        MenuItem itemWithoutId = new MenuItemBuilder().withId(null).build();

        // Act & Assert
        assertThat(itemWithId).isNotEqualTo(itemWithoutId);
        assertThat(itemWithoutId).isNotEqualTo(itemWithId);

        assertThat(itemWithId.hashCode()).isNotEqualTo(itemWithoutId.hashCode());
    }
}