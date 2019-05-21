package com.netflix.billing.bank.service;

import com.netflix.billing.bank.controller.wire.CreditAmount;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.controller.wire.DebitHistory;
import com.netflix.billing.bank.controller.wire.DebitLineItem;

import java.util.List;

public interface CustomerService {
    CustomerBalance credit(String customerId, CreditAmount creditAmount);
    CustomerBalance debit (String customerId, DebitAmount debitAmount);
    CustomerBalance getBalance (String customerId);
    DebitHistory getDebitHistory(String customerId);
}
