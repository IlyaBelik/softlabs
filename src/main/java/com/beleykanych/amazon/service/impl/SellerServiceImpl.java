package com.beleykanych.amazon.service.impl;

import com.beleykanych.amazon.service.SellerService;
import com.beleykanych.amazon.domain.Seller;
import com.beleykanych.amazon.repository.SellerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Seller}.
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    private final Logger log = LoggerFactory.getLogger(SellerServiceImpl.class);

    private final SellerRepository sellerRepository;

    public SellerServiceImpl(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    /**
     * Save a seller.
     *
     * @param seller the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Seller save(Seller seller) {
        log.debug("Request to save Seller : {}", seller);
        return sellerRepository.save(seller);
    }

    /**
     * Get all the sellers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Seller> findAll(Pageable pageable) {
        log.debug("Request to get all Sellers");
        return sellerRepository.findAll(pageable);
    }

    /**
     * Get one seller by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Seller> findOne(Long id) {
        log.debug("Request to get Seller : {}", id);
        return sellerRepository.findById(id);
    }

    /**
     * Delete the seller by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Seller : {}", id);
        sellerRepository.deleteById(id);
    }
}
