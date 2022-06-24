import java.util.concurrent.BlockingQueue;

public abstract class Worker<T> extends Thread {
    protected BlockingQueue<T> queue;

    public Worker(BlockingQueue<T> queue) {
        this.queue = queue;
    }
}
