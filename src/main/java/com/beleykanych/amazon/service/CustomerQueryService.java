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

import com.beleykanych.amazon.domain.Customer;
import com.beleykanych.amazon.domain.*; // for static metamodels
import com.beleykanych.amazon.repository.CustomerRepository;
import com.beleykanych.amazon.service.dto.CustomerCriteria;

/**
 * Service for executing complex queries for {@link Customer} entities in the database.
 * The main input is a {@link CustomerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Customer} or a {@link Page} of {@link Customer} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomerQueryService extends QueryService<Customer> {

    private final Logger log = LoggerFactory.getLogger(CustomerQueryService.class);

    private final CustomerRepository customerRepository;

    public CustomerQueryService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Return a {@link List} of {@link Customer} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Customer> findByCriteria(CustomerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Customer} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Customer> findByCriteria(CustomerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Customer> createSpecification(CustomerCriteria criteria) {
        Specification<Customer> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Customer_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Customer_.name));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Customer_.phone));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Customer_.address));
            }
            if (criteria.getCartId() != null) {
                specification = specification.and(buildSpecification(criteria.getCartId(),
                    root -> root.join(Customer_.cart, JoinType.LEFT).get(Cart_.id)));
            }
            if (criteria.getPaymentsId() != null) {
                specification = specification.and(buildSpecification(criteria.getPaymentsId(),
                    root -> root.join(Customer_.payments, JoinType.LEFT).get(Payment_.id)));
            }
            if (criteria.getOrderedProductsId() != null) {
                specification = specification.and(buildSpecification(criteria.getOrderedProductsId(),
                    root -> root.join(Customer_.orderedProducts, JoinType.LEFT).get(Product_.id)));
            }
        }
        return specification;
    }
}
