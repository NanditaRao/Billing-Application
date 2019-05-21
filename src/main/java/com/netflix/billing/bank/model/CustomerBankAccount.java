package com.netflix.billing.bank.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerBankAccount {

    private Map<BalanceKey,Long> balance;
    private List<CustomerCreditTransaction> credits;
    private List<CustomerDebitTransaction> debits;

    public CustomerBankAccount(){
        this.credits = new ArrayList<>();
        this.debits = new ArrayList<>();
        this.balance= new HashMap<>();
    }

    public List<CustomerCreditTransaction> getCredits() {
        return credits;
    }

    public void addCredit(CustomerCreditTransaction customerTransaction) {
        this.credits.add(customerTransaction);
    }

    public void addDebit(CustomerDebitTransaction customerTransaction) {
        this.debits.add(customerTransaction);
    }

    public List<CustomerDebitTransaction> getDebits() {
        return debits;
    }

    public Map<BalanceKey, Long> getBalance() {
        return balance;
    }

    public void setBalance(Map<BalanceKey, Long> balance) {
        this.balance = balance;
    }

    public void setCredits(List<CustomerCreditTransaction> credits) {
        this.credits = credits;
    }

    public void setDebits(List<CustomerDebitTransaction> debits) {
        this.debits = debits;
    }
}
