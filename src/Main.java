import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        ThreadedExplorer threadedExplorer = new ThreadedExplorer(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        var t = System.currentTimeMillis();
        threadedExplorer.start();
        threadedExplorer.await();
        System.out.println("Time: " + (System.currentTimeMillis() - t));
        for (var b : threadedExplorer.getBuckets()) {
            System.out.println(b);
        }
        for (var f : threadedExplorer.getTopFiles()) {
            System.out.println(f);
        }
    }
}