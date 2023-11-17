package mergesort;

import static mergesort.Utils.insertionSortInPlace;
import static mergesort.Utils.merge;

public class Sequential<T extends Comparable<T>> implements MergeSortStrategy<T>{

    protected final int MIN_ARRAY_SIZE;

    public Sequential(int minArraySize) {
        this.MIN_ARRAY_SIZE = minArraySize;
    }

    public void execute(T[] arrToSort, int start, int end) { // interval is [start, end)
        if (end - start <= MIN_ARRAY_SIZE) { // Base case
            insertionSortInPlace(arrToSort, start, end);
        }
        else {
            int midPoint = (start + end) / 2;
            execute(arrToSort, start, midPoint);
            execute(arrToSort, midPoint, end);
            merge(arrToSort, start, midPoint, end);
        }
    }

    public T[] execute(T[] arrToSort) {
        if (arrToSort.length <= MIN_ARRAY_SIZE) { // Base case
            insertionSortInPlace(arrToSort, 0, arrToSort.length);
            return arrToSort;
        }
        else { // Recursive case
            int midPoint = arrToSort.length / 2;
            execute(arrToSort, 0, midPoint);
            execute(arrToSort, midPoint, arrToSort.length);
            return merge(arrToSort, 0, midPoint, arrToSort.length);
        }
    }
}
