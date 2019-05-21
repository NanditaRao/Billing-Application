package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.controller.wire.CreditAmount;
import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.Money;

public class CreditAmountBuilder {

    private String transactionId;
    private Money money;
    private CreditType creditType;

    public CreditAmountBuilder withTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public CreditAmountBuilder withCreditType(CreditType creditType) {
        this.creditType = creditType;
        return this;
    }

    public CreditAmountBuilder withMoney (Money money){
        this.money = money;
        return this;
    }

    public CreditAmount build(){
        CreditAmount creditAmount = new CreditAmount();
        creditAmount.setTransactionId(transactionId);
        creditAmount.setCreditType(creditType);
        creditAmount.setMoney(money);
        return creditAmount;
    }


}
