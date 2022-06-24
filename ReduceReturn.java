public class ReduceReturn {
    private final String nameOfFile;
    private final double rank;
    private final int maxLen;
    private final int occurrences;

    public ReduceReturn(String nameOfFile, double rank, int maxLen, int occurrences) {
        this.nameOfFile = nameOfFile;
        this.rank = rank;
        this.maxLen = maxLen;
        this.occurrences = occurrences;
    }

    public double getRank() {
        return rank;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public int getOccurrences() {
        return occurrences;
    }

}
