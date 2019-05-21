package com.netflix.billing.bank.builder;

import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.Money;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerBalanceBuilder {
    private Map<CreditType, List<Money>> balanceAmounts;

    public CustomerBalanceBuilder(){
        balanceAmounts = new HashMap<>();
    }

    public CustomerBalanceBuilder withBalance(CreditType creditType, Money money){
        balanceAmounts.put(creditType, Arrays.asList(money));
        return this;
    }

    public CustomerBalance build(){
        CustomerBalance customerBalance = new CustomerBalance();
        customerBalance.setBalanceAmounts(this.balanceAmounts);
        return customerBalance;
    }

}
