package com.beleykanych.amazon.service;

import com.beleykanych.amazon.domain.Cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Cart}.
 */
public interface CartService {

    /**
     * Save a cart.
     *
     * @param cart the entity to save.
     * @return the persisted entity.
     */
    Cart save(Cart cart);

    /**
     * Get all the carts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Cart> findAll(Pageable pageable);
    /**
     * Get all the CartDTO where Customer is {@code null}.
     *
     * @return the list of entities.
     */
    List<Cart> findAllWhereCustomerIsNull();

    /**
     * Get all the carts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Page<Cart> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" cart.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Cart> findOne(Long id);

    /**
     * Delete the "id" cart.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
