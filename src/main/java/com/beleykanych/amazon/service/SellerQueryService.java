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

import com.beleykanych.amazon.domain.Seller;
import com.beleykanych.amazon.domain.*; // for static metamodels
import com.beleykanych.amazon.repository.SellerRepository;
import com.beleykanych.amazon.service.dto.SellerCriteria;

/**
 * Service for executing complex queries for {@link Seller} entities in the database.
 * The main input is a {@link SellerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Seller} or a {@link Page} of {@link Seller} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SellerQueryService extends QueryService<Seller> {

    private final Logger log = LoggerFactory.getLogger(SellerQueryService.class);

    private final SellerRepository sellerRepository;

    public SellerQueryService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    /**
     * Return a {@link List} of {@link Seller} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Seller> findByCriteria(SellerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Seller> specification = createSpecification(criteria);
        return sellerRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Seller} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Seller> findByCriteria(SellerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Seller> specification = createSpecification(criteria);
        return sellerRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SellerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Seller> specification = createSpecification(criteria);
        return sellerRepository.count(specification);
    }

    /**
     * Function to convert {@link SellerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Seller> createSpecification(SellerCriteria criteria) {
        Specification<Seller> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Seller_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Seller_.name));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Seller_.phone));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Seller_.address));
            }
            if (criteria.getProductsId() != null) {
                specification = specification.and(buildSpecification(criteria.getProductsId(),
                    root -> root.join(Seller_.products, JoinType.LEFT).get(Product_.id)));
            }
        }
        return specification;
    }
}
