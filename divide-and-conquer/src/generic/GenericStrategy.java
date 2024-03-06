package generic;

public interface GenericStrategy {
    int getMinSize();

    int getParallelism();

    int getDivisionFactor();

    GenericStrategy getBaseCaseStrategy();

    void setMinSize(int size);

    void setParallelism(int parallelism);

    String toString(boolean minSize, boolean parallelism);

    boolean isSequential();
}
