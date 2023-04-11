import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Explorer extends Thread {

    private final ExplorerMonitor explorerMonitor;
    private final CounterMonitor counterMonitor;

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
            } catch (Exception ignore) {
            }
        }
    }

    private void doSingleTask() throws InterruptedException {
        var file = this.explorerMonitor.pop();
        this.explorerMonitor.startWork();
        if (file.isDirectory()) {
            this.exploreDir(file);
        } else {
            this.exploreFile(file);
        }
        this.explorerMonitor.stopWork();
    }

    public void run(){
        while(true) {
            try {
                this.doSingleTask();
            } catch (InterruptedException ignored){
                return;
            }
        }
    }
}
