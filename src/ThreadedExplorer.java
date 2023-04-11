import java.io.File;
import java.util.List;

public class ThreadedExplorer {
    private final ExplorerMonitor explorerMonitor;
    private final CounterMonitor counterMonitor;

    public ThreadedExplorer(String startingDir, int nBuckets, int maxLines, int maxFiles) {
        this.explorerMonitor = new ExplorerMonitor();
        this.explorerMonitor.add(new File(startingDir));
        this.counterMonitor = new CounterMonitor(nBuckets, maxLines, maxFiles);
    }

    public void start(int nThreads){
        for (int i = 0; i < nThreads; i++) {
            new Explorer(this.explorerMonitor, this.counterMonitor).start();
        }
    }

    public void start(){
        this.start(Runtime.getRuntime().availableProcessors() + 1);
    }

    public void stop(){
        this.explorerMonitor.stop();
    }

    public List<FileEntry> getTopFiles(){
        return this.counterMonitor.getTopFiles();
    }

    public List<Integer> getBuckets(){
        return this.counterMonitor.getBuckets();
    }
    public void await(){
        try {
            this.explorerMonitor.awaitStop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
