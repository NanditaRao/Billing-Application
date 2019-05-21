package com.netflix.billing.bank.service;

import com.netflix.billing.bank.controller.wire.CreditAmount;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.model.CustomerBankAccount;

public interface CreditService {
    CustomerBalance credit (String customerId, CreditAmount creditAmount, CustomerBankAccount customerBankAccount);
}
