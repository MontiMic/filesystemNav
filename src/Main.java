import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        File file = new File("/Users/mikim/Desktop/Uni/");
        explore(file);
    }
    //recursive function to explore a file system. Input: the starting directory
    static void explore(File dir){
        for (File file : Objects.requireNonNull(dir.listFiles())){
            if (file.isDirectory()) {
                explore(file);
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
}