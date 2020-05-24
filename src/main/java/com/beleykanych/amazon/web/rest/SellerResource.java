package com.beleykanych.amazon.web.rest;

import com.beleykanych.amazon.domain.Seller;
import com.beleykanych.amazon.service.SellerService;
import com.beleykanych.amazon.web.rest.errors.BadRequestAlertException;
import com.beleykanych.amazon.service.dto.SellerCriteria;
import com.beleykanych.amazon.service.SellerQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.beleykanych.amazon.domain.Seller}.
 */
@RestController
@RequestMapping("/api")
public class SellerResource {

    private final Logger log = LoggerFactory.getLogger(SellerResource.class);

    private static final String ENTITY_NAME = "seller";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SellerService sellerService;

    private final SellerQueryService sellerQueryService;

    public SellerResource(SellerService sellerService, SellerQueryService sellerQueryService) {
        this.sellerService = sellerService;
        this.sellerQueryService = sellerQueryService;
    }

    /**
     * {@code POST  /sellers} : Create a new seller.
     *
     * @param seller the seller to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new seller, or with status {@code 400 (Bad Request)} if the seller has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sellers")
    public ResponseEntity<Seller> createSeller(@Valid @RequestBody Seller seller) throws URISyntaxException {
        log.debug("REST request to save Seller : {}", seller);
        if (seller.getId() != null) {
            throw new BadRequestAlertException("A new seller cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Seller result = sellerService.save(seller);
        return ResponseEntity.created(new URI("/api/sellers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sellers} : Updates an existing seller.
     *
     * @param seller the seller to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seller,
     * or with status {@code 400 (Bad Request)} if the seller is not valid,
     * or with status {@code 500 (Internal Server Error)} if the seller couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sellers")
    public ResponseEntity<Seller> updateSeller(@Valid @RequestBody Seller seller) throws URISyntaxException {
        log.debug("REST request to update Seller : {}", seller);
        if (seller.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Seller result = sellerService.save(seller);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, seller.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /sellers} : get all the sellers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sellers in body.
     */
    @GetMapping("/sellers")
    public ResponseEntity<List<Seller>> getAllSellers(SellerCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Sellers by criteria: {}", criteria);
        Page<Seller> page = sellerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sellers/count} : count all the sellers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sellers/count")
    public ResponseEntity<Long> countSellers(SellerCriteria criteria) {
        log.debug("REST request to count Sellers by criteria: {}", criteria);
        return ResponseEntity.ok().body(sellerQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sellers/:id} : get the "id" seller.
     *
     * @param id the id of the seller to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the seller, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sellers/{id}")
    public ResponseEntity<Seller> getSeller(@PathVariable Long id) {
        log.debug("REST request to get Seller : {}", id);
        Optional<Seller> seller = sellerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(seller);
    }

    /**
     * {@code DELETE  /sellers/:id} : delete the "id" seller.
     *
     * @param id the id of the seller to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sellers/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        log.debug("REST request to delete Seller : {}", id);
        sellerService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
