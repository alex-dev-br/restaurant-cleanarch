package br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository;

import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTypeRepository extends JpaRepository<UserTypeEntity, Long> {
    Optional<UserTypeEntity> findByName(String name);
    @Query("SELECT count(u) FROM UserTypeEntity u JOIN UserEntity ue ON u.id = ue.userType.id WHERE u.id = :id")
    Long countUsersByTypeId(@Param("id") Long id);
}
