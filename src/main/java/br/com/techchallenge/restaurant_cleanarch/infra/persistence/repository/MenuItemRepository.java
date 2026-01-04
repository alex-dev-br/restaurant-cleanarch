package br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository;

import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.MenuItemEntity;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
}
