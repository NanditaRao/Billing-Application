package com.netflix.billing.bank.model;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CustomerBankAccountEntity {

    private ConcurrentHashMap<String,CustomerBankAccount> customerBankAccount;

    public ConcurrentHashMap<String, CustomerBankAccount> getCustomerBankAccount() {
        return customerBankAccount;
    }

    public void setCustomerBankAccount(ConcurrentHashMap<String,CustomerBankAccount> customerBankAccount) {
        this.customerBankAccount = customerBankAccount;
    }

    public CustomerBankAccount findByCustomerId(String customerId){
        return this.customerBankAccount.get(customerId);
    }

    public void create(String customerId, CustomerBankAccount customerBankAccount){
        CustomerBankAccount custBankAccount = this.customerBankAccount.putIfAbsent(customerId, customerBankAccount);
        if(custBankAccount != null){
            throw new RuntimeException("Concurrent threads are trying to create the same bank account entry. " +
                    "Failing one of the transactions so that it can be updated correctly on a retry.");
        }

    }

    public void update(String customerId, CustomerBankAccount customerBankAccount){
        synchronized (this.customerBankAccount.get(customerId)){
            this.customerBankAccount.put(customerId,customerBankAccount);
        }

    }



}
