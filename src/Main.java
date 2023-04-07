import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        File file = new File(args[0]);
        var explorerMonitor = new ExplorerMonitor();
        var counterMonitor = new CounterMonitor(5, 1000);

        var t = System.currentTimeMillis();
        explorerMonitor.setOnStoppedCallback(() -> {
            System.out.println(file.getAbsolutePath());
            for (var b : counterMonitor.getBuckets()) {
                System.out.println(b);
            }
            for (var f : counterMonitor.getTopFiles(100)) {
                System.out.println(f);
            }
            System.out.println("Time: " + (System.currentTimeMillis() - t));
        });
        for (int i = 0; i < 1; i++) {
            explorerMonitor.add(file);
        }
        for (int i = 0; i < 9; i++) {
            new Explorer(explorerMonitor, counterMonitor).start();
        }
    }
}