public class ThreadCreation2 {
    public static void main(String[] args){
        NewThread t1 = new NewThread();
        t1.start();
    }

    public static class NewThread extends Thread {
        @Override
        public void run() {
            System.out.println("Hello from "+ Thread.currentThread().getName());
            System.out.println("Thread id: " + this.threadId());
        }
    }
}
