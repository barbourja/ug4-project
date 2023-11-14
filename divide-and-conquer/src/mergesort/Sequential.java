package mergesort;

import java.util.ArrayList;
import java.util.List;

import static mergesort.Utils.insertionSortInPlace;
import static mergesort.Utils.merge;

public class Sequential<T extends Comparable<T>> implements MergeSortStrategy<T>{

    protected final int MIN_ARRAY_SIZE;

    public Sequential(int minArraySize) {
        this.MIN_ARRAY_SIZE = minArraySize;
    }

    public List<T> execute(List<T> arrToSort) {
        if (arrToSort.size() <= MIN_ARRAY_SIZE) { // Base case
            insertionSortInPlace(arrToSort);
            return arrToSort;
        }
        else { // Recursive case
            int midPoint = arrToSort.size() / 2;
            List<T> lowerHalf = new ArrayList<>(arrToSort.subList(0, midPoint));
            List<T> upperHalf = new ArrayList<>(arrToSort.subList(midPoint, arrToSort.size()));

            return merge(execute(lowerHalf), execute(upperHalf));
        }
    }
}
