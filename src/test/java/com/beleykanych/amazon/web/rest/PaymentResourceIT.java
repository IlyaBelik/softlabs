package com.beleykanych.amazon.web.rest;

import com.beleykanych.amazon.AmazonApp;
import com.beleykanych.amazon.domain.Payment;
import com.beleykanych.amazon.domain.Customer;
import com.beleykanych.amazon.repository.PaymentRepository;
import com.beleykanych.amazon.service.PaymentService;
import com.beleykanych.amazon.service.dto.PaymentCriteria;
import com.beleykanych.amazon.service.PaymentQueryService;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.beleykanych.amazon.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@SpringBootTest(classes = AmazonApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class PaymentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CARD_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CARD_NUMBER = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_SUCCESSFUL = false;
    private static final Boolean UPDATED_SUCCESSFUL = true;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentQueryService paymentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMockMvc;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
            .name(DEFAULT_NAME)
            .cardType(DEFAULT_CARD_TYPE)
            .cardNumber(DEFAULT_CARD_NUMBER)
            .dateTime(DEFAULT_DATE_TIME)
            .successful(DEFAULT_SUCCESSFUL);
        return payment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment()
            .name(UPDATED_NAME)
            .cardType(UPDATED_CARD_TYPE)
            .cardNumber(UPDATED_CARD_NUMBER)
            .dateTime(UPDATED_DATE_TIME)
            .successful(UPDATED_SUCCESSFUL);
        return payment;
    }

    @BeforeEach
    public void initTest() {
        payment = createEntity(em);
    }

    @Test
    @Transactional
    public void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();

        // Create the Payment
        restPaymentMockMvc.perform(post("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isCreated());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPayment.getCardType()).isEqualTo(DEFAULT_CARD_TYPE);
        assertThat(testPayment.getCardNumber()).isEqualTo(DEFAULT_CARD_NUMBER);
        assertThat(testPayment.getDateTime()).isEqualTo(DEFAULT_DATE_TIME);
        assertThat(testPayment.isSuccessful()).isEqualTo(DEFAULT_SUCCESSFUL);
    }

    @Test
    @Transactional
    public void createPaymentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();

        // Create the Payment with an existing ID
        payment.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMockMvc.perform(post("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllPayments() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList
        restPaymentMockMvc.perform(get("/api/payments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].cardType").value(hasItem(DEFAULT_CARD_TYPE)))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].dateTime").value(hasItem(sameInstant(DEFAULT_DATE_TIME))))
            .andExpect(jsonPath("$.[*].successful").value(hasItem(DEFAULT_SUCCESSFUL.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get the payment
        restPaymentMockMvc.perform(get("/api/payments/{id}", payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.cardType").value(DEFAULT_CARD_TYPE))
            .andExpect(jsonPath("$.cardNumber").value(DEFAULT_CARD_NUMBER))
            .andExpect(jsonPath("$.dateTime").value(sameInstant(DEFAULT_DATE_TIME)))
            .andExpect(jsonPath("$.successful").value(DEFAULT_SUCCESSFUL.booleanValue()));
    }


    @Test
    @Transactional
    public void getPaymentsByIdFiltering() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        Long id = payment.getId();

        defaultPaymentShouldBeFound("id.equals=" + id);
        defaultPaymentShouldNotBeFound("id.notEquals=" + id);

        defaultPaymentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPaymentShouldNotBeFound("id.greaterThan=" + id);

        defaultPaymentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPaymentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPaymentsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where name equals to DEFAULT_NAME
        defaultPaymentShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the paymentList where name equals to UPDATED_NAME
        defaultPaymentShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where name not equals to DEFAULT_NAME
        defaultPaymentShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the paymentList where name not equals to UPDATED_NAME
        defaultPaymentShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPaymentShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the paymentList where name equals to UPDATED_NAME
        defaultPaymentShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where name is not null
        defaultPaymentShouldBeFound("name.specified=true");

        // Get all the paymentList where name is null
        defaultPaymentShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllPaymentsByNameContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where name contains DEFAULT_NAME
        defaultPaymentShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the paymentList where name contains UPDATED_NAME
        defaultPaymentShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where name does not contain DEFAULT_NAME
        defaultPaymentShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the paymentList where name does not contain UPDATED_NAME
        defaultPaymentShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllPaymentsByCardTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardType equals to DEFAULT_CARD_TYPE
        defaultPaymentShouldBeFound("cardType.equals=" + DEFAULT_CARD_TYPE);

        // Get all the paymentList where cardType equals to UPDATED_CARD_TYPE
        defaultPaymentShouldNotBeFound("cardType.equals=" + UPDATED_CARD_TYPE);
    }

    @Test
    @Transactional
    public void getAllPaymentsByCardTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardType not equals to DEFAULT_CARD_TYPE
        defaultPaymentShouldNotBeFound("cardType.notEquals=" + DEFAULT_CARD_TYPE);

        // Get all the paymentList where cardType not equals to UPDATED_CARD_TYPE
        defaultPaymentShouldBeFound("cardType.notEquals=" + UPDATED_CARD_TYPE);
    }

    @Test
    @Transactional
    public void getAllPaymentsByCardTypeIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardType in DEFAULT_CARD_TYPE or UPDATED_CARD_TYPE
        defaultPaymentShouldBeFound("cardType.in=" + DEFAULT_CARD_TYPE + "," + UPDATED_CARD_TYPE);

        // Get all the paymentList where cardType equals to UPDATED_CARD_TYPE
        defaultPaymentShouldNotBeFound("cardType.in=" + UPDATED_CARD_TYPE);
    }

    @Test
    @Transactional
    public void getAllPaymentsByCardTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardType is not null
        defaultPaymentShouldBeFound("cardType.specified=true");

        // Get all the paymentList where cardType is null
        defaultPaymentShouldNotBeFound("cardType.specified=false");
    }
                @Test
    @Transactional
    public void getAllPaymentsByCardTypeContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardType contains DEFAULT_CARD_TYPE
        defaultPaymentShouldBeFound("cardType.contains=" + DEFAULT_CARD_TYPE);

        // Get all the paymentList where cardType contains UPDATED_CARD_TYPE
        defaultPaymentShouldNotBeFound("cardType.contains=" + UPDATED_CARD_TYPE);
    }

    @Test
    @Transactional
    public void getAllPaymentsByCardTypeNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardType does not contain DEFAULT_CARD_TYPE
        defaultPaymentShouldNotBeFound("cardType.doesNotContain=" + DEFAULT_CARD_TYPE);

        // Get all the paymentList where cardType does not contain UPDATED_CARD_TYPE
        defaultPaymentShouldBeFound("cardType.doesNotContain=" + UPDATED_CARD_TYPE);
    }


    @Test
    @Transactional
    public void getAllPaymentsByCardNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardNumber equals to DEFAULT_CARD_NUMBER
        defaultPaymentShouldBeFound("cardNumber.equals=" + DEFAULT_CARD_NUMBER);

        // Get all the paymentList where cardNumber equals to UPDATED_CARD_NUMBER
        defaultPaymentShouldNotBeFound("cardNumber.equals=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPaymentsByCardNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardNumber not equals to DEFAULT_CARD_NUMBER
        defaultPaymentShouldNotBeFound("cardNumber.notEquals=" + DEFAULT_CARD_NUMBER);

        // Get all the paymentList where cardNumber not equals to UPDATED_CARD_NUMBER
        defaultPaymentShouldBeFound("cardNumber.notEquals=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPaymentsByCardNumberIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardNumber in DEFAULT_CARD_NUMBER or UPDATED_CARD_NUMBER
        defaultPaymentShouldBeFound("cardNumber.in=" + DEFAULT_CARD_NUMBER + "," + UPDATED_CARD_NUMBER);

        // Get all the paymentList where cardNumber equals to UPDATED_CARD_NUMBER
        defaultPaymentShouldNotBeFound("cardNumber.in=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPaymentsByCardNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardNumber is not null
        defaultPaymentShouldBeFound("cardNumber.specified=true");

        // Get all the paymentList where cardNumber is null
        defaultPaymentShouldNotBeFound("cardNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllPaymentsByCardNumberContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardNumber contains DEFAULT_CARD_NUMBER
        defaultPaymentShouldBeFound("cardNumber.contains=" + DEFAULT_CARD_NUMBER);

        // Get all the paymentList where cardNumber contains UPDATED_CARD_NUMBER
        defaultPaymentShouldNotBeFound("cardNumber.contains=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPaymentsByCardNumberNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where cardNumber does not contain DEFAULT_CARD_NUMBER
        defaultPaymentShouldNotBeFound("cardNumber.doesNotContain=" + DEFAULT_CARD_NUMBER);

        // Get all the paymentList where cardNumber does not contain UPDATED_CARD_NUMBER
        defaultPaymentShouldBeFound("cardNumber.doesNotContain=" + UPDATED_CARD_NUMBER);
    }


    @Test
    @Transactional
    public void getAllPaymentsByDateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where dateTime equals to DEFAULT_DATE_TIME
        defaultPaymentShouldBeFound("dateTime.equals=" + DEFAULT_DATE_TIME);

        // Get all the paymentList where dateTime equals to UPDATED_DATE_TIME
        defaultPaymentShouldNotBeFound("dateTime.equals=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByDateTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where dateTime not equals to DEFAULT_DATE_TIME
        defaultPaymentShouldNotBeFound("dateTime.notEquals=" + DEFAULT_DATE_TIME);

        // Get all the paymentList where dateTime not equals to UPDATED_DATE_TIME
        defaultPaymentShouldBeFound("dateTime.notEquals=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByDateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where dateTime in DEFAULT_DATE_TIME or UPDATED_DATE_TIME
        defaultPaymentShouldBeFound("dateTime.in=" + DEFAULT_DATE_TIME + "," + UPDATED_DATE_TIME);

        // Get all the paymentList where dateTime equals to UPDATED_DATE_TIME
        defaultPaymentShouldNotBeFound("dateTime.in=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByDateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where dateTime is not null
        defaultPaymentShouldBeFound("dateTime.specified=true");

        // Get all the paymentList where dateTime is null
        defaultPaymentShouldNotBeFound("dateTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllPaymentsByDateTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where dateTime is greater than or equal to DEFAULT_DATE_TIME
        defaultPaymentShouldBeFound("dateTime.greaterThanOrEqual=" + DEFAULT_DATE_TIME);

        // Get all the paymentList where dateTime is greater than or equal to UPDATED_DATE_TIME
        defaultPaymentShouldNotBeFound("dateTime.greaterThanOrEqual=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByDateTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where dateTime is less than or equal to DEFAULT_DATE_TIME
        defaultPaymentShouldBeFound("dateTime.lessThanOrEqual=" + DEFAULT_DATE_TIME);

        // Get all the paymentList where dateTime is less than or equal to SMALLER_DATE_TIME
        defaultPaymentShouldNotBeFound("dateTime.lessThanOrEqual=" + SMALLER_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByDateTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where dateTime is less than DEFAULT_DATE_TIME
        defaultPaymentShouldNotBeFound("dateTime.lessThan=" + DEFAULT_DATE_TIME);

        // Get all the paymentList where dateTime is less than UPDATED_DATE_TIME
        defaultPaymentShouldBeFound("dateTime.lessThan=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPaymentsByDateTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where dateTime is greater than DEFAULT_DATE_TIME
        defaultPaymentShouldNotBeFound("dateTime.greaterThan=" + DEFAULT_DATE_TIME);

        // Get all the paymentList where dateTime is greater than SMALLER_DATE_TIME
        defaultPaymentShouldBeFound("dateTime.greaterThan=" + SMALLER_DATE_TIME);
    }


    @Test
    @Transactional
    public void getAllPaymentsBySuccessfulIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where successful equals to DEFAULT_SUCCESSFUL
        defaultPaymentShouldBeFound("successful.equals=" + DEFAULT_SUCCESSFUL);

        // Get all the paymentList where successful equals to UPDATED_SUCCESSFUL
        defaultPaymentShouldNotBeFound("successful.equals=" + UPDATED_SUCCESSFUL);
    }

    @Test
    @Transactional
    public void getAllPaymentsBySuccessfulIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where successful not equals to DEFAULT_SUCCESSFUL
        defaultPaymentShouldNotBeFound("successful.notEquals=" + DEFAULT_SUCCESSFUL);

        // Get all the paymentList where successful not equals to UPDATED_SUCCESSFUL
        defaultPaymentShouldBeFound("successful.notEquals=" + UPDATED_SUCCESSFUL);
    }

    @Test
    @Transactional
    public void getAllPaymentsBySuccessfulIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where successful in DEFAULT_SUCCESSFUL or UPDATED_SUCCESSFUL
        defaultPaymentShouldBeFound("successful.in=" + DEFAULT_SUCCESSFUL + "," + UPDATED_SUCCESSFUL);

        // Get all the paymentList where successful equals to UPDATED_SUCCESSFUL
        defaultPaymentShouldNotBeFound("successful.in=" + UPDATED_SUCCESSFUL);
    }

    @Test
    @Transactional
    public void getAllPaymentsBySuccessfulIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where successful is not null
        defaultPaymentShouldBeFound("successful.specified=true");

        // Get all the paymentList where successful is null
        defaultPaymentShouldNotBeFound("successful.specified=false");
    }

    @Test
    @Transactional
    public void getAllPaymentsByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);
        Customer customer = CustomerResourceIT.createEntity(em);
        em.persist(customer);
        em.flush();
        payment.setCustomer(customer);
        paymentRepository.saveAndFlush(payment);
        Long customerId = customer.getId();

        // Get all the paymentList where customer equals to customerId
        defaultPaymentShouldBeFound("customerId.equals=" + customerId);

        // Get all the paymentList where customer equals to customerId + 1
        defaultPaymentShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaymentShouldBeFound(String filter) throws Exception {
        restPaymentMockMvc.perform(get("/api/payments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].cardType").value(hasItem(DEFAULT_CARD_TYPE)))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].dateTime").value(hasItem(sameInstant(DEFAULT_DATE_TIME))))
            .andExpect(jsonPath("$.[*].successful").value(hasItem(DEFAULT_SUCCESSFUL.booleanValue())));

        // Check, that the count call also returns 1
        restPaymentMockMvc.perform(get("/api/payments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaymentShouldNotBeFound(String filter) throws Exception {
        restPaymentMockMvc.perform(get("/api/payments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaymentMockMvc.perform(get("/api/payments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPayment() throws Exception {
        // Get the payment
        restPaymentMockMvc.perform(get("/api/payments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePayment() throws Exception {
        // Initialize the database
        paymentService.save(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).get();
        // Disconnect from session so that the updates on updatedPayment are not directly saved in db
        em.detach(updatedPayment);
        updatedPayment
            .name(UPDATED_NAME)
            .cardType(UPDATED_CARD_TYPE)
            .cardNumber(UPDATED_CARD_NUMBER)
            .dateTime(UPDATED_DATE_TIME)
            .successful(UPDATED_SUCCESSFUL);

        restPaymentMockMvc.perform(put("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPayment)))
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPayment.getCardType()).isEqualTo(UPDATED_CARD_TYPE);
        assertThat(testPayment.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testPayment.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testPayment.isSuccessful()).isEqualTo(UPDATED_SUCCESSFUL);
    }

    @Test
    @Transactional
    public void updateNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Create the Payment

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc.perform(put("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePayment() throws Exception {
        // Initialize the database
        paymentService.save(payment);

        int databaseSizeBeforeDelete = paymentRepository.findAll().size();

        // Delete the payment
        restPaymentMockMvc.perform(delete("/api/payments/{id}", payment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
