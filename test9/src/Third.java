import java.util.concurrent.*;
import java.util.Random;

public class Third {

    public static void main(String[] args) throws InterruptedException {
        int bufferSize = 5;
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(bufferSize);

        int threadBound = 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                0, threadBound,
                0L, TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );

        Runnable producerTask = () -> {
            Random random = new Random();
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    int number = random.nextInt(100);
                    buffer.put(number);
                    System.out.println("Генерирую: " + number +
                            " | Занято в буфере: " + buffer.size());
                  //  Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable consumerTask = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Integer number = buffer.take();
                    System.out.println("Потребляю: " + number +
                            " | Занято в буфере: " + buffer.size());
                   // Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        executor.submit(producerTask);
        executor.submit(consumerTask);

        Thread.sleep(10000);

        executor.shutdownNow();
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("Потоки не завершились корректно");
        }
    }
}