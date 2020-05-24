package com.beleykanych.amazon.service.impl;

import com.beleykanych.amazon.service.CartService;
import com.beleykanych.amazon.domain.Cart;
import com.beleykanych.amazon.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Implementation for managing {@link Cart}.
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Save a cart.
     *
     * @param cart the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Cart save(Cart cart) {
        log.debug("Request to save Cart : {}", cart);
        return cartRepository.save(cart);
    }

    /**
     * Get all the carts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Cart> findAll(Pageable pageable) {
        log.debug("Request to get all Carts");
        return cartRepository.findAll(pageable);
    }

    /**
     * Get all the carts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Cart> findAllWithEagerRelationships(Pageable pageable) {
        return cartRepository.findAllWithEagerRelationships(pageable);
    }


    /**
     *  Get all the carts where Customer is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true) 
    public List<Cart> findAllWhereCustomerIsNull() {
        log.debug("Request to get all carts where Customer is null");
        return StreamSupport
            .stream(cartRepository.findAll().spliterator(), false)
            .filter(cart -> cart.getCustomer() == null)
            .collect(Collectors.toList());
    }

    /**
     * Get one cart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findOne(Long id) {
        log.debug("Request to get Cart : {}", id);
        return cartRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the cart by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Cart : {}", id);
        cartRepository.deleteById(id);
    }
}
