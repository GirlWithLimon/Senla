import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Four implements Runnable {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public Four() { }
    @Override
    public void run() {
        System.out.println("Текущее время: " + LocalDateTime.now().format(formatter));
    }

    public static void main(String[] args) {
        int n = 5;

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Four task = new Four();
        scheduledExecutorService.scheduleAtFixedRate(task, 0, n, TimeUnit.SECONDS);

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduledExecutorService.shutdownNow();
    }
}