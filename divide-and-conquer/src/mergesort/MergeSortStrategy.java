package mergesort;

import generic.GenericStrategy;

public interface MergeSortStrategy<T extends Comparable<T>> extends GenericStrategy {
    T[] execute(T[] arrToSort);

    void execute(T[] arrToSort, int start, int end);
}
