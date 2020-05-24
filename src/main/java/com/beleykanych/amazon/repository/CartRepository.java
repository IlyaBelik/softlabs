package com.beleykanych.amazon.repository;

import com.beleykanych.amazon.domain.Cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Cart entity.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, JpaSpecificationExecutor<Cart> {

    @Query(value = "select distinct cart from Cart cart left join fetch cart.productsIns",
        countQuery = "select count(distinct cart) from Cart cart")
    Page<Cart> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct cart from Cart cart left join fetch cart.productsIns")
    List<Cart> findAllWithEagerRelationships();

    @Query("select cart from Cart cart left join fetch cart.productsIns where cart.id =:id")
    Optional<Cart> findOneWithEagerRelationships(@Param("id") Long id);
}
