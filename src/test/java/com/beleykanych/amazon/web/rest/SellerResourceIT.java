package com.beleykanych.amazon.web.rest;

import com.beleykanych.amazon.AmazonApp;
import com.beleykanych.amazon.domain.Seller;
import com.beleykanych.amazon.domain.Product;
import com.beleykanych.amazon.repository.SellerRepository;
import com.beleykanych.amazon.service.SellerService;
import com.beleykanych.amazon.service.dto.SellerCriteria;
import com.beleykanych.amazon.service.SellerQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link SellerResource} REST controller.
 */
@SpringBootTest(classes = AmazonApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class SellerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "+736225368895";
    private static final String UPDATED_PHONE = "+9 2792734564";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private SellerQueryService sellerQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSellerMockMvc;

    private Seller seller;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Seller createEntity(EntityManager em) {
        Seller seller = new Seller()
            .name(DEFAULT_NAME)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS);
        return seller;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Seller createUpdatedEntity(EntityManager em) {
        Seller seller = new Seller()
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS);
        return seller;
    }

    @BeforeEach
    public void initTest() {
        seller = createEntity(em);
    }

    @Test
    @Transactional
    public void createSeller() throws Exception {
        int databaseSizeBeforeCreate = sellerRepository.findAll().size();

        // Create the Seller
        restSellerMockMvc.perform(post("/api/sellers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(seller)))
            .andExpect(status().isCreated());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeCreate + 1);
        Seller testSeller = sellerList.get(sellerList.size() - 1);
        assertThat(testSeller.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSeller.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testSeller.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    public void createSellerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sellerRepository.findAll().size();

        // Create the Seller with an existing ID
        seller.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSellerMockMvc.perform(post("/api/sellers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(seller)))
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sellerRepository.findAll().size();
        // set the field null
        seller.setName(null);

        // Create the Seller, which fails.

        restSellerMockMvc.perform(post("/api/sellers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(seller)))
            .andExpect(status().isBadRequest());

        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSellers() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList
        restSellerMockMvc.perform(get("/api/sellers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seller.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }
    
    @Test
    @Transactional
    public void getSeller() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get the seller
        restSellerMockMvc.perform(get("/api/sellers/{id}", seller.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(seller.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS));
    }


    @Test
    @Transactional
    public void getSellersByIdFiltering() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        Long id = seller.getId();

        defaultSellerShouldBeFound("id.equals=" + id);
        defaultSellerShouldNotBeFound("id.notEquals=" + id);

        defaultSellerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSellerShouldNotBeFound("id.greaterThan=" + id);

        defaultSellerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSellerShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllSellersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name equals to DEFAULT_NAME
        defaultSellerShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the sellerList where name equals to UPDATED_NAME
        defaultSellerShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSellersByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name not equals to DEFAULT_NAME
        defaultSellerShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the sellerList where name not equals to UPDATED_NAME
        defaultSellerShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSellersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSellerShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the sellerList where name equals to UPDATED_NAME
        defaultSellerShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSellersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name is not null
        defaultSellerShouldBeFound("name.specified=true");

        // Get all the sellerList where name is null
        defaultSellerShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllSellersByNameContainsSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name contains DEFAULT_NAME
        defaultSellerShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the sellerList where name contains UPDATED_NAME
        defaultSellerShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSellersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name does not contain DEFAULT_NAME
        defaultSellerShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the sellerList where name does not contain UPDATED_NAME
        defaultSellerShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllSellersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where phone equals to DEFAULT_PHONE
        defaultSellerShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the sellerList where phone equals to UPDATED_PHONE
        defaultSellerShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllSellersByPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where phone not equals to DEFAULT_PHONE
        defaultSellerShouldNotBeFound("phone.notEquals=" + DEFAULT_PHONE);

        // Get all the sellerList where phone not equals to UPDATED_PHONE
        defaultSellerShouldBeFound("phone.notEquals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllSellersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultSellerShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the sellerList where phone equals to UPDATED_PHONE
        defaultSellerShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllSellersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where phone is not null
        defaultSellerShouldBeFound("phone.specified=true");

        // Get all the sellerList where phone is null
        defaultSellerShouldNotBeFound("phone.specified=false");
    }
                @Test
    @Transactional
    public void getAllSellersByPhoneContainsSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where phone contains DEFAULT_PHONE
        defaultSellerShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the sellerList where phone contains UPDATED_PHONE
        defaultSellerShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllSellersByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where phone does not contain DEFAULT_PHONE
        defaultSellerShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the sellerList where phone does not contain UPDATED_PHONE
        defaultSellerShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }


    @Test
    @Transactional
    public void getAllSellersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where address equals to DEFAULT_ADDRESS
        defaultSellerShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the sellerList where address equals to UPDATED_ADDRESS
        defaultSellerShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllSellersByAddressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where address not equals to DEFAULT_ADDRESS
        defaultSellerShouldNotBeFound("address.notEquals=" + DEFAULT_ADDRESS);

        // Get all the sellerList where address not equals to UPDATED_ADDRESS
        defaultSellerShouldBeFound("address.notEquals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllSellersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultSellerShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the sellerList where address equals to UPDATED_ADDRESS
        defaultSellerShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllSellersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where address is not null
        defaultSellerShouldBeFound("address.specified=true");

        // Get all the sellerList where address is null
        defaultSellerShouldNotBeFound("address.specified=false");
    }
                @Test
    @Transactional
    public void getAllSellersByAddressContainsSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where address contains DEFAULT_ADDRESS
        defaultSellerShouldBeFound("address.contains=" + DEFAULT_ADDRESS);

        // Get all the sellerList where address contains UPDATED_ADDRESS
        defaultSellerShouldNotBeFound("address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllSellersByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where address does not contain DEFAULT_ADDRESS
        defaultSellerShouldNotBeFound("address.doesNotContain=" + DEFAULT_ADDRESS);

        // Get all the sellerList where address does not contain UPDATED_ADDRESS
        defaultSellerShouldBeFound("address.doesNotContain=" + UPDATED_ADDRESS);
    }


    @Test
    @Transactional
    public void getAllSellersByProductsIsEqualToSomething() throws Exception {
        // Initialize the database
        sellerRepository.saveAndFlush(seller);
        Product products = ProductResourceIT.createEntity(em);
        em.persist(products);
        em.flush();
        seller.addProducts(products);
        sellerRepository.saveAndFlush(seller);
        Long productsId = products.getId();

        // Get all the sellerList where products equals to productsId
        defaultSellerShouldBeFound("productsId.equals=" + productsId);

        // Get all the sellerList where products equals to productsId + 1
        defaultSellerShouldNotBeFound("productsId.equals=" + (productsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSellerShouldBeFound(String filter) throws Exception {
        restSellerMockMvc.perform(get("/api/sellers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seller.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));

        // Check, that the count call also returns 1
        restSellerMockMvc.perform(get("/api/sellers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSellerShouldNotBeFound(String filter) throws Exception {
        restSellerMockMvc.perform(get("/api/sellers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSellerMockMvc.perform(get("/api/sellers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSeller() throws Exception {
        // Get the seller
        restSellerMockMvc.perform(get("/api/sellers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSeller() throws Exception {
        // Initialize the database
        sellerService.save(seller);

        int databaseSizeBeforeUpdate = sellerRepository.findAll().size();

        // Update the seller
        Seller updatedSeller = sellerRepository.findById(seller.getId()).get();
        // Disconnect from session so that the updates on updatedSeller are not directly saved in db
        em.detach(updatedSeller);
        updatedSeller
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS);

        restSellerMockMvc.perform(put("/api/sellers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSeller)))
            .andExpect(status().isOk());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeUpdate);
        Seller testSeller = sellerList.get(sellerList.size() - 1);
        assertThat(testSeller.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSeller.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testSeller.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void updateNonExistingSeller() throws Exception {
        int databaseSizeBeforeUpdate = sellerRepository.findAll().size();

        // Create the Seller

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSellerMockMvc.perform(put("/api/sellers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(seller)))
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSeller() throws Exception {
        // Initialize the database
        sellerService.save(seller);

        int databaseSizeBeforeDelete = sellerRepository.findAll().size();

        // Delete the seller
        restSellerMockMvc.perform(delete("/api/sellers/{id}", seller.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Seller> sellerList = sellerRepository.findAll();
        assertThat(sellerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
