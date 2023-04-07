import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("/Users/mikim/Desktop/Uni/");
        Monitor monitor = new Monitor();
        monitor.add(file);
        for (int i = 0; i < 4; i++){
            new Explorer(monitor).start();
        }
    }
}