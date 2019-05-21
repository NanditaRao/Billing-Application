package com.netflix.billing.bank.controller.wire;

import java.time.Instant;

/**
 * Represents a single debit transaction that was applied to the customer's account.
 */
public class DebitLineItem {
    private String invoiceId;
    private Money amount;
    private String transactionId;  //Credit transactionId it was charged against.
    private CreditType creditType; //Credit type it was charged against.
    private Instant transactionDate; //The time in UTC when the debit was applied to the account.

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public CreditType getCreditType() {
        return creditType;
    }

    public void setCreditType(CreditType creditType) {
        this.creditType = creditType;
    }
}

