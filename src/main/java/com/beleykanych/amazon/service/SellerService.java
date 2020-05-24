package com.beleykanych.amazon.service;

import com.beleykanych.amazon.domain.Seller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Seller}.
 */
public interface SellerService {

    /**
     * Save a seller.
     *
     * @param seller the entity to save.
     * @return the persisted entity.
     */
    Seller save(Seller seller);

    /**
     * Get all the sellers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Seller> findAll(Pageable pageable);

    /**
     * Get the "id" seller.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Seller> findOne(Long id);

    /**
     * Delete the "id" seller.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
