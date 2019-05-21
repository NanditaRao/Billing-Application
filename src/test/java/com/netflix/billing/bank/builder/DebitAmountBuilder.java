package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.controller.wire.CreditAmount;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.controller.wire.Money;

public class DebitAmountBuilder {

    private String invoiceId;
    private Money money;

    public DebitAmountBuilder withInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

    public DebitAmountBuilder withMoney (Money money){
        this.money = money;
        return this;
    }

    public DebitAmount build(){
        DebitAmount debitAmount = new DebitAmount();
        debitAmount.setInvoiceId(invoiceId);
        debitAmount.setMoney(money);
        return debitAmount;
    }
}
