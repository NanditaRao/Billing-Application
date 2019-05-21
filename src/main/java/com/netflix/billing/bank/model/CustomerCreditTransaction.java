package com.netflix.billing.bank.model;

import com.netflix.billing.bank.controller.wire.CreditType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerCreditTransaction {

    private String transactionId;
    private CreditType creditType;
    private String currency;
    private Long amount;
    private Date creditDate;
    private boolean applied;
    private List<String> appliedInvoiceIds;

    public CustomerCreditTransaction(){
        appliedInvoiceIds = new ArrayList<>();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public CreditType getCreditType() {
        return creditType;
    }

    public void setCreditType(CreditType creditType) {
        this.creditType = creditType;
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

    public Date getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(Date creditDate) {
        this.creditDate = creditDate;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public List<String> getAppliedInvoiceIds() {
        return appliedInvoiceIds;
    }

    public void addInvoiceId(String invoiceId) {
        this.appliedInvoiceIds.add(invoiceId);
    }

    public void setAppliedInvoiceIds(List<String> appliedInvoiceIds) {
        this.appliedInvoiceIds = appliedInvoiceIds;
    }

    public boolean isValidForCurrency(String currency){
        return this.currency.equals(currency);
    }

    public boolean debitGreaterThanAmount(Long debitAmount){
        return debitAmount > this.amount;
    }
}
