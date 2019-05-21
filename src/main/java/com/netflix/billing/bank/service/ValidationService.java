package com.netflix.billing.bank.service;

import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerCreditTransaction;

import java.util.List;

public interface ValidationService {
    void validateTransaction(CustomerBankAccount customerBankAccount, String transactionId);
    void validateDebit(CustomerBankAccount customerBankAccount, DebitAmount debitAmount);
}
