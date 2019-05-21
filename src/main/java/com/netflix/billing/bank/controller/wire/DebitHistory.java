package com.netflix.billing.bank.controller.wire;

import java.util.List;

/**
 * List of all the debit transactions applied to the customer's account.
 */
public class DebitHistory {
    private List<DebitLineItem> debits;

    public List<DebitLineItem> getDebits() {
        return debits;
    }

    public void setDebits(List<DebitLineItem> debits) {
        this.debits = debits;
    }
}
