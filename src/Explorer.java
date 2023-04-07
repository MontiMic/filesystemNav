import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Explorer extends Thread {

    private final Monitor monitor;

    public Explorer(Monitor monitor){
        this.monitor = monitor;
    }

    private void exploreDir(File dir){
        Arrays.stream(dir.listFiles()).forEach(this.monitor::add);
    }

    private void exploreFile(File file){
        if (file.getName().endsWith(".java")){
            try {
                System.out.println(file.getName() + "\n" + (int) Files.lines(file.toPath(), StandardCharsets.UTF_8).count() + " lines\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void run(){
        while(true) {
            try {
                var file = this.monitor.pop();
                this.monitor.startWork();
                if (file.isDirectory()) {
                    this.exploreDir(file);
                } else {
                    this.exploreFile(file);
                }
                this.monitor.stopWork();
            } catch (InterruptedException ignored){
                return;
            }
        }
    }
}
