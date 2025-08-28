package thread_basics;

import java.util.concurrent.ArrayBlockingQueue;

class MyConsumer implements Runnable {
    private final ArrayBlockingQueue<String> buffer;
    private final String color;

    public MyConsumer(ArrayBlockingQueue<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    public void run() {

        while(true) {
            synchronized (buffer) {
                try {
                    if (buffer.isEmpty()) {
                        continue;
                    }

                    if (buffer.peek().equals(Main.EOF)) {
                        System.out.println(color + "Exiting");
                        break;
                    } else {
                        System.out.println(color + "Removed " + buffer.take());
                    }
                } catch (InterruptedException ignored) {

                }
            }
        }
    }
}
