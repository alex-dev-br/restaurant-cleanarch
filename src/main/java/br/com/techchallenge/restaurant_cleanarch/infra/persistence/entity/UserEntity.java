package br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id")
    private UserTypeEntity userType;

    @Column
    private String name;

    @Column
    private String email;

    @Embedded
    private AddressEmbeddableEntity address;

    //TODO implementar o resto, primeira implementação apenas para teste do user type
}
