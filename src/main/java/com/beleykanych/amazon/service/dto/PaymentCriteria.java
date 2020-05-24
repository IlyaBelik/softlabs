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
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.beleykanych.amazon.domain.Payment} entity. This class is used
 * in {@link com.beleykanych.amazon.web.rest.PaymentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PaymentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter cardType;

    private StringFilter cardNumber;

    private ZonedDateTimeFilter dateTime;

    private BooleanFilter successful;

    private LongFilter customerId;

    public PaymentCriteria() {
    }

    public PaymentCriteria(PaymentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.cardType = other.cardType == null ? null : other.cardType.copy();
        this.cardNumber = other.cardNumber == null ? null : other.cardNumber.copy();
        this.dateTime = other.dateTime == null ? null : other.dateTime.copy();
        this.successful = other.successful == null ? null : other.successful.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
    }

    @Override
    public PaymentCriteria copy() {
        return new PaymentCriteria(this);
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

    public StringFilter getCardType() {
        return cardType;
    }

    public void setCardType(StringFilter cardType) {
        this.cardType = cardType;
    }

    public StringFilter getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(StringFilter cardNumber) {
        this.cardNumber = cardNumber;
    }

    public ZonedDateTimeFilter getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTimeFilter dateTime) {
        this.dateTime = dateTime;
    }

    public BooleanFilter getSuccessful() {
        return successful;
    }

    public void setSuccessful(BooleanFilter successful) {
        this.successful = successful;
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
        final PaymentCriteria that = (PaymentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(cardType, that.cardType) &&
            Objects.equals(cardNumber, that.cardNumber) &&
            Objects.equals(dateTime, that.dateTime) &&
            Objects.equals(successful, that.successful) &&
            Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        cardType,
        cardNumber,
        dateTime,
        successful,
        customerId
        );
    }

    @Override
    public String toString() {
        return "PaymentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (cardType != null ? "cardType=" + cardType + ", " : "") +
                (cardNumber != null ? "cardNumber=" + cardNumber + ", " : "") +
                (dateTime != null ? "dateTime=" + dateTime + ", " : "") +
                (successful != null ? "successful=" + successful + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
            "}";
    }

}
