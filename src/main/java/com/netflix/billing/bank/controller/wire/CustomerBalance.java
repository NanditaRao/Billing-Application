package com.netflix.billing.bank.controller.wire;

import java.util.List;
import java.util.Map;

/**
 * Represents the sum total of all the credits a customer has left after deducting all the debit transactions
 * from the their account.
 */
public class CustomerBalance {
    private Map<CreditType, List<Money>> balanceAmounts;

    public Map<CreditType, List<Money>> getBalanceAmounts() {
        return balanceAmounts;
    }

    public void setBalanceAmounts(Map<CreditType, List<Money>> balanceAmounts) {
        this.balanceAmounts = balanceAmounts;
    }
}

