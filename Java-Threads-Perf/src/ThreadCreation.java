public class ThreadCreation {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("We are in Thread: " + Thread.currentThread().getName());
                System.out.println("The current thread priority is: " + Thread.currentThread().getPriority());
            }
        });
        thread.setName("Worker-Thread-1");
        thread.setPriority(Thread.MAX_PRIORITY);
        System.out.println("We are in Thread: " + Thread.currentThread().getName() + " before starting a new Thread");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("An exception has been caught in thread: " + t.getName() + " with message: " + e.getMessage());
            }
        });
        thread.start();
        System.out.println("We are in Thread: " + Thread.currentThread().getName() + " after starting a new Thread");
        Thread.sleep(10000); // Main thread sleeps for 10 seconds
    }
}
