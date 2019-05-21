package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.model.BalanceKey;

public class BalanceKeyBuilder {
    private CreditType creditType;
    private String currency;

    public BalanceKeyBuilder withCreditType(CreditType creditType) {
        this.creditType = creditType;
        return this;
    }

    public BalanceKeyBuilder withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public BalanceKey build(){
        return new BalanceKey(this.creditType,this.currency);
    }
}
