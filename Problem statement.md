# Billing Bank

This is a homework exercise to implement a toy "bank" that keeps track of credit and debit transactions for a _multinational_ 
bank that works across many countries and allows the user to check balance at any time.

There are methods in the BankController class that need to work as the final work product. Those methods represent the 
transactions that a user can perform on the bank. Assume that the application is **multi-threaded** and the code needs to be 
performant *and* correct under various workloads. For e.g. One request is trying to add credit and another is trying to 
debit the account. 

You have to also make sure that your code follows the business rules given below. The best way to do that is to write 
tests that prove that the rules are being followed. 

Your submitted code should build, run and have tests that ensure the correctness of your solution. 

### Architecture

Feel free to add packages as you see fit for additional code, but don't change packages or rename any existing code. You will need
to complete the solution assuming that a traditional transactional sql database is not an option(no in-memory databases please.)

**Spring** 

The project uses [Spring Framework](https://spring.io/) for it's dependency management and it's request processing framework. 
Some familiarity with dependency injection would be useful. For this project being able to declare a dependency in 
``ApplicationConfig`` and adding ``@Autowired`` where the dependency is being used is really all that is required. There
are examples in ``ApplicationConfig`` and ``BankController`` on how to do that.   

### Business Rules

Your solution must respect these business rules while executing a given request.

- Credits of different types (``com.netflix.billing.bank.controller.wire.CreditType``) cannot be merged together. 
    For e.g. if someone adds a $50 credit of ``CreditType.GIFTCARD``, $50 of ``CreditType.CASH`` and then $10 of ``CreditType.GIFTCARD``, 
    then their balance should reflect 2 credits: $60 of ``CreditType.GIFTCARD`` and $50 of ``CreditType.CASH`` 
- Credits need to be consumed in priority order defined by the ``CreditType`` enum. For e.g. if you have a credit of $10
    ``CreditType.GIFTCARD`` and $20 ``CreditType.CASH``, and a debit request comes in for $15, then you 
    would consume $10 from the ``CreditType.GIFTCARD`` credit and then $5 from the ``CreditType.CASH`` credit.
- Credits need to be consumed in the order they were applied. Which means that if I apply a $10 ``CreditType.GIFTCARD`` 
    credit on 1/5 and a $20 ``CreditType.GIFTCARD`` credit on 1/10, and subsequently a debit request comes in on 1/25 
    for $20, the credit added on 1/5 should be consumed completely and the credit added on 1/10 should be consumed partially.
- If there is not enough credit to be consumed then you would return back an error.
- A credit is considered duplicate if the transactionId has already been applied for the same creditType for the same customer.
- A debit is considered duplicate if the invoiceId has been applied for a given customer.

### Build, Run and Test

The project uses [gradle](https://gradle.org/) for it's build lifecycle.

No prior knowledge of gradle is required to complete this assignment. If you import this project in a java IDE(Eclipse/IntelliJ), 
they should recognize the gradle files and offer to auto-import the dependencies for you. You can run the project by running 
the ``BillingbankApplication.main()`` method.

In case you want to use the command line, here are some useful commands that you can run from the root of the project.<pre>
  | Use case |     Command         | 
  |----------|:--------------------| 
  | Build    |  ./gradlew build    |
  | Run      |  ./gradlew bootRun  |
  | Run Tests|  ./gradlew test     |
</pre>
 
 




