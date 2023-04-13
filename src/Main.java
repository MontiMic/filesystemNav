import javax.swing.*;

public class Main {
    static void CLI(String startingDir, int nBuckets, int maxLines, int maxFiles, int threads) {
        ThreadedExplorer threadedExplorer = new ThreadedExplorer(startingDir, nBuckets, maxLines, maxFiles);
        var t = System.currentTimeMillis();
        threadedExplorer.setOnStoppedCallback(() -> {
            System.out.println("Time: " + (System.currentTimeMillis() - t));
            for (var e : threadedExplorer.getBuckets().entrySet()) {
                System.out.println(e);
            }
            for (var f : threadedExplorer.getTopFiles()) {
                System.out.println(f);
            }

        });
        if (threads > 0) {
            threadedExplorer.start(threads);
        } else {
            threadedExplorer.start();
        }
    }

    static void GUI() {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().getRootPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length >= 4) {
            int threads = -1;
            if (args.length >= 5) {
                threads = Integer.parseInt(args[4]);
            }
            CLI(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), threads);
        } else {
            GUI();
        }
    }
}