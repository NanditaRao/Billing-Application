package com.netflix.billing.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * This is the main entry-point for app startup. Running the main method should start up the server.
 */
@SpringBootApplication
public class BillingbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingbankApplication.class, args);
	}

}

