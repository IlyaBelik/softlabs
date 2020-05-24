package com.beleykanych.amazon.repository;

import com.beleykanych.amazon.domain.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Customer entity.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    @Query(value = "select distinct customer from Customer customer left join fetch customer.orderedProducts",
        countQuery = "select count(distinct customer) from Customer customer")
    Page<Customer> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct customer from Customer customer left join fetch customer.orderedProducts")
    List<Customer> findAllWithEagerRelationships();

    @Query("select customer from Customer customer left join fetch customer.orderedProducts where customer.id =:id")
    Optional<Customer> findOneWithEagerRelationships(@Param("id") Long id);
}
