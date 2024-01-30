package quickselect;

public interface QuickSelectStrategy<T extends Comparable<T>> {
    T execute(T[] searchArr, int k);

    T execute(T[] arr, int start, int end, int k);

    int getMinSize();

    int getParallelism();

    void setMinSize(int size);

    void setParallelism(int parallelism);

    String toString(boolean minSize, boolean parallelism);
}
