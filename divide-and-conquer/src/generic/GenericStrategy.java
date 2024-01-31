package generic;

public interface GenericStrategy {
    int getMinSize();

    int getParallelism();

    void setMinSize(int size);

    void setParallelism(int parallelism);

    String toString(boolean minSize, boolean parallelism);

    boolean isSequential();
}
