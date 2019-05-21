package com.netflix.billing.bank.service.impl;

import com.netflix.billing.bank.builder.BalanceKeyBuilder;
import com.netflix.billing.bank.builder.CustomerBankAccountBuilder;
import com.netflix.billing.bank.builder.CustomerCreditTransactionBuilder;
import com.netflix.billing.bank.builder.CustomerDebitTransactionBuilder;
import com.netflix.billing.bank.builder.DebitAmountBuilder;
import com.netflix.billing.bank.builder.MoneyBuilder;
import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ValidationException;
import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidationServiceImplTest {

    @InjectMocks
    private ValidationServiceImpl validationService;


    @Test(expected = ValidationException.class)
    public void validate_withDuplicateTransactionId(){
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey, 100L).addCustomerCreditTransaction(credit).build();

        validationService.validateTransaction(customerBankAccount,"111");

    }

    @Test(expected = ValidationException.class)
    public void validate_debitWithNoCreditsAvailable(){
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(new MoneyBuilder().withAmount(100L).withCurrency("USD").build()).build();

        validationService.validateDebit(customerBankAccount,debitAmount);

    }

    @Test(expected = ValidationException.class)
    public void validate_withLessCrediAvailable(){
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey,100L).addCustomerCreditTransaction(credit).build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(new MoneyBuilder().withAmount(1000L).withCurrency("USD").build()).build();

        validationService.validateDebit(customerBankAccount,debitAmount);

    }

    @Test(expected = ValidationException.class)
    public void validate_withDuplicateInvoiceId(){
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        CustomerDebitTransaction debit = new CustomerDebitTransactionBuilder().withInvoiceId("111").withAmount(20L).withCurrency("USD").build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey,100L).addCustomerCreditTransaction(credit).addCustomerDebitTransaction(debit).build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(new MoneyBuilder().withAmount(1000L).withCurrency("USD").build()).build();

        validationService.validateDebit(customerBankAccount,debitAmount);

    }
}
