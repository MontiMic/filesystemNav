public class Monitor {
    private int value;
    private boolean available;

    public Monitor(){
        available = false;

    }
    public synchronized void set(int v){
        value = v;
        available = true;
        notifyAll();
    }
    public synchronized int get() {
        while (!available){
            try {
                wait();
            } catch (InterruptedException ex){}
        }
        return value;
    }
}
