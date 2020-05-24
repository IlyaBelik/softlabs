package com.beleykanych.amazon.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Cart.
 */
@Entity
@Table(name = "cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "products_number")
    private Integer productsNumber;

    @Column(name = "total_price")
    private Double totalPrice;

    @ManyToMany
    @JoinTable(name = "cart_products_in",
               joinColumns = @JoinColumn(name = "cart_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "products_in_id", referencedColumnName = "id"))
    private Set<Product> productsIns = new HashSet<>();

    @OneToOne(mappedBy = "cart")
    @JsonIgnore
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProductsNumber() {
        return productsNumber;
    }

    public Cart productsNumber(Integer productsNumber) {
        this.productsNumber = productsNumber;
        return this;
    }

    public void setProductsNumber(Integer productsNumber) {
        this.productsNumber = productsNumber;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public Cart totalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Set<Product> getProductsIns() {
        return productsIns;
    }

    public Cart productsIns(Set<Product> products) {
        this.productsIns = products;
        return this;
    }

    public Cart addProductsIn(Product product) {
        this.productsIns.add(product);
        product.getCartsIns().add(this);
        return this;
    }

    public Cart removeProductsIn(Product product) {
        this.productsIns.remove(product);
        product.getCartsIns().remove(this);
        return this;
    }

    public void setProductsIns(Set<Product> products) {
        this.productsIns = products;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Cart customer(Customer customer) {
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
        if (!(o instanceof Cart)) {
            return false;
        }
        return id != null && id.equals(((Cart) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Cart{" +
            "id=" + getId() +
            ", productsNumber=" + getProductsNumber() +
            ", totalPrice=" + getTotalPrice() +
            "}";
    }
}
