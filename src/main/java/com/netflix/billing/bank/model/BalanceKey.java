package com.netflix.billing.bank.model;

import com.netflix.billing.bank.controller.wire.CreditType;

import java.util.Objects;

public class BalanceKey {
    private CreditType creditType;
    private String currency;

    public BalanceKey(CreditType creditType, String currency){
        this.creditType=creditType;
        this.currency=currency;
    }

    public CreditType getCreditType() {
        return creditType;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (creditType != null ? creditType.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BalanceKey balanceKey = (BalanceKey) o;
        return Objects.equals(this.creditType, balanceKey.creditType) && Objects.equals(this.currency, balanceKey.currency);
    }

    @Override
    public String toString() {
        return "BalanceKey{" + "creditType='" + creditType + '\'' + ", currency='" + currency+'}';
    }
}
