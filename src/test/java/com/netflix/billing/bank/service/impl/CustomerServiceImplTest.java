package com.netflix.billing.bank.service.impl;

import com.netflix.billing.bank.builder.BalanceKeyBuilder;
import com.netflix.billing.bank.builder.CreditAmountBuilder;
import com.netflix.billing.bank.builder.CreditTransactionBuilder;
import com.netflix.billing.bank.builder.CustomerBalanceBuilder;
import com.netflix.billing.bank.builder.CustomerBankAccountBuilder;
import com.netflix.billing.bank.builder.CustomerCreditTransactionBuilder;
import com.netflix.billing.bank.builder.CustomerDebitTransactionBuilder;
import com.netflix.billing.bank.builder.MoneyBuilder;
import com.netflix.billing.bank.controller.wire.CreditAmount;
import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.DebitHistory;
import com.netflix.billing.bank.controller.wire.DebitLineItem;
import com.netflix.billing.bank.controller.wire.Money;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CreditTransaction;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerBankAccountEntity;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;
import com.netflix.billing.bank.service.CreditService;
import com.netflix.billing.bank.service.DebitService;
import com.netflix.billing.bank.service.ValidationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceImplTest {

    @Mock
    private CustomerBankAccountEntity customerBankAccountEntity;

    @Mock
    private ValidationService validationService;

    @Mock
    private CreditService creditService;

    @Mock
    private DebitService debitService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Before
    public void setup(){
        validationService = mock(ValidationService.class);
        creditService = mock(CreditService.class);
        debitService = mock(DebitService.class);
    }

    @Test
    public void getBalance_whenThereIsNoBankAccountInformation(){
        Mockito.when(customerBankAccountEntity.findByCustomerId(Mockito.anyString())).thenReturn(null);

        CustomerBalance balance = customerService.getBalance("ABC");

        Assert.assertNull(balance.getBalanceAmounts());

    }

    @Test
    public void getBalance_withCreditInformation(){
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey, 100L).addCustomerCreditTransaction(credit).build();
        Map<CreditType, List<Money>> expectdBalanceAmounts = new CustomerBalanceBuilder().withBalance(CreditType.GIFTCARD, new MoneyBuilder().withCurrency("USD").withAmount(100L).build()).build().getBalanceAmounts();

        Mockito.when(customerBankAccountEntity.findByCustomerId(Mockito.anyString())).thenReturn(customerBankAccount);

        CustomerBalance balance = customerService.getBalance("ABC");

        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(expectdBalanceAmounts.size() ,balanceAmounts.size());
        Assert.assertEquals(expectdBalanceAmounts.get(CreditType.GIFTCARD).size() ,balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(expectdBalanceAmounts.get(CreditType.GIFTCARD).get(0) ,balanceAmounts.get(CreditType.GIFTCARD).get(0));
    }

    @Test
    public void getHistory_forOneDebitWithOneCreditApplied(){
        CreditTransaction creditTransaction = new CreditTransactionBuilder().withCreditType(CreditType.GIFTCARD).withTransactionId("123").withAmount(100L).build();
        CustomerDebitTransaction debit = new CustomerDebitTransactionBuilder().withInvoiceId("111").withCurrency("USD")
                .withAmount(100L).withDate(new Date()).addCreditTransaction(creditTransaction).build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addCustomerDebitTransaction(debit).build();
        Map<CreditType, List<Money>> expectdBalanceAmounts = new CustomerBalanceBuilder().withBalance(CreditType.GIFTCARD, new MoneyBuilder().withCurrency("USD").withAmount(100L).build()).build().getBalanceAmounts();

        Mockito.when(customerBankAccountEntity.findByCustomerId(Mockito.anyString())).thenReturn(customerBankAccount);

        DebitHistory history = customerService.getDebitHistory("ABC");

        List<DebitLineItem> debits = history.getDebits();
        Assert.assertEquals(1 ,debits.size());
        Assert.assertEquals("111" ,debits.get(0).getInvoiceId());
        Assert.assertEquals( "123", debits.get(0).getTransactionId());
        Assert.assertEquals( Long.valueOf(100), Long.valueOf(debits.get(0).getAmount().getAmount()));
        Assert.assertEquals( "USD", debits.get(0).getAmount().getCurrency());
    }

    @Test
    public void getHistory_forOneDebitAcrossCredits(){
        CreditTransaction creditTransaction1 = new CreditTransactionBuilder().withCreditType(CreditType.GIFTCARD).withTransactionId("123").withAmount(100L).build();
        CreditTransaction creditTransaction2 = new CreditTransactionBuilder().withCreditType(CreditType.PROMOTION).withTransactionId("345").withAmount(50L).build();
        CreditTransaction creditTransaction3 = new CreditTransactionBuilder().withCreditType(CreditType.CASH).withTransactionId("456").withAmount(5L).build();
        CustomerDebitTransaction debit = new CustomerDebitTransactionBuilder().withInvoiceId("111").withCurrency("USD")
                .withAmount(155L).withDate(new Date()).addCreditTransaction(creditTransaction1).addCreditTransaction(creditTransaction2).addCreditTransaction(creditTransaction3).build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addCustomerDebitTransaction(debit).build();

        Mockito.when(customerBankAccountEntity.findByCustomerId(Mockito.anyString())).thenReturn(customerBankAccount);

        DebitHistory history = customerService.getDebitHistory("ABC");

        List<DebitLineItem> debits = history.getDebits();
        Assert.assertEquals(3 ,debits.size());
        DebitLineItem debitLineItem1 = debits.stream().filter(debitLineItem -> debitLineItem.getCreditType().equals(CreditType.GIFTCARD)).findFirst().get();
        Assert.assertEquals(Long.valueOf(100) ,Long.valueOf(debitLineItem1.getAmount().getAmount()));
        DebitLineItem debitLineItem2 = debits.stream().filter(debitLineItem -> debitLineItem.getCreditType().equals(CreditType.PROMOTION)).findFirst().get();
        Assert.assertEquals(Long.valueOf(50) ,Long.valueOf(debitLineItem2.getAmount().getAmount()));
        DebitLineItem debitLineItem3 = debits.stream().filter(debitLineItem -> debitLineItem.getCreditType().equals(CreditType.CASH)).findFirst().get();
        Assert.assertEquals(Long.valueOf(5),Long.valueOf(debitLineItem3.getAmount().getAmount()));
    }





}
