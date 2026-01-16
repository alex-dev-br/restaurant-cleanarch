package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.AddressBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.OpeningHoursBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateOpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateRestaurantUseCase")
class UpdateRestaurantUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private RestaurantGateway restaurantGateway;
    @Mock private UserGateway userGateway;

    @InjectMocks
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Captor
    private ArgumentCaptor<Restaurant> restaurantCaptor;

    private Long restaurantId;

    private UUID ownerId;
    private User owner;

    private UUID oldEmployeeId;
    private User oldEmployee;

    private Restaurant current;

    @BeforeEach
    void setUp() {
        restaurantId = 1L;

        ownerId = UUID.randomUUID();
        owner = new UserBuilder()
                .withDefaults()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        oldEmployeeId = UUID.randomUUID();
        oldEmployee = new UserBuilder()
                .withDefaults()
                .withId(oldEmployeeId)
                .build();

        current = new RestaurantBuilder()
                .withDefaults()
                .withId(restaurantId)
                .withName("Old Name")
                .withAddress(new AddressBuilder().build())
                .withCuisineType("Old Cuisine")
                .withOwner(owner)
                .withEmployees(Set.of(oldEmployee))
                .build();

        // deixa o restaurante “realista” (não é obrigatório para o use case, mas ajuda)
        current.addOpeningHours(new OpeningHoursBuilder().build());
        current.addMenuItem(new MenuItemBuilder().build());
    }

    @Test
    @DisplayName("Deve atualizar restaurante e substituir coleções quando fornecidas")
    void shouldUpdateRestaurantReplacingCollectionsWhenProvided() {
        UUID newOwnerId = UUID.randomUUID();
        User newOwner = new UserBuilder()
                .withDefaults()
                .withId(newOwnerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        UUID newEmployeeId = UUID.randomUUID();
        User newEmployee = new UserBuilder()
                .withDefaults()
                .withId(newEmployeeId)
                .build();

        UpdateOpeningHoursInput openingHoursInput = new OpeningHoursBuilder().buildUpdateInput();
        UpdateMenuItemInput menuItemInput = new MenuItemBuilder().buildUpdateInput();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "New Name",
                new AddressBuilder().buildInput(),
                "New Cuisine",
                Set.of(openingHoursInput),
                Set.of(menuItemInput),
                Set.of(newEmployeeId),
                newOwnerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner); // pode gerenciar (é dono)
        given(userGateway.findById(newOwnerId)).willReturn(Optional.of(newOwner));
        given(restaurantGateway.existsRestaurantWithName("New Name")).willReturn(false);
        given(userGateway.findById(newEmployeeId)).willReturn(Optional.of(newEmployee));

        updateRestaurantUseCase.execute(input);

        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getId()).isEqualTo(restaurantId);
        assertThat(saved.getName()).isEqualTo("New Name");
        assertThat(saved.getCuisineType()).isEqualTo("New Cuisine");
        assertThat(saved.getOwner()).isEqualTo(newOwner);

        assertThat(saved.getEmployees()).containsExactlyInAnyOrder(newEmployee);
        assertThat(saved.getOpeningHours()).hasSize(1);
        assertThat(saved.getMenuItems()).hasSize(1);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(userGateway).should().findById(newOwnerId);
        then(restaurantGateway).should().existsRestaurantWithName("New Name");
        then(userGateway).should().findById(newEmployeeId);
    }

    @Test
    @DisplayName("Deve manter employees atuais quando employees no input for null (PATCH semantics)")
    void shouldKeepEmployeesWhenEmployeesIsNull() {
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null, // mantém nome atual
                null, // mantém address atual
                null, // mantém cuisine atual
                null, // mantém openingHours atual
                null, // mantém menu atual
                null, // employees null => mantém
                null  // owner null => mantém owner atual
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));

        updateRestaurantUseCase.execute(input);

        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getEmployees()).containsExactlyInAnyOrder(oldEmployee);
        assertThat(saved.getOwner()).isEqualTo(owner);

        // nome não mudou -> NÃO deve consultar existsRestaurantWithName
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());

        // employees null -> NÃO consulta employee no gateway
        then(userGateway).should(times(1)).findById(ownerId);
        then(userGateway).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando employee não existir e não salvar")
    void shouldThrowWhenEmployeeNotFound() {
        UUID missingEmployeeId = UUID.randomUUID();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                Set.of(missingEmployeeId),
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(missingEmployeeId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee " + missingEmployeeId + " not found.");

        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(
                new UpdateRestaurantInput(restaurantId, "x", null, "y", null, null, null, null)
        ))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("permission to perform this action");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário logado não é owner nem employee")
    void shouldThrowOperationNotAllowedWhenCurrentUserCannotManage() {
        User outsider = new UserBuilder().withDefaults().withId(UUID.randomUUID()).build();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Any",
                null,
                null,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(outsider);

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("permission to perform this action");

        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar RestaurantNameIsAlreadyInUseException quando nome mudar e já existir")
    void shouldThrowWhenRestaurantNameAlreadyInUse() {
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "New Name",
                null,
                null,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.existsRestaurantWithName("New Name")).willReturn(true);

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class);

        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando novo owner não for encontrado")
    void shouldThrowWhenNewOwnerNotFound() {
        UUID newOwnerId = UUID.randomUUID();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                null,
                newOwnerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(newOwnerId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Owner not found.");

        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar UserCannotBeRestaurantOwnerException quando novo owner não pode ser dono")
    void shouldThrowWhenUserCannotBeOwner() {
        UUID newOwnerId = UUID.randomUUID();
        User newOwner = new UserBuilder().withDefaults().withId(newOwnerId).build(); // sem role de owner

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null, // nome não muda
                null,
                null,
                null,
                null,
                null,
                newOwnerId
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(newOwnerId)).willReturn(Optional.of(newOwner));

        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class);

        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for null (UseCaseBase)")
    void shouldThrowWhenInputIsNull() {
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }
}
