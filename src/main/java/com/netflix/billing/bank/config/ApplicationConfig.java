package com.netflix.billing.bank.config;

import com.netflix.billing.bank.model.CustomerBankAccount;
import com.netflix.billing.bank.model.CustomerBankAccountEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Your Spring dependency configurations go here.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public CustomerBankAccountEntity customerBalance() {
        CustomerBankAccountEntity customerBankAccountEntity = new CustomerBankAccountEntity();
        customerBankAccountEntity.setCustomerBankAccount(new ConcurrentHashMap<>());
        return customerBankAccountEntity;
    }

}
