package thread_basics;

public class Thread1 extends Thread {

    @Override
    public void run(){
        System.out.println(ThreadColor.ANSI_RED  + " Hello from " + currentThread().getName() );

        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            System.out.println(ThreadColor.ANSI_RED + "Another thread woke me up");
            // Return statement to handle the interrupt.
            // return;
        }
        System.out.println(ThreadColor.ANSI_RED + "5 sec have passed and I am awake");
    }

}
