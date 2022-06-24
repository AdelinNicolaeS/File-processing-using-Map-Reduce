import java.util.List;

public class ReduceTask<K, V> extends Task {
    private final List<MapReturn<K, V>> mapReturnList;
    private ReduceReturn reduceReturn;

    public ReduceTask(String nameOfFile, List<MapReturn<K, V>> mapReturnList) {
        super(nameOfFile);
        this.mapReturnList = mapReturnList;
    }

    public List<MapReturn<K, V>> getMapReturnList() {
        return mapReturnList;
    }

    public void setReduceReturn(ReduceReturn reduceReturn) {
        this.reduceReturn = reduceReturn;
    }
    public ReduceReturn getReduceReturn() {
        return reduceReturn;
    }
}

