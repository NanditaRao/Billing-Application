package com.netflix.billing.bank.service.impl;

import com.netflix.billing.bank.controller.wire.CreditAmount;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerBankAccountEntity;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import com.netflix.billing.bank.service.CreditService;
import com.netflix.billing.bank.transformer.BalanceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class CreditServiceImpl implements CreditService {

    public static Long EMPTY_BALANCE = 0L;

    @Autowired
    private CustomerBankAccountEntity customerBankAccountEntity;

    @Override
    public CustomerBalance credit(String customerId, CreditAmount creditAmount, CustomerBankAccount customerBankAccount) {
        return (customerBankAccount == null) ? createCustomerRecord(customerId,creditAmount) : updateCustomerRecord(customerBankAccount,creditAmount,customerId);
    }

    private CustomerBalance createCustomerRecord(String customerId, CreditAmount creditAmount){
        CustomerBankAccount customerBankAccount = new CustomerBankAccount();
        customerBankAccount.addCredit(buildCreditTransaction(creditAmount));
        addBalance(customerBankAccount.getBalance(),creditAmount);
        customerBankAccountEntity.create(customerId, customerBankAccount);
        return BalanceTransformer.toCustomerBalance(customerBankAccount.getBalance());

    }

    private CustomerBalance updateCustomerRecord (CustomerBankAccount customerBankAccount, CreditAmount creditAmount, String customerId){
        customerBankAccount.addCredit(buildCreditTransaction(creditAmount));
        Map<BalanceKey, Long> updatedBalance = updateBalance(customerBankAccount, creditAmount);
        customerBankAccount.setBalance(updatedBalance);
        customerBankAccountEntity.update(customerId,customerBankAccount);
        return BalanceTransformer.toCustomerBalance(updatedBalance);
    }

    private Map<BalanceKey,Long> updateBalance(CustomerBankAccount customerBankAccount, CreditAmount creditAmount){
        Map<BalanceKey,Long> balance = customerBankAccount.getBalance();
        BalanceKey balanceKey = new BalanceKey(creditAmount.getCreditType(), creditAmount.getMoney().getCurrency());
        Long currentBalance = balance.get(balanceKey) == null ? EMPTY_BALANCE : balance.get(balanceKey);
        balance.put(balanceKey,currentBalance + creditAmount.getMoney().getAmount());
        return balance;
    }

    private void addBalance(Map<BalanceKey,Long> customerBalance, CreditAmount creditAmount){
        BalanceKey balanceKey = new BalanceKey(creditAmount.getCreditType(),creditAmount.getMoney().getCurrency());
        customerBalance.put(balanceKey, creditAmount.getMoney().getAmount());
    }

    private CustomerCreditTransaction buildCreditTransaction(CreditAmount creditAmount){
        CustomerCreditTransaction customerCreditTransaction = new CustomerCreditTransaction();
        customerCreditTransaction.setTransactionId(creditAmount.getTransactionId());
        customerCreditTransaction.setAmount(creditAmount.getMoney().getAmount());
        customerCreditTransaction.setCreditDate(new Date());
        customerCreditTransaction.setApplied(false);
        customerCreditTransaction.setCreditType(creditAmount.getCreditType());
        customerCreditTransaction.setCurrency(creditAmount.getMoney().getCurrency());
        return customerCreditTransaction;
    }


}
