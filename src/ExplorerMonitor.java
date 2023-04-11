import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class ExplorerMonitor {
    private final Queue<File> queue = new LinkedList<>();
    private CountDownLatch stopLatch;
    private boolean stopped;
    private int workingThreads;

    public ExplorerMonitor() {
        restart();
    }

    public void restart(){
        if (this.stopped)
            return;
        stopLatch = new CountDownLatch(1);
        stopped = false;
    }
    public synchronized void stop(){
        if (this.stopped)
            return;
        this.stopped = true;
        notifyAll();
        stopLatch.countDown();
    }
    public void awaitStop() throws InterruptedException {
        stopLatch.await();
    }
    public boolean isDone() {
        return this.queue.isEmpty();
    }

    private boolean shouldStop(){
        return this.workingThreads <= 0 && this.isDone();
    }
    public synchronized void add(File file){
        this.queue.add(file);
        notify();
    }
    public synchronized void startWork(){
        this.workingThreads = this.workingThreads + 1;
    }
    public synchronized void stopWork(){
        this.workingThreads = this.workingThreads - 1;
        if (this.shouldStop()){
            this.stop();
        }
    }
    public synchronized File pop() throws InterruptedException {
        while (this.queue.isEmpty() && !this.stopped){
            wait();
        }
        if (this.stopped){
            throw new InterruptedException();
        }
        return this.queue.poll();
    }
}
