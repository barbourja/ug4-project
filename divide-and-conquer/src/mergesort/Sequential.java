package mergesort;

import generic.GenericStrategy;

import static mergesort.Utils.insertionSortInPlace;
import static mergesort.Utils.merge;

public class Sequential<T extends Comparable<T>> implements MergeSortStrategy<T>{

    protected int MIN_ARRAY_SIZE;
    protected final int DIVISION_FACTOR = 2;

    public Sequential(int minArraySize) {
        this.MIN_ARRAY_SIZE = minArraySize > 0 ? minArraySize : 1;
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

    @Override
    public int getMinSize() {
        return MIN_ARRAY_SIZE;
    }

    @Override
    public int getParallelism() {
        return 1;
    }

    @Override
    public int getDivisionFactor() {
        return DIVISION_FACTOR;
    }

    @Override
    public GenericStrategy getBaseCaseStrategy() {
        return null; // no base case strategy
    }


    @Override
    public void setMinSize(int size) {
        if (size >= 1) {
            MIN_ARRAY_SIZE = size;
        }
    }

    @Override
    public void setParallelism(int parallelism) {
        // do nothing
    }

    @Override
    public boolean isSequential() {
        return true;
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("MergeSort Sequential ");
        if (minSize) {
            sb.append("| Minimum Array Size = " + MIN_ARRAY_SIZE);
        }
        return sb.toString();
    }
}
