### Billing Bank

Table of Contents
==================

<!--ts-->
   * [Introduction](#introduction)
   * [Considerations](#Considerations)
   * [Implementation Details](#implementation-details)
      * [App Architecture](#app-architecture)
      * [Concurrent Hashmap](#Concurrent-hashmaps)
      * [Synchronized block on customer ID](#synchronized-block-on-customer-id)
      * [Muti Threaded](#multi-threaded)
      * [Performance](#performance)
   * [Explore Rest APIs](#explore-rest-apis)
   * [Installation](#installation)
   * [Tests](#tests)
   * [Dependency](#dependency)
   * [Sample Calls](#sample-calls)
      * [Make Debit](#make-debit)
      * [Make Credit](#make-credit)
      * [Duplicate Transactions](#duplicate-transaction)
      * [Get Balance](#get-balance)
      * [Get Debit History](#get-debit-history)
      * [Make Credit in Multiple Currency](#make-credit-in-multiple-currency)
      * [Debit in different Currency Types](#debit-in-different-currency-types)
<!--te-->


Introduction
============

This is an implmentation of toy "bank" that keeps track of credit and debit transactions for a multinational bank that works across many countries and allows the user to check balance at any time.

Considerations
===============

Rules that were adhered to while implementing the solution :

    - Assume that the application is multi-threaded and the code needs to be performant and correct under various workloads
    - You will need to complete the solution assuming that a traditional transactional sql database is not an option(no in-memory databases please.
    - The business rules (in order of rules and priority defined within each rule)
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


Implementation Details
======================


App Architecture
----------------

    -The application is divided into services by functionality, where the validationservice takes care of everything related to validations, credit service about credit and so on. This helps when debugging issues where each component is in it's own silo.
    -I have used a ConcurrentHashMap as my primary data store, which stores all account information by customerId. More details regarding this can be found below.
    -Multiple currencies are supported as separate transactions.
    -The save to the datastore is synchronized on customerId to help maintain conisistency in response.
    -Unit tests have been written for all major workflows in the code.

Concurrent Hashmap
------------------

To keep track of the customerid, credit and debit transactions; I decided to use concurrent hashmaps over all/any other data structures available.

- Concurrent hashmap is thread safe without synchronizing the whole map (minimal wait time).
- Reads can happen very fast while write is done with a lock.
- There is no locking at the object level.
- The locking is at a much finer granularity at a hashmap bucket level.
- For Java8, ConcurrentHashMap uses balanced tree (red-black tree) instead of plain linked list for improved performance (search, insertion, and deletion are all worst case 𝑂(log𝑛).)
- ConcurrentHashMap doesn’t throw a ConcurrentModificationException if one thread tries to modify it while another is iterating over it.
- Concurrent hashmaps are faster in write/retrievals on an average as compared to hashmaps.


Synchronized block on customer ID
------------------------------------
I have used the putIfAbsent method on the concurrentLock. If for the same customer, there are 2 concurrent create record requests that come in, one of them is going to fail. This is where consistency of data is chosen over api response not failing.
Considering this is a banking application, consistency of data was given priority.
On an update to the customer record, a synchronized block has been used, to lock on the customer object in the concurrent hashmap.
This is to ensure, that while a record is being updated, any concurrent read requests wait till the update is complete, so that they receive updated information.


I've decided to go against java concurrent lock in interest of time but for more number of threads, I would use lock instead of synchronized()

Explore Rest APIs
=================

The Bank defines following CRU APIs.

    POST /customer/ABC/credit
        
        Sample payload : {
                         	"transactionId":"1111",
                         	"money": {
                         		"amount":20,
                         		"currency":"USD"
                         	},
                         	"creditType":"PROMOTION"
                         	
                         }


    GET /customer/ABC/balance

    POST /customer/ABC/debit
    
    Sample payload : {
                     	"invoiceId":"1111",
                     	"money": {
                     		"amount":15,
                     		"currency":"USD"
                     	}
                     	
                     }
                     
    GET /customer/ABC/debitHistory


Installation
============

To install/run the toy bank, use the following

<pre> | Use case | Command | |----------|:--------------------| | Build | ./gradlew build | | Run | ./gradlew bootRun | | Run Tests| ./gradlew test | </pre>

