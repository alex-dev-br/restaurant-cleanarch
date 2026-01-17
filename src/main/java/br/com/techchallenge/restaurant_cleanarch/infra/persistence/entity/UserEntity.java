package br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class UserEntity implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = -5839444405465657922L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_type_id")
    private UserTypeEntity userType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Embedded
    private AddressEmbeddableEntity address;

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userType == null || userType.getRoles() == null) return new ArrayList<>();
        return userType.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList();
    }
}
