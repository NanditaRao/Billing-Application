package com.netflix.billing.bank.service;

import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.model.CustomerBankAccount;

public interface DebitService {
    CustomerBalance debit (String customerId, DebitAmount debitAmount, CustomerBankAccount customerBankAccount);

}
