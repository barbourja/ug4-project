package quickselect;

public interface QuickSelectStrategy<T extends Comparable<T>> {
    T execute(T[] searchArr, int k);
    T execute(T[] searchArr, int start, int end, int k);
}
