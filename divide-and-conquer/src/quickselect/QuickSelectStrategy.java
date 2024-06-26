package quickselect;

import generic.GenericStrategy;

public interface QuickSelectStrategy<T extends Comparable<T>> extends GenericStrategy {
    T execute(T[] searchArr, int k);

    T execute(T[] arr, int start, int end, int k);

}
