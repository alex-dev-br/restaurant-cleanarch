package br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository;

import br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    // Management view
    @Query("""
        SELECT DISTINCT r FROM RestaurantEntity r
        LEFT JOIN FETCH r.menu
        LEFT JOIN FETCH r.openingHours
        LEFT JOIN FETCH r.employeeLinks el
        LEFT JOIN FETCH el.employee
        LEFT JOIN FETCH r.owner
        WHERE r.id = :id
    """)
    Optional<RestaurantEntity> findByIdWithManagement(@Param("id") Long id);

    // Público por ID
    @Query("""
        SELECT DISTINCT r FROM RestaurantEntity r
        LEFT JOIN FETCH r.menu
        LEFT JOIN FETCH r.openingHours
        LEFT JOIN FETCH r.owner
        WHERE r.id = :id
    """)
    Optional<RestaurantEntity> findByIdWithPublicData(@Param("id") Long id);

    // 2 passos — (1) paginação só com IDs por cuisine
    @Query("""
        SELECT r.id FROM RestaurantEntity r
        WHERE LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :filter, '%'))
    """)
    Page<Long> findIdsByCuisineType(@Param("filter") String filter, Pageable pageable);

    // 2 passos — (2) busca completa (sem paginação) com fetch join (public)
    @Query("""
        SELECT DISTINCT r FROM RestaurantEntity r
        LEFT JOIN FETCH r.menu
        LEFT JOIN FETCH r.openingHours
        LEFT JOIN FETCH r.owner
        WHERE r.id IN :ids
    """)
    List<RestaurantEntity> findAllByIdInWithPublicData(@Param("ids") List<Long> ids);

    // 2 passos — (1) paginação só com IDs (sem filtro)
    @Query("SELECT r.id FROM RestaurantEntity r")
    Page<Long> findAllIds(Pageable pageable);
}
