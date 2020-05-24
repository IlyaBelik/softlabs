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
 * Criteria class for the {@link com.beleykanych.amazon.domain.Seller} entity. This class is used
 * in {@link com.beleykanych.amazon.web.rest.SellerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sellers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SellerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter phone;

    private StringFilter address;

    private LongFilter productsId;

    public SellerCriteria() {
    }

    public SellerCriteria(SellerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.productsId = other.productsId == null ? null : other.productsId.copy();
    }

    @Override
    public SellerCriteria copy() {
        return new SellerCriteria(this);
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

    public LongFilter getProductsId() {
        return productsId;
    }

    public void setProductsId(LongFilter productsId) {
        this.productsId = productsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SellerCriteria that = (SellerCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(address, that.address) &&
            Objects.equals(productsId, that.productsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        phone,
        address,
        productsId
        );
    }

    @Override
    public String toString() {
        return "SellerCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (phone != null ? "phone=" + phone + ", " : "") +
                (address != null ? "address=" + address + ", " : "") +
                (productsId != null ? "productsId=" + productsId + ", " : "") +
            "}";
    }

}
