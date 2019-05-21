package com.netflix.billing.bank.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerDebitTransaction {
    private String invoiceId;
    private String currency;
    private Long amount;
    private Date date;
    private List<CreditTransaction> creditTransactions;

    public CustomerDebitTransaction(){
        creditTransactions = new ArrayList<>();
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<CreditTransaction> getCreditTransactions() {
        return creditTransactions;
    }

    public void addCreditTransaction(CreditTransaction creditTransaction) {
        this.creditTransactions.add(creditTransaction);
    }

    public void setCreditTransactions(List<CreditTransaction> creditTransactions) {
        this.creditTransactions = creditTransactions;
    }
}
