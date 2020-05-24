package com.beleykanych.amazon.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.beleykanych.amazon.domain.Cart} entity. This class is used
 * in {@link com.beleykanych.amazon.web.rest.CartResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /carts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CartCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter productsNumber;

    private DoubleFilter totalPrice;

    private LongFilter productsInId;

    private LongFilter customerId;

    public CartCriteria() {
    }

    public CartCriteria(CartCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.productsNumber = other.productsNumber == null ? null : other.productsNumber.copy();
        this.totalPrice = other.totalPrice == null ? null : other.totalPrice.copy();
        this.productsInId = other.productsInId == null ? null : other.productsInId.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
    }

    @Override
    public CartCriteria copy() {
        return new CartCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getProductsNumber() {
        return productsNumber;
    }

    public void setProductsNumber(IntegerFilter productsNumber) {
        this.productsNumber = productsNumber;
    }

    public DoubleFilter getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(DoubleFilter totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LongFilter getProductsInId() {
        return productsInId;
    }

    public void setProductsInId(LongFilter productsInId) {
        this.productsInId = productsInId;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CartCriteria that = (CartCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(productsNumber, that.productsNumber) &&
            Objects.equals(totalPrice, that.totalPrice) &&
            Objects.equals(productsInId, that.productsInId) &&
            Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        productsNumber,
        totalPrice,
        productsInId,
        customerId
        );
    }

    @Override
    public String toString() {
        return "CartCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (productsNumber != null ? "productsNumber=" + productsNumber + ", " : "") +
                (totalPrice != null ? "totalPrice=" + totalPrice + ", " : "") +
                (productsInId != null ? "productsInId=" + productsInId + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
            "}";
    }

}
