package com.netflix.billing.bank.service.impl;

import com.netflix.billing.bank.controller.wire.CreditAmount;
import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.controller.wire.DebitHistory;
import com.netflix.billing.bank.controller.wire.DebitLineItem;
import com.netflix.billing.bank.controller.wire.Money;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerBankAccountEntity;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;
import com.netflix.billing.bank.service.CreditService;
import com.netflix.billing.bank.service.CustomerService;
import com.netflix.billing.bank.service.DebitService;
import com.netflix.billing.bank.service.ValidationService;
import com.netflix.billing.bank.transformer.BalanceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;


@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerBankAccountEntity customerBankAccountEntity;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private DebitService debitService;

    @Autowired
    private CreditService creditService;

    @Override
    public CustomerBalance credit(String customerId, CreditAmount creditAmount) {
        CustomerBankAccount customerBankAccount = customerBankAccountEntity.findByCustomerId(customerId);
        return creditService.credit(customerId,creditAmount,customerBankAccount);
    }

    @Override
    public CustomerBalance debit(String customerId, DebitAmount debitAmount) {
        CustomerBankAccount customerBankAccount = customerBankAccountEntity.findByCustomerId(customerId);
        validationService.validateDebit(customerBankAccount, debitAmount);
        return debitService.debit(customerId,debitAmount,customerBankAccount);
    }

    @Override
    public CustomerBalance getBalance(String customerId) {
        CustomerBankAccount customerBankAccount = customerBankAccountEntity.findByCustomerId(customerId);
        if(customerBankAccount == null){
            return new CustomerBalance();
        }
        return BalanceTransformer.toCustomerBalance(customerBankAccount.getBalance());
    }

    @Override
    public DebitHistory getDebitHistory(String customerId) {
        CustomerBankAccount customerBankAccount = customerBankAccountEntity.findByCustomerId(customerId);
        if(customerBankAccount == null){
            return new DebitHistory();
        }
        DebitHistory debitHistory = new DebitHistory();
        debitHistory.setDebits(BalanceTransformer.toDebitLineItemList(customerBankAccount.getDebits()));
        return debitHistory;
    }

}
