package thread_basics;

class Message {
    private String message;
    private boolean empty = true;

    public synchronized String read() {
        while(empty) {
            try {
                wait();
            } catch(InterruptedException ignored) {

            }

        }
        empty = true;
        notifyAll();
        return message;
    }

    public synchronized void write(String message) {
        while(!empty) {
            try {
                wait();
            } catch(InterruptedException ignored) {

            }

        }
        empty = false;
        this.message = message;
        notifyAll();
    }
}
