package com.beleykanych.amazon.web.rest;

import com.beleykanych.amazon.AmazonApp;
import com.beleykanych.amazon.domain.Customer;
import com.beleykanych.amazon.domain.Cart;
import com.beleykanych.amazon.domain.Payment;
import com.beleykanych.amazon.domain.Product;
import com.beleykanych.amazon.repository.CustomerRepository;
import com.beleykanych.amazon.service.CustomerService;
import com.beleykanych.amazon.service.dto.CustomerCriteria;
import com.beleykanych.amazon.service.CustomerQueryService;

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
 * Integration tests for the {@link CustomerResource} REST controller.
 */
@SpringBootTest(classes = AmazonApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CustomerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "+747-9245724258";
    private static final String UPDATED_PHONE = "+96-7305924082";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    @Autowired
    private CustomerRepository customerRepository;

    @Mock
    private CustomerRepository customerRepositoryMock;

    @Mock
    private CustomerService customerServiceMock;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerQueryService customerQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerMockMvc;

    private Customer customer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createEntity(EntityManager em) {
        Customer customer = new Customer()
            .name(DEFAULT_NAME)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS);
        return customer;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createUpdatedEntity(EntityManager em) {
        Customer customer = new Customer()
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS);
        return customer;
    }

    @BeforeEach
    public void initTest() {
        customer = createEntity(em);
    }

    @Test
    @Transactional
    public void createCustomer() throws Exception {
        int databaseSizeBeforeCreate = customerRepository.findAll().size();

        // Create the Customer
        restCustomerMockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(customer)))
            .andExpect(status().isCreated());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate + 1);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCustomer.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCustomer.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    public void createCustomerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = customerRepository.findAll().size();

        // Create the Customer with an existing ID
        customer.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerMockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(customer)))
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerRepository.findAll().size();
        // set the field null
        customer.setName(null);

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(customer)))
            .andExpect(status().isBadRequest());

        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCustomers() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList
        restCustomerMockMvc.perform(get("/api/customers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllCustomersWithEagerRelationshipsIsEnabled() throws Exception {
        when(customerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCustomerMockMvc.perform(get("/api/customers?eagerload=true"))
            .andExpect(status().isOk());

        verify(customerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllCustomersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(customerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCustomerMockMvc.perform(get("/api/customers?eagerload=true"))
            .andExpect(status().isOk());

        verify(customerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getCustomer() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get the customer
        restCustomerMockMvc.perform(get("/api/customers/{id}", customer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customer.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS));
    }


    @Test
    @Transactional
    public void getCustomersByIdFiltering() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        Long id = customer.getId();

        defaultCustomerShouldBeFound("id.equals=" + id);
        defaultCustomerShouldNotBeFound("id.notEquals=" + id);

        defaultCustomerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCustomerShouldNotBeFound("id.greaterThan=" + id);

        defaultCustomerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCustomerShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllCustomersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where name equals to DEFAULT_NAME
        defaultCustomerShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the customerList where name equals to UPDATED_NAME
        defaultCustomerShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCustomersByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where name not equals to DEFAULT_NAME
        defaultCustomerShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the customerList where name not equals to UPDATED_NAME
        defaultCustomerShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCustomersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCustomerShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the customerList where name equals to UPDATED_NAME
        defaultCustomerShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCustomersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where name is not null
        defaultCustomerShouldBeFound("name.specified=true");

        // Get all the customerList where name is null
        defaultCustomerShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllCustomersByNameContainsSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where name contains DEFAULT_NAME
        defaultCustomerShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the customerList where name contains UPDATED_NAME
        defaultCustomerShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCustomersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where name does not contain DEFAULT_NAME
        defaultCustomerShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the customerList where name does not contain UPDATED_NAME
        defaultCustomerShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllCustomersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone equals to DEFAULT_PHONE
        defaultCustomerShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the customerList where phone equals to UPDATED_PHONE
        defaultCustomerShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllCustomersByPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone not equals to DEFAULT_PHONE
        defaultCustomerShouldNotBeFound("phone.notEquals=" + DEFAULT_PHONE);

        // Get all the customerList where phone not equals to UPDATED_PHONE
        defaultCustomerShouldBeFound("phone.notEquals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllCustomersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultCustomerShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the customerList where phone equals to UPDATED_PHONE
        defaultCustomerShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllCustomersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone is not null
        defaultCustomerShouldBeFound("phone.specified=true");

        // Get all the customerList where phone is null
        defaultCustomerShouldNotBeFound("phone.specified=false");
    }
                @Test
    @Transactional
    public void getAllCustomersByPhoneContainsSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone contains DEFAULT_PHONE
        defaultCustomerShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the customerList where phone contains UPDATED_PHONE
        defaultCustomerShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllCustomersByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where phone does not contain DEFAULT_PHONE
        defaultCustomerShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the customerList where phone does not contain UPDATED_PHONE
        defaultCustomerShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }


    @Test
    @Transactional
    public void getAllCustomersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where address equals to DEFAULT_ADDRESS
        defaultCustomerShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the customerList where address equals to UPDATED_ADDRESS
        defaultCustomerShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllCustomersByAddressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where address not equals to DEFAULT_ADDRESS
        defaultCustomerShouldNotBeFound("address.notEquals=" + DEFAULT_ADDRESS);

        // Get all the customerList where address not equals to UPDATED_ADDRESS
        defaultCustomerShouldBeFound("address.notEquals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllCustomersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultCustomerShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the customerList where address equals to UPDATED_ADDRESS
        defaultCustomerShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllCustomersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where address is not null
        defaultCustomerShouldBeFound("address.specified=true");

        // Get all the customerList where address is null
        defaultCustomerShouldNotBeFound("address.specified=false");
    }
                @Test
    @Transactional
    public void getAllCustomersByAddressContainsSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where address contains DEFAULT_ADDRESS
        defaultCustomerShouldBeFound("address.contains=" + DEFAULT_ADDRESS);

        // Get all the customerList where address contains UPDATED_ADDRESS
        defaultCustomerShouldNotBeFound("address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllCustomersByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList where address does not contain DEFAULT_ADDRESS
        defaultCustomerShouldNotBeFound("address.doesNotContain=" + DEFAULT_ADDRESS);

        // Get all the customerList where address does not contain UPDATED_ADDRESS
        defaultCustomerShouldBeFound("address.doesNotContain=" + UPDATED_ADDRESS);
    }


    @Test
    @Transactional
    public void getAllCustomersByCartIsEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);
        Cart cart = CartResourceIT.createEntity(em);
        em.persist(cart);
        em.flush();
        customer.setCart(cart);
        customerRepository.saveAndFlush(customer);
        Long cartId = cart.getId();

        // Get all the customerList where cart equals to cartId
        defaultCustomerShouldBeFound("cartId.equals=" + cartId);

        // Get all the customerList where cart equals to cartId + 1
        defaultCustomerShouldNotBeFound("cartId.equals=" + (cartId + 1));
    }


    @Test
    @Transactional
    public void getAllCustomersByPaymentsIsEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);
        Payment payments = PaymentResourceIT.createEntity(em);
        em.persist(payments);
        em.flush();
        customer.addPayments(payments);
        customerRepository.saveAndFlush(customer);
        Long paymentsId = payments.getId();

        // Get all the customerList where payments equals to paymentsId
        defaultCustomerShouldBeFound("paymentsId.equals=" + paymentsId);

        // Get all the customerList where payments equals to paymentsId + 1
        defaultCustomerShouldNotBeFound("paymentsId.equals=" + (paymentsId + 1));
    }


    @Test
    @Transactional
    public void getAllCustomersByOrderedProductsIsEqualToSomething() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);
        Product orderedProducts = ProductResourceIT.createEntity(em);
        em.persist(orderedProducts);
        em.flush();
        customer.addOrderedProducts(orderedProducts);
        customerRepository.saveAndFlush(customer);
        Long orderedProductsId = orderedProducts.getId();

        // Get all the customerList where orderedProducts equals to orderedProductsId
        defaultCustomerShouldBeFound("orderedProductsId.equals=" + orderedProductsId);

        // Get all the customerList where orderedProducts equals to orderedProductsId + 1
        defaultCustomerShouldNotBeFound("orderedProductsId.equals=" + (orderedProductsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerShouldBeFound(String filter) throws Exception {
        restCustomerMockMvc.perform(get("/api/customers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));

        // Check, that the count call also returns 1
        restCustomerMockMvc.perform(get("/api/customers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCustomerShouldNotBeFound(String filter) throws Exception {
        restCustomerMockMvc.perform(get("/api/customers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCustomerMockMvc.perform(get("/api/customers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingCustomer() throws Exception {
        // Get the customer
        restCustomerMockMvc.perform(get("/api/customers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCustomer() throws Exception {
        // Initialize the database
        customerService.save(customer);

        int databaseSizeBeforeUpdate = customerRepository.findAll().size();

        // Update the customer
        Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
        // Disconnect from session so that the updates on updatedCustomer are not directly saved in db
        em.detach(updatedCustomer);
        updatedCustomer
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS);

        restCustomerMockMvc.perform(put("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCustomer)))
            .andExpect(status().isOk());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCustomer.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCustomer.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void updateNonExistingCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();

        // Create the Customer

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc.perform(put("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(customer)))
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCustomer() throws Exception {
        // Initialize the database
        customerService.save(customer);

        int databaseSizeBeforeDelete = customerRepository.findAll().size();

        // Delete the customer
        restCustomerMockMvc.perform(delete("/api/customers/{id}", customer.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
