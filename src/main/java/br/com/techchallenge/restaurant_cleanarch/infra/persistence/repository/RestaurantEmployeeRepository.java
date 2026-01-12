package br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository;

import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEmployeeEntity;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEmployeeEntity.RestaurantEmployeeId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantEmployeeRepository extends JpaRepository<RestaurantEmployeeEntity, RestaurantEmployeeId> {
}
