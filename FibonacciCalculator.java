import java.util.concurrent.RecursiveTask;

// source: https://ocw.cs.pub.ro/courses/apd/laboratoare/07

public class FibonacciCalculator extends RecursiveTask<Integer> {
    private final int n;

    public FibonacciCalculator(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n == 0 || n == 1) {
            return n;
        }

        FibonacciCalculator first = new FibonacciCalculator(n - 1);
        FibonacciCalculator second = new FibonacciCalculator(n - 2);

        first.fork();
        second.fork();

        return first.join() + second.join();
    }
}