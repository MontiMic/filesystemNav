import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class Monitor {
    private final Queue<File> queue = new LinkedList<>();
    private boolean stopped;
    private int workingThreads;
    public synchronized void stop(){
        this.stopped = true;
        notifyAll();
    }
    private boolean shouldStop(){
        return this.workingThreads <= 0 && this.queue.isEmpty();
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
