package com.beleykanych.amazon.web.rest;

import com.beleykanych.amazon.AmazonApp;
import com.beleykanych.amazon.domain.Product;
import com.beleykanych.amazon.domain.Seller;
import com.beleykanych.amazon.domain.Cart;
import com.beleykanych.amazon.domain.Customer;
import com.beleykanych.amazon.repository.ProductRepository;
import com.beleykanych.amazon.service.ProductService;
import com.beleykanych.amazon.service.dto.ProductCriteria;
import com.beleykanych.amazon.service.ProductQueryService;

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
 * Integration tests for the {@link ProductResource} REST controller.
 */
@SpringBootTest(classes = AmazonApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RECOMMENDE_AGE_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_RECOMMENDE_AGE_GROUP = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductQueryService productQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .name(DEFAULT_NAME)
            .recommendeAgeGroup(DEFAULT_RECOMMENDE_AGE_GROUP)
            .category(DEFAULT_CATEGORY);
        return product;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .name(UPDATED_NAME)
            .recommendeAgeGroup(UPDATED_RECOMMENDE_AGE_GROUP)
            .category(UPDATED_CATEGORY);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    public void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product
        restProductMockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getRecommendeAgeGroup()).isEqualTo(DEFAULT_RECOMMENDE_AGE_GROUP);
        assertThat(testProduct.getCategory()).isEqualTo(DEFAULT_CATEGORY);
    }

    @Test
    @Transactional
    public void createProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product with an existing ID
        product.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.

        restProductMockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc.perform(get("/api/products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].recommendeAgeGroup").value(hasItem(DEFAULT_RECOMMENDE_AGE_GROUP)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)));
    }
    
    @Test
    @Transactional
    public void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.recommendeAgeGroup").value(DEFAULT_RECOMMENDE_AGE_GROUP))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY));
    }


    @Test
    @Transactional
    public void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);

        defaultProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name equals to DEFAULT_NAME
        defaultProductShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProductsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name not equals to DEFAULT_NAME
        defaultProductShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the productList where name not equals to UPDATED_NAME
        defaultProductShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name is not null
        defaultProductShouldBeFound("name.specified=true");

        // Get all the productList where name is null
        defaultProductShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name contains DEFAULT_NAME
        defaultProductShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the productList where name contains UPDATED_NAME
        defaultProductShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name does not contain DEFAULT_NAME
        defaultProductShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the productList where name does not contain UPDATED_NAME
        defaultProductShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllProductsByRecommendeAgeGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where recommendeAgeGroup equals to DEFAULT_RECOMMENDE_AGE_GROUP
        defaultProductShouldBeFound("recommendeAgeGroup.equals=" + DEFAULT_RECOMMENDE_AGE_GROUP);

        // Get all the productList where recommendeAgeGroup equals to UPDATED_RECOMMENDE_AGE_GROUP
        defaultProductShouldNotBeFound("recommendeAgeGroup.equals=" + UPDATED_RECOMMENDE_AGE_GROUP);
    }

    @Test
    @Transactional
    public void getAllProductsByRecommendeAgeGroupIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where recommendeAgeGroup not equals to DEFAULT_RECOMMENDE_AGE_GROUP
        defaultProductShouldNotBeFound("recommendeAgeGroup.notEquals=" + DEFAULT_RECOMMENDE_AGE_GROUP);

        // Get all the productList where recommendeAgeGroup not equals to UPDATED_RECOMMENDE_AGE_GROUP
        defaultProductShouldBeFound("recommendeAgeGroup.notEquals=" + UPDATED_RECOMMENDE_AGE_GROUP);
    }

    @Test
    @Transactional
    public void getAllProductsByRecommendeAgeGroupIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where recommendeAgeGroup in DEFAULT_RECOMMENDE_AGE_GROUP or UPDATED_RECOMMENDE_AGE_GROUP
        defaultProductShouldBeFound("recommendeAgeGroup.in=" + DEFAULT_RECOMMENDE_AGE_GROUP + "," + UPDATED_RECOMMENDE_AGE_GROUP);

        // Get all the productList where recommendeAgeGroup equals to UPDATED_RECOMMENDE_AGE_GROUP
        defaultProductShouldNotBeFound("recommendeAgeGroup.in=" + UPDATED_RECOMMENDE_AGE_GROUP);
    }

    @Test
    @Transactional
    public void getAllProductsByRecommendeAgeGroupIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where recommendeAgeGroup is not null
        defaultProductShouldBeFound("recommendeAgeGroup.specified=true");

        // Get all the productList where recommendeAgeGroup is null
        defaultProductShouldNotBeFound("recommendeAgeGroup.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductsByRecommendeAgeGroupContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where recommendeAgeGroup contains DEFAULT_RECOMMENDE_AGE_GROUP
        defaultProductShouldBeFound("recommendeAgeGroup.contains=" + DEFAULT_RECOMMENDE_AGE_GROUP);

        // Get all the productList where recommendeAgeGroup contains UPDATED_RECOMMENDE_AGE_GROUP
        defaultProductShouldNotBeFound("recommendeAgeGroup.contains=" + UPDATED_RECOMMENDE_AGE_GROUP);
    }

    @Test
    @Transactional
    public void getAllProductsByRecommendeAgeGroupNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where recommendeAgeGroup does not contain DEFAULT_RECOMMENDE_AGE_GROUP
        defaultProductShouldNotBeFound("recommendeAgeGroup.doesNotContain=" + DEFAULT_RECOMMENDE_AGE_GROUP);

        // Get all the productList where recommendeAgeGroup does not contain UPDATED_RECOMMENDE_AGE_GROUP
        defaultProductShouldBeFound("recommendeAgeGroup.doesNotContain=" + UPDATED_RECOMMENDE_AGE_GROUP);
    }


    @Test
    @Transactional
    public void getAllProductsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where category equals to DEFAULT_CATEGORY
        defaultProductShouldBeFound("category.equals=" + DEFAULT_CATEGORY);

        // Get all the productList where category equals to UPDATED_CATEGORY
        defaultProductShouldNotBeFound("category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllProductsByCategoryIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where category not equals to DEFAULT_CATEGORY
        defaultProductShouldNotBeFound("category.notEquals=" + DEFAULT_CATEGORY);

        // Get all the productList where category not equals to UPDATED_CATEGORY
        defaultProductShouldBeFound("category.notEquals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllProductsByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where category in DEFAULT_CATEGORY or UPDATED_CATEGORY
        defaultProductShouldBeFound("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY);

        // Get all the productList where category equals to UPDATED_CATEGORY
        defaultProductShouldNotBeFound("category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllProductsByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where category is not null
        defaultProductShouldBeFound("category.specified=true");

        // Get all the productList where category is null
        defaultProductShouldNotBeFound("category.specified=false");
    }
                @Test
    @Transactional
    public void getAllProductsByCategoryContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where category contains DEFAULT_CATEGORY
        defaultProductShouldBeFound("category.contains=" + DEFAULT_CATEGORY);

        // Get all the productList where category contains UPDATED_CATEGORY
        defaultProductShouldNotBeFound("category.contains=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllProductsByCategoryNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where category does not contain DEFAULT_CATEGORY
        defaultProductShouldNotBeFound("category.doesNotContain=" + DEFAULT_CATEGORY);

        // Get all the productList where category does not contain UPDATED_CATEGORY
        defaultProductShouldBeFound("category.doesNotContain=" + UPDATED_CATEGORY);
    }


    @Test
    @Transactional
    public void getAllProductsBySellerIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        Seller seller = SellerResourceIT.createEntity(em);
        em.persist(seller);
        em.flush();
        product.setSeller(seller);
        productRepository.saveAndFlush(product);
        Long sellerId = seller.getId();

        // Get all the productList where seller equals to sellerId
        defaultProductShouldBeFound("sellerId.equals=" + sellerId);

        // Get all the productList where seller equals to sellerId + 1
        defaultProductShouldNotBeFound("sellerId.equals=" + (sellerId + 1));
    }


    @Test
    @Transactional
    public void getAllProductsByCartsInIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        Cart cartsIn = CartResourceIT.createEntity(em);
        em.persist(cartsIn);
        em.flush();
        product.addCartsIn(cartsIn);
        productRepository.saveAndFlush(product);
        Long cartsInId = cartsIn.getId();

        // Get all the productList where cartsIn equals to cartsInId
        defaultProductShouldBeFound("cartsInId.equals=" + cartsInId);

        // Get all the productList where cartsIn equals to cartsInId + 1
        defaultProductShouldNotBeFound("cartsInId.equals=" + (cartsInId + 1));
    }


    @Test
    @Transactional
    public void getAllProductsByOrderedByIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        Customer orderedBy = CustomerResourceIT.createEntity(em);
        em.persist(orderedBy);
        em.flush();
        product.addOrderedBy(orderedBy);
        productRepository.saveAndFlush(product);
        Long orderedById = orderedBy.getId();

        // Get all the productList where orderedBy equals to orderedById
        defaultProductShouldBeFound("orderedById.equals=" + orderedById);

        // Get all the productList where orderedBy equals to orderedById + 1
        defaultProductShouldNotBeFound("orderedById.equals=" + (orderedById + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc.perform(get("/api/products?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].recommendeAgeGroup").value(hasItem(DEFAULT_RECOMMENDE_AGE_GROUP)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)));

        // Check, that the count call also returns 1
        restProductMockMvc.perform(get("/api/products/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc.perform(get("/api/products?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc.perform(get("/api/products/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProduct() throws Exception {
        // Initialize the database
        productService.save(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .recommendeAgeGroup(UPDATED_RECOMMENDE_AGE_GROUP)
            .category(UPDATED_CATEGORY);

        restProductMockMvc.perform(put("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedProduct)))
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getRecommendeAgeGroup()).isEqualTo(UPDATED_RECOMMENDE_AGE_GROUP);
        assertThat(testProduct.getCategory()).isEqualTo(UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void updateNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Create the Product

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(put("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProduct() throws Exception {
        // Initialize the database
        productService.save(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc.perform(delete("/api/products/{id}", product.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
