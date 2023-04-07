import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Explorer extends Thread {

    private final ExplorerMonitor explorerMonitor;
    private final CounterMonitor counterMonitor;
    public int counter = 0;

    public Explorer(ExplorerMonitor explorerMonitor, CounterMonitor counterMonitor){
        this.explorerMonitor = explorerMonitor;
        this.counterMonitor = counterMonitor;
    }

    private void exploreDir(File dir){
        Arrays.stream(dir.listFiles()).forEach(this.explorerMonitor::add);
    }

    private void exploreFile(File file){
        if (file.getName().endsWith(".java")){
            try (var lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                this.counterMonitor.countedFile(file, lines.count());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void run(){
        while(true) {
            try {
                var file = this.explorerMonitor.pop();
                this.explorerMonitor.startWork();
                this.counter++;
                if (file.isDirectory()) {
                    this.exploreDir(file);
                } else {
                    this.exploreFile(file);
                }
                this.explorerMonitor.stopWork();
            } catch (InterruptedException ignored){
                return;
            }
        }
    }
}
