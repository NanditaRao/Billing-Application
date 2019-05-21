package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerBankAccountBuilder {

    private Map<BalanceKey,Long> balance;
    private List<CustomerCreditTransaction> credits;
    private List<CustomerDebitTransaction> debits;

    public CustomerBankAccountBuilder(){
        balance = new HashMap<>();
        credits = new ArrayList<>();
        debits = new ArrayList<>();
    }

    public CustomerBankAccountBuilder addBalance(BalanceKey balanceKey, Long amount) {
        balance.put(balanceKey,amount);
        return this;
    }

    public CustomerBankAccountBuilder addCustomerCreditTransaction(CustomerCreditTransaction customerCreditTransaction){
        credits.add(customerCreditTransaction);
        return this;
    }

    public CustomerBankAccountBuilder addCustomerDebitTransaction(CustomerDebitTransaction customerDebitransaction){
        debits.add(customerDebitransaction);
        return this;
    }

    public CustomerBankAccount build(){
        CustomerBankAccount customerBankAccount = new CustomerBankAccount();
        customerBankAccount.setBalance(balance);
        customerBankAccount.setCredits(credits);
        customerBankAccount.setDebits(debits);
        return customerBankAccount;
    }


}
