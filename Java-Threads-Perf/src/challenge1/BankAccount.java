package challenge1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Challenge 1: Create and start Threads
 * We could have two people using a joint bank account at the same time. Create and start
 * two threads that use the bank account instance and an initial balance of $1000.00. One will
 * deposit $300.00 into the bank account and then withdraws $50.00. The other will deposit $203.75
 * and then withdraw $100.00.
 * Challenge 2: Make the BankAccount class Threadsafe using to synchronize keyword.
 */

public class BankAccount {
    private double balance;
    private final String accountNumber;
    private final Lock lock;

    public BankAccount(String accountNumber, double initialBalance) {
        this.balance = initialBalance;
        this.accountNumber = accountNumber;
        this.lock = new ReentrantLock(); // Initialize re-entrant lock.
    }

    //    public  void deposit(double amount) {
    //      synchronized(this){
    //        balance += amount;
    //      }
    //    }
    //
    //    public synchronized void withdraw(double amount) {
    //      synchronized(this){
    //        balance -= amount;
    //      }
    //    }

    public void deposit(double amount){
        boolean status = false;
        try {
            if(lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    balance += amount;
                    status = true;
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println("Could not get the lock");
            }

        } catch(InterruptedException e) {
            // do something here
        }
        System.out.println("Transaction status " + status);
    }

    public void withdraw(double amount){
        boolean status = false;
        try {
            if(lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    balance -= amount;
                    status = true;
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println("Could not get the lock");
            }

        } catch(InterruptedException e) {
            // do something here
        }
        System.out.println("Transaction status " + status);
    }

    public String getAccountNumber(){
        return accountNumber;
    }

    public void printAccountNumber(){
        System.out.println("Account number = " + accountNumber);
    }
}
