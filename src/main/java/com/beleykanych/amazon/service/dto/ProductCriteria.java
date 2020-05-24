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
 * Criteria class for the {@link com.beleykanych.amazon.domain.Product} entity. This class is used
 * in {@link com.beleykanych.amazon.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter recommendeAgeGroup;

    private StringFilter category;

    private LongFilter sellerId;

    private LongFilter cartsInId;

    private LongFilter orderedById;

    public ProductCriteria() {
    }

    public ProductCriteria(ProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.recommendeAgeGroup = other.recommendeAgeGroup == null ? null : other.recommendeAgeGroup.copy();
        this.category = other.category == null ? null : other.category.copy();
        this.sellerId = other.sellerId == null ? null : other.sellerId.copy();
        this.cartsInId = other.cartsInId == null ? null : other.cartsInId.copy();
        this.orderedById = other.orderedById == null ? null : other.orderedById.copy();
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
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

    public StringFilter getRecommendeAgeGroup() {
        return recommendeAgeGroup;
    }

    public void setRecommendeAgeGroup(StringFilter recommendeAgeGroup) {
        this.recommendeAgeGroup = recommendeAgeGroup;
    }

    public StringFilter getCategory() {
        return category;
    }

    public void setCategory(StringFilter category) {
        this.category = category;
    }

    public LongFilter getSellerId() {
        return sellerId;
    }

    public void setSellerId(LongFilter sellerId) {
        this.sellerId = sellerId;
    }

    public LongFilter getCartsInId() {
        return cartsInId;
    }

    public void setCartsInId(LongFilter cartsInId) {
        this.cartsInId = cartsInId;
    }

    public LongFilter getOrderedById() {
        return orderedById;
    }

    public void setOrderedById(LongFilter orderedById) {
        this.orderedById = orderedById;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCriteria that = (ProductCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(recommendeAgeGroup, that.recommendeAgeGroup) &&
            Objects.equals(category, that.category) &&
            Objects.equals(sellerId, that.sellerId) &&
            Objects.equals(cartsInId, that.cartsInId) &&
            Objects.equals(orderedById, that.orderedById);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        recommendeAgeGroup,
        category,
        sellerId,
        cartsInId,
        orderedById
        );
    }

    @Override
    public String toString() {
        return "ProductCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (recommendeAgeGroup != null ? "recommendeAgeGroup=" + recommendeAgeGroup + ", " : "") +
                (category != null ? "category=" + category + ", " : "") +
                (sellerId != null ? "sellerId=" + sellerId + ", " : "") +
                (cartsInId != null ? "cartsInId=" + cartsInId + ", " : "") +
                (orderedById != null ? "orderedById=" + orderedById + ", " : "") +
            "}";
    }

}
