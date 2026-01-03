package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes para Restaurant")
class RestaurantTest {

    @Test
    @DisplayName("Deve criar Restaurant válido com campos preenchidos")
    void deveCriarRestaurantValido() {
        // Arrange
        User owner = criarDonoValido();

        var builder = new RestaurantBuilder()
                .withName("Restaurante Teste")
                .withAddress(new Address("Rua Teste", "123", "Cidade", "SP", "00000-000", null))
                .withCuisineType("Italiana")
                .withOwner(owner);

        // Act
        Restaurant restaurant = builder.build();

        // Assert
        assertThat(restaurant).isNotNull();
        assertThat(restaurant.getName()).isEqualTo("Restaurante Teste");
        assertThat(restaurant.getCuisineType()).isEqualTo("Italiana");
        assertThat(restaurant.getOwner()).isEqualTo(owner);
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono válido")
    void deveLancarExcecaoSemDonoValido() {
        // Arrange
        UserType clienteType = new UserType(null, "Cliente", Set.of(new Role(null, "ADMIN")));
        User invalidOwner = new UserBuilder().withUserType(clienteType).build();

        var builder = new RestaurantBuilder().withOwner(invalidOwner);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O restaurante deve ter um dono válido do tipo 'Dono de Restaurante'.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem dono")
    void deveLancarExcecaoSemDono() {
        // Arrange
        var builder = new RestaurantBuilder().withOwner(null);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O dono do restaurante não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando name for nulo")
    void deveLancarExcecaoQuandoNameForNulo() {
        // Arrange
        User owner = criarDonoValido();

        var builder = new RestaurantBuilder()
                .withName(null)
                .withOwner(owner);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O nome do restaurante não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando name for blank")
    void deveLancarExcecaoQuandoNameForBlank() {
        // Arrange
        User owner = criarDonoValido();

        var builder = new RestaurantBuilder()
                .withName("   ")
                .withOwner(owner);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O nome do restaurante não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando address for nulo")
    void deveLancarExcecaoQuandoAddressForNulo() {
        // Arrange
        User owner = criarDonoValido();

        var builder = new RestaurantBuilder()
                .withAddress(null)
                .withOwner(owner);

        // Act & Assert
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O endereço não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando cuisineType for nulo ou blank")
    void deveLancarExcecaoQuandoCuisineTypeInvalido() {
        // Arrange - caso nulo
        User owner = criarDonoValido();
        var builderNulo = new RestaurantBuilder()
                .withCuisineType(null)
                .withOwner(owner);

        // Act & Assert - caso nulo
        assertThatThrownBy(builderNulo::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("O tipo de cozinha não pode ser nulo.");

        // Arrange - caso blank
        var builderBlank = new RestaurantBuilder()
                .withCuisineType("   ")
                .withOwner(owner);

        // Act & Assert - caso blank
        assertThatThrownBy(builderBlank::build)
                .isInstanceOf(BusinessException.class)
                .hasMessage("O tipo de cozinha não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve permitir coleções nulas ou vazias")
    void devePermitirColecoesNulasOuVazias() {
        // Arrange
        User owner = criarDonoValido();
        var builder = new RestaurantBuilder()
                .withName("Teste")
                .withAddress(new Address("Rua Teste", "123", "Cidade", "SP", "00000-000", null))
                .withCuisineType("Italiana")
                .withOpeningHours(null)
                .withMenu(Set.of())
                .withOwner(owner);

        // Act
        Restaurant restaurant = builder.build();

        // Assert
        assertThat(restaurant.getOpeningHours()).isNull();
        assertThat(restaurant.getMenu()).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar true para equals com mesmo objeto")
    void deveSerIgualParaMesmoObjeto() {
        // Arrange
        Restaurant restaurant = criarRestaurantValido();

        // Act & Assert
        assertThat(restaurant.equals(restaurant)).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false para equals com objeto de outra classe")
    void deveSerDiferenteParaOutraClasse() {
        // Arrange
        Restaurant restaurant = criarRestaurantValido();

        // Act & Assert
        assertThat(restaurant.equals("Outra coisa")).isFalse();
        assertThat(restaurant.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Deve retornar true para equals quando ids são iguais")
    void deveSerIgualQuandoIdsIguais() {
        // Arrange
        User owner = criarDonoValido();
        Restaurant r1 = new RestaurantBuilder().withId(1L).withOwner(owner).build();
        Restaurant r2 = new RestaurantBuilder().withId(1L).withOwner(owner).build();

        // Act & Assert
        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false para equals quando ids diferentes ou mistos")
    void deveSerDiferenteQuandoIdsDiferentesOuMistos() {
        // Arrange
        User owner = criarDonoValido();
        Restaurant r1 = new RestaurantBuilder().withId(1L).withOwner(owner).build();
        Restaurant r2 = new RestaurantBuilder().withId(null).withOwner(owner).build();
        Restaurant r3 = new RestaurantBuilder().withId(2L).withOwner(owner).build();

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2);
        assertThat(r1).isNotEqualTo(r3);
        assertThat(r2).isNotEqualTo(r3);
    }

    @Test
    @DisplayName("Deve respeitar contrato equals com id nulo")
    void deveRespeitarContratoComIdNulo() {
        // Arrange
        User owner = criarDonoValido();
        Restaurant r1 = new RestaurantBuilder().withId(null).withOwner(owner).build();
        Restaurant r2 = new RestaurantBuilder().withId(null).withOwner(owner).build();

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2); // diferentes instâncias
        assertThat(r1.equals(r1)).isTrue(); // reflexividade
    }

    @Test
    @DisplayName("Deve considerar diferentes quando um tem id nulo e outro não")
    void deveSerDiferenteQuandoUmIdNuloEOutroNao() {
        // Arrange
        User owner = criarDonoValido();

        Restaurant restaurantWithId = new RestaurantBuilder()
                .withId(1L)
                .withName("Restaurante com ID")
                .withAddress(new Address("Rua Teste", "123", "Cidade", "SP", "00000-000", null))
                .withCuisineType("Italiana")
                .withOwner(owner)
                .build();

        Restaurant restaurantWithoutId = new RestaurantBuilder()
                .withId(null)
                .withName("Restaurante sem ID")
                .withAddress(new Address("Rua Teste", "123", "Cidade", "SP", "00000-000", null))
                .withCuisineType("Italiana")
                .withOwner(owner)
                .build();

        // Act & Assert
        assertThat(restaurantWithId).isNotEqualTo(restaurantWithoutId);
        assertThat(restaurantWithoutId).isNotEqualTo(restaurantWithId);

        // hashCode deve ser diferente (um usa id, outro usa 0)
        assertThat(restaurantWithId.hashCode()).isNotEqualTo(restaurantWithoutId.hashCode());
    }

    private User criarDonoValido() {
        UserType ownerType = new UserType(null, "Dono de Restaurante", Set.of(new Role(null, "ADMIN")));
        return new UserBuilder().withUserType(ownerType).build();
    }

    private Restaurant criarRestaurantValido() {
        User owner = criarDonoValido();
        return new RestaurantBuilder().withName("Teste").withAddress(new Address("Rua Teste", "123", "Cidade", "SP", "00000-000", null)).withCuisineType("Italiana").withOwner(owner).build();
    }
}