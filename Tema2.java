import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Tema2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        int numberOfWorkers = Integer.parseInt(args[0]);
        String inputFile = args[1];
        String outputFile = args[2];

        File file = new File(inputFile);
        Scanner scanner = new Scanner(file);
        int bytesSize = scanner.nextInt();
        scanner.nextInt();

        List<MapTask<Integer, String>> mapTasks = new ArrayList<>();
        BlockingQueue<MapTask<Integer, String>> mapTasksQueue = new LinkedBlockingQueue<>();
        while(scanner.hasNextLine()) {
            String lineWithNameOfFile = scanner.nextLine();
            File newFile = new File(lineWithNameOfFile);
            int length = (int) newFile.length();
            for(int i = 0; i < length; i += bytesSize) {
                MapTask<Integer, String> mapTask;
                // establish length of our fragment
                if(i + bytesSize < length) {
                    mapTask = new MapTask<>(lineWithNameOfFile, i, bytesSize);
                } else {
                    mapTask = new MapTask<>(lineWithNameOfFile, i, length - i);
                }
                mapTasks.add(mapTask);
                mapTasksQueue.add(mapTask);
            }
        }
        List<MapWorker> mapWorkers = new ArrayList<>();
        for(int i = 0; i < numberOfWorkers; i++) {
            MapWorker mapWorker = new MapWorker(mapTasksQueue);
            mapWorkers.add(mapWorker);
        }
        for(var worker : mapWorkers) {
            worker.start();
        }
        for(var worker : mapWorkers) {
            worker.join();
        }

        // create the new threads for Reduce
        List<ReduceTask<Integer, String>> reduceTasks = new ArrayList<>();
        String currentFile = mapTasks.get(0).getOut().getNameOfFile();
        List<MapReturn<Integer, String>> currentList = new ArrayList<>();
        BlockingQueue<ReduceTask<Integer, String>> reduceTasksQueue = new LinkedBlockingQueue<>();

        for(var mapTask : mapTasks) {
            String threadFile = mapTask.getOut().getNameOfFile();
            if (currentFile.compareTo(threadFile) != 0) {
                ReduceTask<Integer, String> reduceTask = new ReduceTask<>(currentFile, currentList);
                reduceTasks.add(reduceTask);
                reduceTasksQueue.add(reduceTask);

                currentFile = threadFile;
                currentList = new ArrayList<>();
            }
            currentList.add(mapTask.getOut());
        }
        ReduceTask<Integer, String> reduceTask = new ReduceTask<>(currentFile, currentList);
        reduceTasks.add(reduceTask);
        reduceTasksQueue.add(reduceTask);

        List<ReduceWorker> reduceWorkers = new ArrayList<>();
        for(int i = 0; i < numberOfWorkers; i++) {
            reduceWorkers.add(new ReduceWorker(reduceTasksQueue));
        }

        for(var worker : reduceWorkers) {
            worker.start();
        }
        for(var worker : reduceWorkers) {
            worker.join();
        }

        // sort the result according to the rank
        reduceTasks.sort((rt1, rt2) -> -Double.compare(rt1.getReduceReturn().getRank(), rt2.getReduceReturn().getRank()));

        FileWriter fileWriter = new FileWriter(outputFile);
        for(var task : reduceTasks) {
            String name = task.getReduceReturn().getNameOfFile();
            String rank = String.format("%.2f", task.getReduceReturn().getRank());
            int maxLen = task.getReduceReturn().getMaxLen();
            int occ = task.getReduceReturn().getOccurrences();
            fileWriter.write(name + ',' + rank + ',' + maxLen + ',' + occ + '\n');
        }
        fileWriter.close();
    }
}
