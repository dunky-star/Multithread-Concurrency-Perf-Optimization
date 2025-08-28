package thread_basics;

public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(ThreadColor.ANSI_RESET + "Hello from MyRunnable's implementation of run()");
    }
}
