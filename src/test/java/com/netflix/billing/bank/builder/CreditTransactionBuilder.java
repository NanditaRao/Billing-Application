package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.model.CreditTransaction;

public class CreditTransactionBuilder {

    private String transactionId;
    private CreditType creditType;
    private Long amount;

    public CreditTransactionBuilder withCreditType(CreditType creditType) {
        this.creditType = creditType;
        return this;
    }

    public CreditTransactionBuilder withAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public CreditTransactionBuilder withTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public CreditTransaction build(){
        return new CreditTransaction(transactionId,creditType,amount);
    }
}
