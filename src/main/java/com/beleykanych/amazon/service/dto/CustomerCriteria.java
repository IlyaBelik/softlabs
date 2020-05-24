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
 * Criteria class for the {@link com.beleykanych.amazon.domain.Customer} entity. This class is used
 * in {@link com.beleykanych.amazon.web.rest.CustomerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CustomerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter phone;

    private StringFilter address;

    private LongFilter cartId;

    private LongFilter paymentsId;

    private LongFilter orderedProductsId;

    public CustomerCriteria() {
    }

    public CustomerCriteria(CustomerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.cartId = other.cartId == null ? null : other.cartId.copy();
        this.paymentsId = other.paymentsId == null ? null : other.paymentsId.copy();
        this.orderedProductsId = other.orderedProductsId == null ? null : other.orderedProductsId.copy();
    }

    @Override
    public CustomerCriteria copy() {
        return new CustomerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getAddress() {
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public LongFilter getCartId() {
        return cartId;
    }

    public void setCartId(LongFilter cartId) {
        this.cartId = cartId;
    }

    public LongFilter getPaymentsId() {
        return paymentsId;
    }

    public void setPaymentsId(LongFilter paymentsId) {
        this.paymentsId = paymentsId;
    }

    public LongFilter getOrderedProductsId() {
        return orderedProductsId;
    }

    public void setOrderedProductsId(LongFilter orderedProductsId) {
        this.orderedProductsId = orderedProductsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CustomerCriteria that = (CustomerCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(address, that.address) &&
            Objects.equals(cartId, that.cartId) &&
            Objects.equals(paymentsId, that.paymentsId) &&
            Objects.equals(orderedProductsId, that.orderedProductsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        phone,
        address,
        cartId,
        paymentsId,
        orderedProductsId
        );
    }

    @Override
    public String toString() {
        return "CustomerCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (phone != null ? "phone=" + phone + ", " : "") +
                (address != null ? "address=" + address + ", " : "") +
                (cartId != null ? "cartId=" + cartId + ", " : "") +
                (paymentsId != null ? "paymentsId=" + paymentsId + ", " : "") +
                (orderedProductsId != null ? "orderedProductsId=" + orderedProductsId + ", " : "") +
            "}";
    }

}
