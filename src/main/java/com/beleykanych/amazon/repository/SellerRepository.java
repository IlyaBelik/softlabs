package com.beleykanych.amazon.repository;

import com.beleykanych.amazon.domain.Seller;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Seller entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SellerRepository extends JpaRepository<Seller, Long>, JpaSpecificationExecutor<Seller> {
}
