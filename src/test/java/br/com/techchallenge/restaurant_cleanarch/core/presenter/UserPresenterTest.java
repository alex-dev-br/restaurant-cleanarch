package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserSummaryOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para UserPresenter")
class UserPresenterTest {

    private Address address;
    private UserType userType;
    private Long userTypeId;

    @BeforeEach
    void setUp() {
        String street = "123 Main St";
        String city = "Anytown";
        String state = "CA";
        String zipCode = "12345";
        String country = "USA";
        String complement = "Apt 4B";
        address = new Address(street, city, state, zipCode, country, complement);

        userTypeId = 1L;
        String userTypeName = "Administrator";
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(2L, "USER");
        Set<Role> roles = Set.of(role1, role2);
        userType = new UserType(userTypeId, userTypeName, roles);
    }

    @Test
    @DisplayName("Deve retornar nulo ao converter usuário nulo para UserOutput")
    void shouldReturnNullWhenConvertingNullUserToUserOutput() {
        assertThat(UserPresenter.toOutput(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter User para UserOutput corretamente")
    void shouldConvertUserToUserOutput() {
        UUID userId = UUID.randomUUID();
        String userName = "John Doe";
        String userEmail = "john.doe@example.com";

        User user = new User(userId, userName, userEmail, address, userType, "S&cre7&5tr0nG");

        UserOutput output = UserPresenter.toOutput(user);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(userId);
        assertThat(output.name()).isEqualTo(userName);
        assertThat(output.email()).isEqualTo(userEmail);
        assertThat(output.address()).isNotNull();
        assertThat(output.userType()).isNotNull();
        assertThat(output.userType().id()).isEqualTo(userTypeId);
        assertThat(output.userType().roles()).hasSize(2);
        assertThat(output.userType().roles()).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    @DisplayName("Deve converter User sem endereço para UserOutput corretamente")
    void shouldConvertUserWithoutAddressToUserOutput() {
        UUID userId = UUID.randomUUID();
        String userName = "John Doe";
        String userEmail = "john.doe@example.com";

        User user = new User(userId, userName, userEmail, null, userType, "S&cre7&5tr0nG");

        UserOutput output = UserPresenter.toOutput(user);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(userId);
        assertThat(output.name()).isEqualTo(userName);
        assertThat(output.email()).isEqualTo(userEmail);
        assertThat(output.address()).isNull();
        assertThat(output.userType()).isNotNull();
        assertThat(output.userType().id()).isEqualTo(userTypeId);
        assertThat(output.userType().roles()).hasSize(2);
        assertThat(output.userType().roles()).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    @DisplayName("Deve retornar nulo ao converter usuário nulo para UserSummaryOutput")
    void shouldReturnNullWhenConvertingNullUserToUserSummaryOutput() {
        assertThat(UserPresenter.toSummaryOutput(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter User para UserSummaryOutput corretamente")
    void shouldConvertUserToUserSummaryOutput() {
        var userId = UUID.randomUUID();
        String userName = "John Doe";
        User user = new User(userId, userName, "john.doe@example.com", address, userType, "S&cre7&5tr0nG");

        UserSummaryOutput output = UserPresenter.toSummaryOutput(user);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(userId);
        assertThat(output.name()).isEqualTo(userName);
    }
}
