package thread_basics;

import java.util.Random;

class Reader implements Runnable {
    private final Message message;

    public Reader(Message message) {
        this.message = message;
    }

    public void run() {
        Random random = new Random();
        for(String latestMessage = message.read(); !latestMessage.equals("Finished");
            latestMessage = message.read()) {
            System.out.println(latestMessage);
            try {
                Thread.sleep(random.nextInt(10000));
            } catch(InterruptedException ignored) {

            }
        }
    }
}
