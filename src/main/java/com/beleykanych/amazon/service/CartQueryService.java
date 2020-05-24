package com.beleykanych.amazon.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.beleykanych.amazon.domain.Cart;
import com.beleykanych.amazon.domain.*; // for static metamodels
import com.beleykanych.amazon.repository.CartRepository;
import com.beleykanych.amazon.service.dto.CartCriteria;

/**
 * Service for executing complex queries for {@link Cart} entities in the database.
 * The main input is a {@link CartCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Cart} or a {@link Page} of {@link Cart} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CartQueryService extends QueryService<Cart> {

    private final Logger log = LoggerFactory.getLogger(CartQueryService.class);

    private final CartRepository cartRepository;

    public CartQueryService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Return a {@link List} of {@link Cart} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Cart> findByCriteria(CartCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Cart> specification = createSpecification(criteria);
        return cartRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Cart} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Cart> findByCriteria(CartCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Cart> specification = createSpecification(criteria);
        return cartRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CartCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Cart> specification = createSpecification(criteria);
        return cartRepository.count(specification);
    }

    /**
     * Function to convert {@link CartCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Cart> createSpecification(CartCriteria criteria) {
        Specification<Cart> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Cart_.id));
            }
            if (criteria.getProductsNumber() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getProductsNumber(), Cart_.productsNumber));
            }
            if (criteria.getTotalPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalPrice(), Cart_.totalPrice));
            }
            if (criteria.getProductsInId() != null) {
                specification = specification.and(buildSpecification(criteria.getProductsInId(),
                    root -> root.join(Cart_.productsIns, JoinType.LEFT).get(Product_.id)));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildSpecification(criteria.getCustomerId(),
                    root -> root.join(Cart_.customer, JoinType.LEFT).get(Customer_.id)));
            }
        }
        return specification;
    }
}
