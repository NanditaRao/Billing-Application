package com.netflix.billing.bank.transformer;

import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.DebitLineItem;
import com.netflix.billing.bank.controller.wire.Money;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BalanceTransformer {

    public static CustomerBalance toCustomerBalance(Map<BalanceKey,Long> accountBalance){
        CustomerBalance customerBalance = new CustomerBalance();
        Map<CreditType,List<Money>> balanceAmounts = new HashMap<>();
        for(BalanceKey balanceKey : accountBalance.keySet()){
            List<Money> money = balanceAmounts.get(balanceKey.getCreditType());
            if(money == null){
                money = new ArrayList<>();
            }
            money.add(new Money( accountBalance.get(balanceKey), balanceKey.getCurrency()));
            balanceAmounts.put(balanceKey.getCreditType(),money);
        }
        customerBalance.setBalanceAmounts(balanceAmounts);
        return customerBalance;
    }

    public static List<DebitLineItem> toDebitLineItemList(List<CustomerDebitTransaction> debits){
        return debits.stream().map(debit ->toDebitLines(debit)).collect(ArrayList::new, List::addAll, List::addAll);
    }

    public static List<DebitLineItem> toDebitLines(CustomerDebitTransaction debit){
        return debit.getCreditTransactions().stream().map(t -> toDebitLine(t,debit)).collect(Collectors.toList());
    }

    public static DebitLineItem toDebitLine(CreditTransaction creditTransaction, CustomerDebitTransaction debit){
        DebitLineItem debitLineItem = new DebitLineItem();
        debitLineItem.setTransactionId(creditTransaction.getTransactionId());
        debitLineItem.setInvoiceId(debit.getInvoiceId());
        debitLineItem.setCreditType(creditTransaction.getCreditType());
        debitLineItem.setTransactionDate(debit.getDate().toInstant());
        debitLineItem.setAmount(new Money(creditTransaction.getAmount(),debit.getCurrency()));
        return debitLineItem;
    }
}
