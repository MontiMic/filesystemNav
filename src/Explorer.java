import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class Explorer extends Thread {

    private final File dir;
    public void run(){
        for (File file : Objects.requireNonNull(dir.listFiles())){
            if (file.isDirectory()) {
                new Explorer(file).start();
            } else {
                if (file.getName().endsWith(".java")){
                    try {
                        System.out.println(file.getName() + "\n" + (int) Files.lines(file.toPath(), StandardCharsets.UTF_8).count() + " lines\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    public Explorer(File dir){
        this.dir = dir;
    }
}
