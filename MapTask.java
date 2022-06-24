public class MapTask<K, V> extends Task{
    private final int offset;
    private final int size;
    MapReturn<K, V> out;

    public MapTask(String nameOfFile, int offset, int size) {
        super(nameOfFile);
        this.offset = offset;
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public MapReturn<K, V> getOut() {
        return out;
    }

    public void setOut(MapReturn<K, V> out) {
        this.out = out;
    }

}
