import java.io.File;
import java.util.List;
import java.util.Map;

public class ThreadedExplorer {
    private final ExplorerMonitor explorerMonitor;
    private final CounterMonitor counterMonitor;

    public ThreadedExplorer(String startingDir, int nBuckets, int maxLines, int maxFiles) {
        this.explorerMonitor = new ExplorerMonitor();
        this.explorerMonitor.add(new File(startingDir));
        this.counterMonitor = new CounterMonitor(nBuckets, maxLines, maxFiles);
    }

    public boolean isDone() {
        return this.explorerMonitor.isDone();
    }
    public boolean isStopped() {
        return this.explorerMonitor.isStopped();
    }

    public void start(int nThreads){
        this.explorerMonitor.restart();
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

    public void setOnStoppedCallback(Runnable r) {
        this.explorerMonitor.setOnStoppedCallback(r);
    }

    public List<FileEntry> getTopFiles(){
        return this.counterMonitor.getTopFiles();
    }

    public Map<Integer, Integer> getBuckets(){
        return this.counterMonitor.getBuckets();
    }
}
