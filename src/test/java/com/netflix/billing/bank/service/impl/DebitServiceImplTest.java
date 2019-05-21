package com.netflix.billing.bank.service.impl;

import com.netflix.billing.bank.builder.BalanceKeyBuilder;
import com.netflix.billing.bank.builder.CustomerBankAccountBuilder;
import com.netflix.billing.bank.builder.CustomerCreditTransactionBuilder;
import com.netflix.billing.bank.builder.DebitAmountBuilder;
import com.netflix.billing.bank.builder.MoneyBuilder;
import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.DebitAmount;
import com.netflix.billing.bank.controller.wire.Money;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerBankAccountEntity;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import com.netflix.billing.bank.model.CustomerDebitTransaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DebitServiceImplTest {

    @Mock
    private CustomerBankAccountEntity customerBankAccountEntity;

    @InjectMocks
    private DebitServiceImpl debitService;

    @Captor
    ArgumentCaptor<CustomerBankAccount> customerBankAccountArgumentCaptor;

    @Test
    public void debit_withOneCredit(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(10L).build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(money).build();
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey, 100L).addCustomerCreditTransaction(credit).build();

        CustomerBalance balance = debitService.debit("ABC",debitAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(1, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(90), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());

    }

    @Test
    public void debit_withCreditEqualToDebitAmount(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(100L).build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(money).build();
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey, 100L).addCustomerCreditTransaction(credit).build();

        CustomerBalance balance = debitService.debit("ABC",debitAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(1, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(0), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());

    }

    @Test
    public void debit_appliedAcrossTwoCredits(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(100L).build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(money).build();
        CustomerCreditTransaction credit1 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(70L).withDate(new Date()).build();
        CustomerCreditTransaction credit2 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey, 170L).addCustomerCreditTransaction(credit1)
                .addCustomerCreditTransaction(credit2).build();

        CustomerBalance balance = debitService.debit("ABC",debitAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(1, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(70), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());

    }

    @Test
    public void debit_prioritizeByEnumOrder(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(100L).build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(money).build();
        CustomerCreditTransaction credit1 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(70L).withDate(new Date()).build();
        CustomerCreditTransaction credit2 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.CASH).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey1 = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        BalanceKey balanceKey2 = new BalanceKeyBuilder().withCreditType(CreditType.CASH).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey1, 70L).addBalance(balanceKey2,100L).addCustomerCreditTransaction(credit1)
                .addCustomerCreditTransaction(credit2).build();


        CustomerBalance balance = debitService.debit("ABC",debitAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(2, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(0), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.CASH).size());
        Assert.assertEquals(Long.valueOf(70), Long.valueOf(balanceAmounts.get(CreditType.CASH).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.CASH).get(0).getCurrency());

    }

    @Test
    public void debit_prioritizeByEnumOrderAllTypes(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(100L).build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(money).build();
        CustomerCreditTransaction credit1 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(70L).withDate(new Date()).build();
        CustomerCreditTransaction credit2 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.CASH).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        CustomerCreditTransaction credit3 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.PROMOTION).withCurrency("USD")
                .withAmount(20L).withDate(new Date()).build();
        BalanceKey balanceKey1 = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        BalanceKey balanceKey2 = new BalanceKeyBuilder().withCreditType(CreditType.CASH).withCurrency("USD").build();
        BalanceKey balanceKey3 = new BalanceKeyBuilder().withCreditType(CreditType.PROMOTION).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey1, 70L).addBalance(balanceKey2,100L)
                .addBalance(balanceKey3,20L).addCustomerCreditTransaction(credit1).addCustomerCreditTransaction(credit2).addCustomerCreditTransaction(credit3).build();


        CustomerBalance balance = debitService.debit("ABC",debitAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(3, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(0), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.PROMOTION).size());
        Assert.assertEquals(Long.valueOf(0), Long.valueOf(balanceAmounts.get(CreditType.PROMOTION).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.PROMOTION).get(0).getCurrency());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.CASH).size());
        Assert.assertEquals(Long.valueOf(90), Long.valueOf(balanceAmounts.get(CreditType.CASH).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.CASH).get(0).getCurrency());

    }

    @Test
    public void debit_verifyStoredDebitsAndCredits(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(100L).build();
        DebitAmount debitAmount = new DebitAmountBuilder().withInvoiceId("111").withMoney(money).build();
        CustomerCreditTransaction credit1 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(70L).withDate(new Date()).build();
        CustomerCreditTransaction credit2 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.CASH).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        CustomerCreditTransaction credit3 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.PROMOTION).withCurrency("USD")
                .withAmount(20L).withDate(new Date()).build();
        BalanceKey balanceKey1 = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        BalanceKey balanceKey2 = new BalanceKeyBuilder().withCreditType(CreditType.CASH).withCurrency("USD").build();
        BalanceKey balanceKey3 = new BalanceKeyBuilder().withCreditType(CreditType.PROMOTION).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey1, 70L).addBalance(balanceKey2,100L)
                .addBalance(balanceKey3,20L).addCustomerCreditTransaction(credit1).addCustomerCreditTransaction(credit2).addCustomerCreditTransaction(credit3).build();


        CustomerBalance balance = debitService.debit("ABC",debitAmount,customerBankAccount);

        verify(customerBankAccountEntity).update(Mockito.anyString(),customerBankAccountArgumentCaptor.capture());

        CustomerBankAccount customerAccount = customerBankAccountArgumentCaptor.getValue();
        List<CustomerDebitTransaction> debits = customerAccount.getDebits();
        List<CustomerCreditTransaction> credits = customerAccount.getCredits();
        Assert.assertEquals(1, debits.size());
        Assert.assertEquals(3, debits.get(0).getCreditTransactions().size());

        CustomerCreditTransaction creditTransaction1 = credits.stream().filter(credit -> credit.getCreditType().equals(CreditType.GIFTCARD)).findFirst().get();
        Assert.assertTrue(creditTransaction1.isApplied());
        Assert.assertEquals(1,creditTransaction1.getAppliedInvoiceIds().size());
        CustomerCreditTransaction creditTransaction2 = credits.stream().filter(credit -> credit.getCreditType().equals(CreditType.PROMOTION)).findFirst().get();
        Assert.assertEquals(1,creditTransaction2.getAppliedInvoiceIds().size());
        Assert.assertTrue(creditTransaction2.isApplied());
        CustomerCreditTransaction creditTransaction3 = credits.stream().filter(credit -> credit.getCreditType().equals(CreditType.CASH)).findFirst().get();
        Assert.assertEquals(1,creditTransaction3.getAppliedInvoiceIds().size());
        Assert.assertEquals(Long.valueOf(90L),creditTransaction3.getAmount());

    }
}


