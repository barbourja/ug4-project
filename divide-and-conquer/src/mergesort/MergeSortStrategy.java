package mergesort;

public interface MergeSortStrategy<T extends Comparable<T>> {
    T[] execute(T[] arrToSort);
    void execute(T[] arrToSort, int start, int end);

}
