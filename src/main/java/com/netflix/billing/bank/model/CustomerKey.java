package com.netflix.billing.bank.model;

import com.netflix.billing.bank.controller.wire.CreditType;

public class CustomerKey {
    private String customerId;
    private CreditType creditType;

    public CustomerKey(String customerId, CreditType creditType){
        this.customerId=customerId;
        this.creditType=creditType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public CreditType getCreditType() {
        return creditType;
    }

    public void setCreditType(CreditType creditType) {
        this.creditType = creditType;
    }
}
