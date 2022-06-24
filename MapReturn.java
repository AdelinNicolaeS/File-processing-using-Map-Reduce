import java.util.*;

public class MapReturn<K, V> {
    private final Map<K, K> map;
    private final List<V> list;
    private final String nameOfFile;

    public MapReturn(Map<K, K> map, List<V> list, String nameOfFile) {
        this.map = map;
        this.list = list;
        this.nameOfFile = nameOfFile;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }

    public Map<K, K> getMap() {
        return map;
    }

    public List<V> getList() {
        return list;
    }
}
