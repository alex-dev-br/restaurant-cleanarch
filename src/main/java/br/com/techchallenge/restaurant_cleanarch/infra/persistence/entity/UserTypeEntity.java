package br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class UserTypeEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String name;
}
