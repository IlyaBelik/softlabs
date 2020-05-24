package com.beleykanych.amazon.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "recommende_age_group")
    private String recommendeAgeGroup;

    @Column(name = "category")
    private String category;

    @ManyToOne
    @JsonIgnoreProperties("products")
    private Seller seller;

    @ManyToMany(mappedBy = "productsIns")
    @JsonIgnore
    private Set<Cart> cartsIns = new HashSet<>();

    @ManyToMany(mappedBy = "orderedProducts")
    @JsonIgnore
    private Set<Customer> orderedBies = new HashSet<>();

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

    public Product name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecommendeAgeGroup() {
        return recommendeAgeGroup;
    }

    public Product recommendeAgeGroup(String recommendeAgeGroup) {
        this.recommendeAgeGroup = recommendeAgeGroup;
        return this;
    }

    public void setRecommendeAgeGroup(String recommendeAgeGroup) {
        this.recommendeAgeGroup = recommendeAgeGroup;
    }

    public String getCategory() {
        return category;
    }

    public Product category(String category) {
        this.category = category;
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Seller getSeller() {
        return seller;
    }

    public Product seller(Seller seller) {
        this.seller = seller;
        return this;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Set<Cart> getCartsIns() {
        return cartsIns;
    }

    public Product cartsIns(Set<Cart> carts) {
        this.cartsIns = carts;
        return this;
    }

    public Product addCartsIn(Cart cart) {
        this.cartsIns.add(cart);
        cart.getProductsIns().add(this);
        return this;
    }

    public Product removeCartsIn(Cart cart) {
        this.cartsIns.remove(cart);
        cart.getProductsIns().remove(this);
        return this;
    }

    public void setCartsIns(Set<Cart> carts) {
        this.cartsIns = carts;
    }

    public Set<Customer> getOrderedBies() {
        return orderedBies;
    }

    public Product orderedBies(Set<Customer> customers) {
        this.orderedBies = customers;
        return this;
    }

    public Product addOrderedBy(Customer customer) {
        this.orderedBies.add(customer);
        customer.getOrderedProducts().add(this);
        return this;
    }

    public Product removeOrderedBy(Customer customer) {
        this.orderedBies.remove(customer);
        customer.getOrderedProducts().remove(this);
        return this;
    }

    public void setOrderedBies(Set<Customer> customers) {
        this.orderedBies = customers;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", recommendeAgeGroup='" + getRecommendeAgeGroup() + "'" +
            ", category='" + getCategory() + "'" +
            "}";
    }
}
