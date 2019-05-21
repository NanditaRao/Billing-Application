package com.netflix.billing.bank.model;

import com.netflix.billing.bank.controller.wire.CreditType;

public class CreditTransaction {

    private String transactionId;
    private CreditType creditType;
    private Long amount;

    public CreditTransaction(String transactionId, CreditType creditType, Long amount){
        this.transactionId = transactionId;
        this.creditType = creditType;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public CreditType getCreditType() {
        return creditType;
    }

    public Long getAmount() {
        return amount;
    }
}
