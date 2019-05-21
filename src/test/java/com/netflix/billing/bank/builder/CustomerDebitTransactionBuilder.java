package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.model.CreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerDebitTransactionBuilder {

    private String invoiceId;
    private String currency;
    private Long amount;
    private Date date;
    private List<CreditTransaction> creditTransactions;

    public CustomerDebitTransactionBuilder(){
        creditTransactions = new ArrayList<>();
    }

    public CustomerDebitTransactionBuilder withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public CustomerDebitTransactionBuilder withAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public CustomerDebitTransactionBuilder withDate(Date date) {
        this.date = date;
        return this;
    }

    public CustomerDebitTransactionBuilder withInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

    public CustomerDebitTransactionBuilder addCreditTransaction(CreditTransaction creditTransaction) {
        this.creditTransactions.add(creditTransaction);
        return this;
    }

    public CustomerDebitTransaction build(){
        CustomerDebitTransaction customerDebitTransaction = new CustomerDebitTransaction();
        customerDebitTransaction.setInvoiceId(invoiceId);
        customerDebitTransaction.setCurrency(currency);
        customerDebitTransaction.setDate(date);
        customerDebitTransaction.setAmount(amount);
        customerDebitTransaction.setCreditTransactions(creditTransactions);
        return customerDebitTransaction;
    }


}
