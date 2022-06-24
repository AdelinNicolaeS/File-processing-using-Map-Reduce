public abstract class Task {
    private final String nameOfFile;

    public Task(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }
}
