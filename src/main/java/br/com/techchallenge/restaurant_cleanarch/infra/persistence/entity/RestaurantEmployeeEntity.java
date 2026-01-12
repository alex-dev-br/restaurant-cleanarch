package br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "restaurant_employees")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RestaurantEmployeeEntity {

    @EmbeddedId
    private RestaurantEmployeeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("restaurantId")
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id", nullable = false)
    private UserEntity employee;

    public static RestaurantEmployeeEntity link(
            RestaurantEntity restaurant,
            UserEntity employee)
    {
        var entity = new RestaurantEmployeeEntity();
        entity.setRestaurant(restaurant);
        entity.setEmployee(employee);

        Long restaurantId = restaurant.getId();   // pode ser null antes de persistir
        entity.setId(new RestaurantEmployeeId(restaurant.getId(), employee.getId()));

        return entity;
    }


    @Embeddable
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class RestaurantEmployeeId implements Serializable {
        @Column(name = "restaurant_id")
        private Long restaurantId;

        @Column(name = "employee_id")
        private UUID employeeId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RestaurantEmployeeId that)) return false;
            return Objects.equals(restaurantId, that.restaurantId)
                    && Objects.equals(employeeId, that.employeeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(restaurantId, employeeId);
        }
    }
}
