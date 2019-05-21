package com.netflix.billing.bank.service.impl;

import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;
import org.springframework.stereotype.Service;
import com.netflix.billing.bank.service.ValidationService;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Override
    public void validateTransaction(CustomerBankAccount customerBankAccount, String transactionId) {
        if(customerBankAccount != null && !customerBankAccount.getCredits().isEmpty()) {
            Optional<CustomerCreditTransaction> existingTransactions = customerBankAccount.getCredits().stream()
                    .filter(t -> t.getTransactionId().equals(transactionId)).findFirst();
            if (existingTransactions.isPresent()) {
                throw new ValidationException("Duplicate transaction for customer ,transactionId  "+ transactionId);
            }
        }
    }

    @Override
    public void validateDebit(CustomerBankAccount customerBankAccount, DebitAmount debitAmount) {
        if(customerBankAccount == null || customerBankAccount.getCredits().isEmpty()){
            throw new ValidationException("Requested debit amount cannot as there have been no credits to this account ");
        }
        List<CustomerDebitTransaction> debits = customerBankAccount.getDebits();
        Optional<CustomerDebitTransaction> debitWithInvId = debits.stream().filter(debit -> debit.getInvoiceId().equals(debitAmount.getInvoiceId())).findAny();
        if(debitWithInvId.isPresent()){
            throw new ValidationException("Duplicate debit invoiceId "+debitAmount.getInvoiceId());
        }
        if(debitAmount.getMoney().getAmount() > getAvailableBalanceForCustomer(customerBankAccount.getBalance(),debitAmount)){
            throw new ValidationException("Requested debit amount exceeds available balance");
        }

    }

    private Long getAvailableBalanceForCustomer(Map<BalanceKey,Long> availableBalance, DebitAmount debitAmount){
        return Arrays.stream(CreditType.values())
                .map(c ->availableBalance.get(new BalanceKey(c,debitAmount.getMoney().getCurrency())))
                .filter(Objects::nonNull)
                .mapToLong(i->i).sum();


    }
}
