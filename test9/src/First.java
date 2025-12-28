public class First {
     public static void main(String[] args) throws InterruptedException {
            Object lock = new Object();
            Object waitObj = new Object();
            Thread thread = new Thread(() -> {
                try {
                        synchronized (lock) {
                        Thread.sleep(1000);
                         }
                        synchronized (waitObj) {
                            waitObj.wait();
                        }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            System.out.println(thread.getState());

            synchronized (lock) {
                thread.start();
                System.out.println(thread.getState());
                Thread.sleep(100);
                System.out.println(thread.getState());

            }

            Thread.sleep(150);
            System.out.println(thread.getState());
            Thread.sleep(1200);
            System.out.println(thread.getState());
            synchronized (waitObj) {
                waitObj.notify();
            }
            thread.join();
            System.out.println(thread.getState());
     }
}
