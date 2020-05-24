package com.beleykanych.amazon.web.rest;

import com.beleykanych.amazon.AmazonApp;
import com.beleykanych.amazon.domain.Cart;
import com.beleykanych.amazon.domain.Product;
import com.beleykanych.amazon.domain.Customer;
import com.beleykanych.amazon.repository.CartRepository;
import com.beleykanych.amazon.service.CartService;
import com.beleykanych.amazon.service.dto.CartCriteria;
import com.beleykanych.amazon.service.CartQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CartResource} REST controller.
 */
@SpringBootTest(classes = AmazonApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CartResourceIT {

    private static final Integer DEFAULT_PRODUCTS_NUMBER = 1;
    private static final Integer UPDATED_PRODUCTS_NUMBER = 2;
    private static final Integer SMALLER_PRODUCTS_NUMBER = 1 - 1;

    private static final Double DEFAULT_TOTAL_PRICE = 1D;
    private static final Double UPDATED_TOTAL_PRICE = 2D;
    private static final Double SMALLER_TOTAL_PRICE = 1D - 1D;

    @Autowired
    private CartRepository cartRepository;

    @Mock
    private CartRepository cartRepositoryMock;

    @Mock
    private CartService cartServiceMock;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartQueryService cartQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartMockMvc;

    private Cart cart;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cart createEntity(EntityManager em) {
        Cart cart = new Cart()
            .productsNumber(DEFAULT_PRODUCTS_NUMBER)
            .totalPrice(DEFAULT_TOTAL_PRICE);
        return cart;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cart createUpdatedEntity(EntityManager em) {
        Cart cart = new Cart()
            .productsNumber(UPDATED_PRODUCTS_NUMBER)
            .totalPrice(UPDATED_TOTAL_PRICE);
        return cart;
    }

    @BeforeEach
    public void initTest() {
        cart = createEntity(em);
    }

    @Test
    @Transactional
    public void createCart() throws Exception {
        int databaseSizeBeforeCreate = cartRepository.findAll().size();

        // Create the Cart
        restCartMockMvc.perform(post("/api/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(cart)))
            .andExpect(status().isCreated());

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeCreate + 1);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getProductsNumber()).isEqualTo(DEFAULT_PRODUCTS_NUMBER);
        assertThat(testCart.getTotalPrice()).isEqualTo(DEFAULT_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void createCartWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cartRepository.findAll().size();

        // Create the Cart with an existing ID
        cart.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartMockMvc.perform(post("/api/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(cart)))
            .andExpect(status().isBadRequest());

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllCarts() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList
        restCartMockMvc.perform(get("/api/carts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cart.getId().intValue())))
            .andExpect(jsonPath("$.[*].productsNumber").value(hasItem(DEFAULT_PRODUCTS_NUMBER)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.doubleValue())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllCartsWithEagerRelationshipsIsEnabled() throws Exception {
        when(cartServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCartMockMvc.perform(get("/api/carts?eagerload=true"))
            .andExpect(status().isOk());

        verify(cartServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllCartsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(cartServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCartMockMvc.perform(get("/api/carts?eagerload=true"))
            .andExpect(status().isOk());

        verify(cartServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getCart() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get the cart
        restCartMockMvc.perform(get("/api/carts/{id}", cart.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cart.getId().intValue()))
            .andExpect(jsonPath("$.productsNumber").value(DEFAULT_PRODUCTS_NUMBER))
            .andExpect(jsonPath("$.totalPrice").value(DEFAULT_TOTAL_PRICE.doubleValue()));
    }


    @Test
    @Transactional
    public void getCartsByIdFiltering() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        Long id = cart.getId();

        defaultCartShouldBeFound("id.equals=" + id);
        defaultCartShouldNotBeFound("id.notEquals=" + id);

        defaultCartShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCartShouldNotBeFound("id.greaterThan=" + id);

        defaultCartShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCartShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllCartsByProductsNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where productsNumber equals to DEFAULT_PRODUCTS_NUMBER
        defaultCartShouldBeFound("productsNumber.equals=" + DEFAULT_PRODUCTS_NUMBER);

        // Get all the cartList where productsNumber equals to UPDATED_PRODUCTS_NUMBER
        defaultCartShouldNotBeFound("productsNumber.equals=" + UPDATED_PRODUCTS_NUMBER);
    }

    @Test
    @Transactional
    public void getAllCartsByProductsNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where productsNumber not equals to DEFAULT_PRODUCTS_NUMBER
        defaultCartShouldNotBeFound("productsNumber.notEquals=" + DEFAULT_PRODUCTS_NUMBER);

        // Get all the cartList where productsNumber not equals to UPDATED_PRODUCTS_NUMBER
        defaultCartShouldBeFound("productsNumber.notEquals=" + UPDATED_PRODUCTS_NUMBER);
    }

    @Test
    @Transactional
    public void getAllCartsByProductsNumberIsInShouldWork() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where productsNumber in DEFAULT_PRODUCTS_NUMBER or UPDATED_PRODUCTS_NUMBER
        defaultCartShouldBeFound("productsNumber.in=" + DEFAULT_PRODUCTS_NUMBER + "," + UPDATED_PRODUCTS_NUMBER);

        // Get all the cartList where productsNumber equals to UPDATED_PRODUCTS_NUMBER
        defaultCartShouldNotBeFound("productsNumber.in=" + UPDATED_PRODUCTS_NUMBER);
    }

    @Test
    @Transactional
    public void getAllCartsByProductsNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where productsNumber is not null
        defaultCartShouldBeFound("productsNumber.specified=true");

        // Get all the cartList where productsNumber is null
        defaultCartShouldNotBeFound("productsNumber.specified=false");
    }

    @Test
    @Transactional
    public void getAllCartsByProductsNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where productsNumber is greater than or equal to DEFAULT_PRODUCTS_NUMBER
        defaultCartShouldBeFound("productsNumber.greaterThanOrEqual=" + DEFAULT_PRODUCTS_NUMBER);

        // Get all the cartList where productsNumber is greater than or equal to UPDATED_PRODUCTS_NUMBER
        defaultCartShouldNotBeFound("productsNumber.greaterThanOrEqual=" + UPDATED_PRODUCTS_NUMBER);
    }

    @Test
    @Transactional
    public void getAllCartsByProductsNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where productsNumber is less than or equal to DEFAULT_PRODUCTS_NUMBER
        defaultCartShouldBeFound("productsNumber.lessThanOrEqual=" + DEFAULT_PRODUCTS_NUMBER);

        // Get all the cartList where productsNumber is less than or equal to SMALLER_PRODUCTS_NUMBER
        defaultCartShouldNotBeFound("productsNumber.lessThanOrEqual=" + SMALLER_PRODUCTS_NUMBER);
    }

    @Test
    @Transactional
    public void getAllCartsByProductsNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where productsNumber is less than DEFAULT_PRODUCTS_NUMBER
        defaultCartShouldNotBeFound("productsNumber.lessThan=" + DEFAULT_PRODUCTS_NUMBER);

        // Get all the cartList where productsNumber is less than UPDATED_PRODUCTS_NUMBER
        defaultCartShouldBeFound("productsNumber.lessThan=" + UPDATED_PRODUCTS_NUMBER);
    }

    @Test
    @Transactional
    public void getAllCartsByProductsNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where productsNumber is greater than DEFAULT_PRODUCTS_NUMBER
        defaultCartShouldNotBeFound("productsNumber.greaterThan=" + DEFAULT_PRODUCTS_NUMBER);

        // Get all the cartList where productsNumber is greater than SMALLER_PRODUCTS_NUMBER
        defaultCartShouldBeFound("productsNumber.greaterThan=" + SMALLER_PRODUCTS_NUMBER);
    }


    @Test
    @Transactional
    public void getAllCartsByTotalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where totalPrice equals to DEFAULT_TOTAL_PRICE
        defaultCartShouldBeFound("totalPrice.equals=" + DEFAULT_TOTAL_PRICE);

        // Get all the cartList where totalPrice equals to UPDATED_TOTAL_PRICE
        defaultCartShouldNotBeFound("totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllCartsByTotalPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where totalPrice not equals to DEFAULT_TOTAL_PRICE
        defaultCartShouldNotBeFound("totalPrice.notEquals=" + DEFAULT_TOTAL_PRICE);

        // Get all the cartList where totalPrice not equals to UPDATED_TOTAL_PRICE
        defaultCartShouldBeFound("totalPrice.notEquals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllCartsByTotalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where totalPrice in DEFAULT_TOTAL_PRICE or UPDATED_TOTAL_PRICE
        defaultCartShouldBeFound("totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE);

        // Get all the cartList where totalPrice equals to UPDATED_TOTAL_PRICE
        defaultCartShouldNotBeFound("totalPrice.in=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllCartsByTotalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where totalPrice is not null
        defaultCartShouldBeFound("totalPrice.specified=true");

        // Get all the cartList where totalPrice is null
        defaultCartShouldNotBeFound("totalPrice.specified=false");
    }

    @Test
    @Transactional
    public void getAllCartsByTotalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where totalPrice is greater than or equal to DEFAULT_TOTAL_PRICE
        defaultCartShouldBeFound("totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE);

        // Get all the cartList where totalPrice is greater than or equal to UPDATED_TOTAL_PRICE
        defaultCartShouldNotBeFound("totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllCartsByTotalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where totalPrice is less than or equal to DEFAULT_TOTAL_PRICE
        defaultCartShouldBeFound("totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE);

        // Get all the cartList where totalPrice is less than or equal to SMALLER_TOTAL_PRICE
        defaultCartShouldNotBeFound("totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllCartsByTotalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where totalPrice is less than DEFAULT_TOTAL_PRICE
        defaultCartShouldNotBeFound("totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);

        // Get all the cartList where totalPrice is less than UPDATED_TOTAL_PRICE
        defaultCartShouldBeFound("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void getAllCartsByTotalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);

        // Get all the cartList where totalPrice is greater than DEFAULT_TOTAL_PRICE
        defaultCartShouldNotBeFound("totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);

        // Get all the cartList where totalPrice is greater than SMALLER_TOTAL_PRICE
        defaultCartShouldBeFound("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE);
    }


    @Test
    @Transactional
    public void getAllCartsByProductsInIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);
        Product productsIn = ProductResourceIT.createEntity(em);
        em.persist(productsIn);
        em.flush();
        cart.addProductsIn(productsIn);
        cartRepository.saveAndFlush(cart);
        Long productsInId = productsIn.getId();

        // Get all the cartList where productsIn equals to productsInId
        defaultCartShouldBeFound("productsInId.equals=" + productsInId);

        // Get all the cartList where productsIn equals to productsInId + 1
        defaultCartShouldNotBeFound("productsInId.equals=" + (productsInId + 1));
    }


    @Test
    @Transactional
    public void getAllCartsByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        cartRepository.saveAndFlush(cart);
        Customer customer = CustomerResourceIT.createEntity(em);
        em.persist(customer);
        em.flush();
        cart.setCustomer(customer);
        customer.setCart(cart);
        cartRepository.saveAndFlush(cart);
        Long customerId = customer.getId();

        // Get all the cartList where customer equals to customerId
        defaultCartShouldBeFound("customerId.equals=" + customerId);

        // Get all the cartList where customer equals to customerId + 1
        defaultCartShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCartShouldBeFound(String filter) throws Exception {
        restCartMockMvc.perform(get("/api/carts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cart.getId().intValue())))
            .andExpect(jsonPath("$.[*].productsNumber").value(hasItem(DEFAULT_PRODUCTS_NUMBER)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.doubleValue())));

        // Check, that the count call also returns 1
        restCartMockMvc.perform(get("/api/carts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCartShouldNotBeFound(String filter) throws Exception {
        restCartMockMvc.perform(get("/api/carts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCartMockMvc.perform(get("/api/carts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingCart() throws Exception {
        // Get the cart
        restCartMockMvc.perform(get("/api/carts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCart() throws Exception {
        // Initialize the database
        cartService.save(cart);

        int databaseSizeBeforeUpdate = cartRepository.findAll().size();

        // Update the cart
        Cart updatedCart = cartRepository.findById(cart.getId()).get();
        // Disconnect from session so that the updates on updatedCart are not directly saved in db
        em.detach(updatedCart);
        updatedCart
            .productsNumber(UPDATED_PRODUCTS_NUMBER)
            .totalPrice(UPDATED_TOTAL_PRICE);

        restCartMockMvc.perform(put("/api/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCart)))
            .andExpect(status().isOk());

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getProductsNumber()).isEqualTo(UPDATED_PRODUCTS_NUMBER);
        assertThat(testCart.getTotalPrice()).isEqualTo(UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    public void updateNonExistingCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().size();

        // Create the Cart

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartMockMvc.perform(put("/api/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(cart)))
            .andExpect(status().isBadRequest());

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCart() throws Exception {
        // Initialize the database
        cartService.save(cart);

        int databaseSizeBeforeDelete = cartRepository.findAll().size();

        // Delete the cart
        restCartMockMvc.perform(delete("/api/carts/{id}", cart.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cart> cartList = cartRepository.findAll();
        assertThat(cartList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
