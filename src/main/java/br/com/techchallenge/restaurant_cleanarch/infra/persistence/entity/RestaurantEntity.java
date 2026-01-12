package br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "restaurants")
@Getter @Setter
@NoArgsConstructor
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Embedded
    private AddressEmbeddableEntity address;

    @Column(name = "cuisine_type", nullable = false)
    private String cuisineType;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OpeningHoursEntity> openingHours = new HashSet<>();  // inicializa para evitar NPE;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)   // `orphanRemoval = true` garante que itens removidos do menu sejam deletados do banco.
    private Set<MenuItemEntity> menu = new HashSet<>();  // inicializa para evitar NPE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RestaurantEmployeeEntity> employeeLinks = new HashSet<>();

    // Conveniência para acessar empregados diretamente (NÃO é property pro MapStruct)
    @Transient
    public Set<UserEntity> employeesView() {
        if (employeeLinks == null || employeeLinks.isEmpty()) return Set.of();
        Set<UserEntity> employees = new HashSet<>();
        for (var link : employeeLinks) {
            employees.add(link.getEmployee());
        }
        return Collections.unmodifiableSet(employees);
    }

    public void addEmployee(UserEntity employee) {
        if (employeeLinks == null) employeeLinks = new HashSet<>();
        employeeLinks.add(RestaurantEmployeeEntity.link(this, employee));
    }

    public void clearEmployees() {
        if (employeeLinks != null) employeeLinks.clear();
    }

}