package br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
public class MenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "restaurant_only", nullable = false)
    private Boolean restaurantOnly;

    @Column(name = "photo_path")
    private String photoPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    public MenuItemEntity(String name, String description, BigDecimal price, Boolean restaurantOnly, String photoPath, RestaurantEntity restaurant) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurantOnly = restaurantOnly;
        this.photoPath = photoPath;
        this.restaurant = restaurant;
    }
}
