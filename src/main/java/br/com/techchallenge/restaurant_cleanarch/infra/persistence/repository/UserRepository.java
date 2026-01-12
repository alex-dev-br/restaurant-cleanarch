package br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository;

import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
