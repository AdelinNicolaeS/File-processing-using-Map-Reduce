import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class ReduceWorker extends Worker<ReduceTask<Integer, String>>{

    public ReduceWorker(BlockingQueue<ReduceTask<Integer, String>> queue) {
        super(queue);
    }

    @Override
    public void run() {
        while(!queue.isEmpty()) {
            ReduceTask<Integer, String> task = queue.poll();
            if(task == null) {
                return;
            }
            // create the final map with all the occurrences
            Map<Integer, Integer> finalMap = Collections.synchronizedMap(new HashMap<>());
            for(var mapReturn : task.getMapReturnList()) {
                var map = mapReturn.getMap();
                for(Map.Entry<Integer, Integer> element : map.entrySet()) {
                    finalMap.put(element.getKey(), finalMap.getOrDefault(element.getKey(), 0) + element.getValue());
                }
            }
            int maxLen = -1, occur = 0;
            double sum = 0, countElements = 0;
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            // compute maximum length and its occurrences
            for(Map.Entry<Integer, Integer> element : finalMap.entrySet()) {
                if(element.getKey() > maxLen) {
                    maxLen = element.getKey();
                    occur = element.getValue();
                }

                // compute rank
                FibonacciCalculator fibonacciCalculator = new FibonacciCalculator(element.getKey() + 1);
                forkJoinPool.execute(fibonacciCalculator);
                try {
                    sum += fibonacciCalculator.get() * element.getValue();
                    countElements += element.getValue();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            double rank = sum/countElements;

            // take just the name from the absolute path
            String[] split = task.getNameOfFile().split("/");
            task.setReduceReturn(new ReduceReturn(split[split.length - 1], rank, maxLen, occur));
        }
    }
}
