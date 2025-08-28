package thread_basics;

import challenge1.BankAccount;
import challenge3.BankAccountTransfer;
import challenge3.Transfer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final String EOF = "EOF";
    public static void main(String[] args)
    {
        System.out.println(ThreadColor.ANSI_BLUE + "\nThreads construction in Java!");
        // Creating a thread using subclass.
        Thread thread1 = new Thread1();
        thread1.setName("== Thread 1 ==");
        // To invoke the run method in thread1.
        thread1.start();

        // Creating an anonymous thread.
        new Thread() {
            public void run(){
                System.out.println(ThreadColor.ANSI_GREEN + "Hello from the anonymous class thread.");
            }
        }.start();

        // Another way of creating Thread using Runnable
        Thread myRunnableThread = new Thread(new MyRunnable(){

            // Creating anonymous class with Runnable
            @Override
            public void run(){
                System.out.println(ThreadColor.ANSI_GREEN + "Hello from the anonymous class implementation of run()");
                // To join a thread without waiting.
                try {
                    thread1.join(3000);
                    System.out.println(ThreadColor.ANSI_RED + "Another thread terminated, or timed out so I'm running again.");
                } catch (InterruptedException e){
                    System.out.println(ThreadColor.ANSI_RED + "I couldn't wait after all. I was interrupted");
                }
            }
        });
        myRunnableThread.start();
        // Interrupting a thread.
        // thread1.interrupt();
        System.out.println(ThreadColor.ANSI_BLUE + "Hello again from the main thread.");


        Countdown countdown = new Countdown();

        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread 2");

        t1.start();
        t2.start();

        // Implementing Deadlocks,-wait,-notify-and-notifyAll
        Message message = new Message();
        (new Thread(new Writer(message))).start();
        (new Thread(new Reader(message))).start();


        // Producer - Consumer paradigm && Thread pools using ArrayBlockingQueue.
        ArrayBlockingQueue<String> buffer = new ArrayBlockingQueue<String>(6);

        // ExecutorService - Thread pools (To manage thread automatically).
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_YELLOW);
        MyConsumer consumer1 = new MyConsumer(buffer, ThreadColor.ANSI_PURPLE);
        MyConsumer consumer2 = new MyConsumer(buffer, ThreadColor.ANSI_CYAN);

        executorService.execute(producer);
        executorService.execute(consumer1);
        executorService.execute(consumer2);

        executorService.shutdown();

        // Challenge 1: Joint Bank Account
        BankAccount account = new BankAccount("903-60402", 1000.00);

//        Thread thThread1 = new Thread(){
//            public void run(){
//                account.deposit(300.00);
//                account.withdrawal(50.0);
//            }
//        };
//
//        Thread thThread2 = new Thread(){
//            public void run(){
//                account.deposit(203.75);
//                account.withdrawal(100);
//            }
//
//        };

        Thread thThread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                account.deposit(300.00);
                account.withdraw(50.00);
                System.out.println("\nTransaction completed for account " + account.getAccountNumber());
            }
        });

        Thread thThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                account.deposit(203.75);
                account.withdraw(100.00);
                System.out.println("Transaction completed for account " + account.getAccountNumber());
            }
        });

        thThread1.start();
        thThread2.start();

        // Challenge 3: Transfer between bank accounts.
        BankAccountTransfer account1 = new BankAccountTransfer("12345-678", 500.00);
        BankAccountTransfer account2 = new BankAccountTransfer("98765-432", 1000.00);

        new Thread(new Transfer(account1, account2, 10.00), "Transfer1").start();
        new Thread(new Transfer(account2, account1, 55.88), "Transfer2").start();


    }


}