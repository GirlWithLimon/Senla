public class Second{
    private static final Object lock = new Object();
    private static boolean firstThreadTurn = true;
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    while (!firstThreadTurn) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    System.out.println("Поток-1");
                    firstThreadTurn = false;
                    lock.notifyAll();
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    while (firstThreadTurn) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    System.out.println("Поток-2");
                    firstThreadTurn = true;
                    lock.notifyAll();
                }
            }
        } );
        thread1.start();
        thread2.start();
    }
}
