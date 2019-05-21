package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.model.CustomerCreditTransaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerCreditTransactionBuilder {

    private String transactionId;
    private CreditType creditType;
    private String currency;
    private Long amount;
    private Date creditDate;
    private boolean applied;
    private List<String> appliedInvoiceIds;

    public CustomerCreditTransactionBuilder(){
        appliedInvoiceIds = new ArrayList<>();
    }

    public CustomerCreditTransactionBuilder withCreditType(CreditType creditType) {
        this.creditType = creditType;
        return this;
    }

    public CustomerCreditTransactionBuilder withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public CustomerCreditTransactionBuilder withAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public CustomerCreditTransactionBuilder withDate(Date date) {
        this.creditDate = date;
        return this;
    }

    public CustomerCreditTransactionBuilder withTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public CustomerCreditTransactionBuilder addInvoiceId(String invoiceId) {
        this.appliedInvoiceIds.add(invoiceId);
        return this;
    }

    public CustomerCreditTransactionBuilder withApplied(boolean applied) {
        this.applied = applied;
        return this;
    }

    public CustomerCreditTransaction build(){
        CustomerCreditTransaction customerCreditTransaction = new CustomerCreditTransaction();
        customerCreditTransaction.setTransactionId(transactionId);
        customerCreditTransaction.setCreditType(creditType);
        customerCreditTransaction.setAmount(amount);
        customerCreditTransaction.setCurrency(currency);
        customerCreditTransaction.setCreditDate(creditDate);
        customerCreditTransaction.setApplied(applied);
        customerCreditTransaction.setAppliedInvoiceIds(appliedInvoiceIds);
        return customerCreditTransaction;
    }


}
