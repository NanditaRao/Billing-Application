package com.netflix.billing.bank.service.impl;

import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CreditTransaction;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerBankAccountEntity;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;
import com.netflix.billing.bank.service.DebitService;
import com.netflix.billing.bank.transformer.BalanceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class DebitServiceImpl implements DebitService {

    public static Long EMPTY = 0L;

    @Autowired
    private CustomerBankAccountEntity customerBankAccountEntity;

    @Override
    public CustomerBalance debit(String customerId, DebitAmount debitAmount, CustomerBankAccount customerBankAccount) {
        CustomerDebitTransaction debit = buildDebitTransaction(debitAmount);
        debitRequestedAmount(customerBankAccount,debitAmount, debit);
        customerBankAccount.addDebit(debit);
        customerBankAccountEntity.update(customerId,customerBankAccount);
        return BalanceTransformer.toCustomerBalance(customerBankAccount.getBalance());
    }

    private void debitRequestedAmount(CustomerBankAccount customerBankAccount, DebitAmount debitAmount, CustomerDebitTransaction debit){
        List<CustomerCreditTransaction> credits = customerBankAccount.getCredits();
        Long amountToBeDebited = debitAmount.getMoney().getAmount();
        credits.sort(Comparator.comparing(CustomerCreditTransaction::getCreditType).thenComparing(CustomerCreditTransaction::getCreditDate));
        while(amountToBeDebited > EMPTY) {
            for (CustomerCreditTransaction credit : credits) {
                if (!credit.isApplied() && credit.isValidForCurrency(debitAmount.getMoney().getCurrency())) {
                    credit.addInvoiceId(debitAmount.getInvoiceId());
                    BalanceKey balanceKey = new BalanceKey(credit.getCreditType(),debitAmount.getMoney().getCurrency());
                    Long balance = customerBankAccount.getBalance().get(balanceKey);
                    if (credit.debitGreaterThanAmount(amountToBeDebited)){
                        credit.setApplied(true);
                        customerBankAccount.getBalance().put(balanceKey, balance - credit.getAmount());
                        amountToBeDebited = amountToBeDebited - credit.getAmount();
                        debit.addCreditTransaction(new CreditTransaction(credit.getTransactionId(),credit.getCreditType(),credit.getAmount()));
                    }
                    else {
                        credit.setAmount(credit.getAmount() - amountToBeDebited);
                        customerBankAccount.getBalance().put(balanceKey, balance - amountToBeDebited);
                        debit.addCreditTransaction(new CreditTransaction(credit.getTransactionId(),credit.getCreditType(),amountToBeDebited));
                        amountToBeDebited = EMPTY;
                        break;
                    }
                }
            }
        }

    }

    private CustomerDebitTransaction buildDebitTransaction(DebitAmount debitAmount){
        CustomerDebitTransaction customerDebitTransaction = new CustomerDebitTransaction();
        customerDebitTransaction.setInvoiceId(debitAmount.getInvoiceId());
        customerDebitTransaction.setCurrency(debitAmount.getMoney().getCurrency());
        customerDebitTransaction.setAmount(debitAmount.getMoney().getAmount());
        customerDebitTransaction.setDate(new Date());
        return customerDebitTransaction;
    }

}
