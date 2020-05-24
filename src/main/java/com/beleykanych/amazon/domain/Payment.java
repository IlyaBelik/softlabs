package com.beleykanych.amazon.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.time.ZonedDateTime;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "date_time")
    private ZonedDateTime dateTime;

    @Column(name = "successful")
    private Boolean successful;

    @ManyToOne
    @JsonIgnoreProperties("payments")
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Payment name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardType() {
        return cardType;
    }

    public Payment cardType(String cardType) {
        this.cardType = cardType;
        return this;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Payment cardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public Payment dateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean isSuccessful() {
        return successful;
    }

    public Payment successful(Boolean successful) {
        this.successful = successful;
        return this;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Payment customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return id != null && id.equals(((Payment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", cardType='" + getCardType() + "'" +
            ", cardNumber='" + getCardNumber() + "'" +
            ", dateTime='" + getDateTime() + "'" +
            ", successful='" + isSuccessful() + "'" +
            "}";
    }
}
