package com.netflix.billing.bank.controller.wire;

/**
 * Wire object representing a credit that the customer is trying to push to their account. The customer's balance will
 * increase once the credit represented here is applied to their account.
 */
public class CreditAmount {
    //Id related to the cash transaction that facilitated this balance transfer. This should be unique for a given creditType.
    private String transactionId;

    //Money object representing the amount left in the account.
    private Money money;

    //Type of credit. Different types of credits cannot be merged with each other.
    private CreditType creditType;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public CreditType getCreditType() {
        return creditType;
    }

    public void setCreditType(CreditType creditType) {
        this.creditType = creditType;
    }
}
