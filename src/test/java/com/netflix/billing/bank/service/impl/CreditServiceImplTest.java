package com.netflix.billing.bank.service.impl;

import com.netflix.billing.bank.builder.BalanceKeyBuilder;
import com.netflix.billing.bank.builder.CreditAmountBuilder;
import com.netflix.billing.bank.builder.CustomerBankAccountBuilder;
import com.netflix.billing.bank.builder.CustomerCreditTransactionBuilder;
import com.netflix.billing.bank.builder.MoneyBuilder;
import com.netflix.billing.bank.controller.wire.CreditAmount;
import com.netflix.billing.bank.controller.wire.CreditType;
import com.netflix.billing.bank.controller.wire.CustomerBalance;
import com.netflix.billing.bank.controller.wire.Money;
import com.netflix.billing.bank.model.BalanceKey;
import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerBankAccountEntity;
import com.netflix.billing.bank.model.CustomerCreditTransaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreditServiceImplTest {

    @Mock
    private CustomerBankAccountEntity customerBankAccountEntity;

    @InjectMocks
    private CreditServiceImpl creditService;

    @Test
    public void credit_addNewCredit(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(100L).build();
        CreditAmount creditAmount = new CreditAmountBuilder().withCreditType(CreditType.GIFTCARD).withMoney(money).build();

        CustomerBalance balance = creditService.credit("ABC",creditAmount,null);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(1, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(100), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());

    }

    @Test
    public void credit_addNewCreditToExistingCredit(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(100L).build();
        CreditAmount creditAmount = new CreditAmountBuilder().withCreditType(CreditType.GIFTCARD).withMoney(money).build();
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey, 100L).addCustomerCreditTransaction(credit).build();

        CustomerBalance balance = creditService.credit("ABC",creditAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(1, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(200), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());

    }

    @Test
    public void credit_addNewCreditWithDifferentType(){
        Money money = new MoneyBuilder().withCurrency("USD").withAmount(100L).build();
        CreditAmount creditAmount = new CreditAmountBuilder().withCreditType(CreditType.CASH).withMoney(money).build();
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey, 100L).addCustomerCreditTransaction(credit).build();

        CustomerBalance balance = creditService.credit("ABC",creditAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(2, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(100), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.CASH).size());
        Assert.assertEquals(Long.valueOf(100), Long.valueOf(balanceAmounts.get(CreditType.CASH).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.CASH).get(0).getCurrency());

    }

    @Test
    public void credit_addNewCreditSameTypeDiffCurrency(){
        Money money = new MoneyBuilder().withCurrency("GBP").withAmount(50L).build();
        CreditAmount creditAmount = new CreditAmountBuilder().withCreditType(CreditType.GIFTCARD).withMoney(money).build();
        CustomerCreditTransaction credit = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        BalanceKey balanceKey = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey, 100L).addCustomerCreditTransaction(credit).build();

        CustomerBalance balance = creditService.credit("ABC",creditAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(1, balanceAmounts.size());
        Assert.assertEquals(2, balanceAmounts.get(CreditType.GIFTCARD).size());

    }

    @Test
    public void credit_multipleCurrencyAndTypes(){
        Money money = new MoneyBuilder().withCurrency("GBP").withAmount(15L).build();
        CreditAmount creditAmount = new CreditAmountBuilder().withCreditType(CreditType.PROMOTION).withMoney(money).build();
        CustomerCreditTransaction credit1 = new CustomerCreditTransactionBuilder().withTransactionId("111").withApplied(false).withCreditType(CreditType.GIFTCARD).withCurrency("USD")
                .withAmount(100L).withDate(new Date()).build();
        CustomerCreditTransaction credit2 = new CustomerCreditTransactionBuilder().withTransactionId("222").withApplied(false).withCreditType(CreditType.CASH).withCurrency("USD")
                .withAmount(50L).withDate(new Date()).build();
        CustomerCreditTransaction credit3 = new CustomerCreditTransactionBuilder().withTransactionId("333").withApplied(false).withCreditType(CreditType.PROMOTION).withCurrency("GBP")
                .withAmount(70L).withDate(new Date()).build();
        BalanceKey balanceKey1 = new BalanceKeyBuilder().withCreditType(CreditType.GIFTCARD).withCurrency("USD").build();
        BalanceKey balanceKey2 = new BalanceKeyBuilder().withCreditType(CreditType.CASH).withCurrency("USD").build();
        BalanceKey balanceKey3 = new BalanceKeyBuilder().withCreditType(CreditType.PROMOTION).withCurrency("USD").build();
        CustomerBankAccount customerBankAccount = new CustomerBankAccountBuilder().addBalance(balanceKey1, 100L).addBalance(balanceKey2,50L).addBalance(balanceKey3, 70L).addCustomerCreditTransaction(credit1)
                .addCustomerCreditTransaction(credit2).addCustomerCreditTransaction(credit3).build();

        CustomerBalance balance = creditService.credit("ABC",creditAmount,customerBankAccount);

        Assert.assertNotNull(balance);
        Map<CreditType, List<Money>> balanceAmounts = balance.getBalanceAmounts();
        Assert.assertEquals(3, balanceAmounts.size());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.GIFTCARD).size());
        Assert.assertEquals(Long.valueOf(100), Long.valueOf(balanceAmounts.get(CreditType.GIFTCARD).get(0).getAmount()));
        Assert.assertEquals("USD", balanceAmounts.get(CreditType.GIFTCARD).get(0).getCurrency());
        Assert.assertEquals(1, balanceAmounts.get(CreditType.CASH).size());
        Assert.assertEquals(Long.valueOf(50), Long.valueOf(balanceAmounts.get(CreditType.CASH).get(0).getAmount()));
        Assert.assertEquals(2, balanceAmounts.get(CreditType.PROMOTION).size());

    }
}
