package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.controller.wire.Money;

public class MoneyBuilder {

    private Long amount;
    private String currency;

    public MoneyBuilder withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public MoneyBuilder withAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public Money build(){
        return new Money(amount,currency);
    }
}
