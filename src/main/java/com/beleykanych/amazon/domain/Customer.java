package com.beleykanych.amazon.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Customer.
 */
@Entity
@Table(name = "customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$")
    @Column(name = "phone", unique = true)
    private String phone;

    @Size(max = 100)
    @Column(name = "address", length = 100)
    private String address;

    @OneToOne
    @JoinColumn(unique = true)
    private Cart cart;

    @OneToMany(mappedBy = "customer")
    private Set<Payment> payments = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "customer_ordered_products",
               joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "ordered_products_id", referencedColumnName = "id"))
    private Set<Product> orderedProducts = new HashSet<>();

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

    public Customer name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public Customer phone(String phone) {
        this.phone = phone;
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public Customer address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Cart getCart() {
        return cart;
    }

    public Customer cart(Cart cart) {
        this.cart = cart;
        return this;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public Customer payments(Set<Payment> payments) {
        this.payments = payments;
        return this;
    }

    public Customer addPayments(Payment payment) {
        this.payments.add(payment);
        payment.setCustomer(this);
        return this;
    }

    public Customer removePayments(Payment payment) {
        this.payments.remove(payment);
        payment.setCustomer(null);
        return this;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    public Set<Product> getOrderedProducts() {
        return orderedProducts;
    }

    public Customer orderedProducts(Set<Product> products) {
        this.orderedProducts = products;
        return this;
    }

    public Customer addOrderedProducts(Product product) {
        this.orderedProducts.add(product);
        product.getOrderedBies().add(this);
        return this;
    }

    public Customer removeOrderedProducts(Product product) {
        this.orderedProducts.remove(product);
        product.getOrderedBies().remove(this);
        return this;
    }

    public void setOrderedProducts(Set<Product> products) {
        this.orderedProducts = products;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }
}
