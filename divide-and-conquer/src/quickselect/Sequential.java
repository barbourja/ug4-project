package quickselect;

import generic.GenericStrategy;

import static quickselect.Utils.random_partition;

public class Sequential<T extends Comparable<T>> implements QuickSelectStrategy<T>{

    protected final int MIN_SEQUENCE_SIZE = 1;
    protected final int DIVISION_FACTOR = 1;

    public Sequential() {}

    @Override
    public T execute(T[] searchArr, int k) {
        return execute(searchArr, 0, searchArr.length - 1, k);
    }

    @Override
    public T execute(T[] arr, int start, int end, int k) { // k in interval [0, (end - start)] i.e doesn't exceed the index bounds of the subarray slice
        if (k < 0 || k > (end - start)) {
            throw new RuntimeException("Invalid value of k!");
        }
        int pivotIndex = random_partition(arr, start, end);
        int adjustedPivotIndex = pivotIndex - start; // normalize value to treat subarray start index as 0

        if (adjustedPivotIndex == k) {
            return arr[pivotIndex];
        }
        else if (k < adjustedPivotIndex) {
            return execute(arr, start, pivotIndex - 1, k);
        }
        else {
            return execute(arr, pivotIndex + 1, end, k - (adjustedPivotIndex + 1));
        }
    }

    @Override
    public int getMinSize() {
        return MIN_SEQUENCE_SIZE;
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
        // no min size - do nothing
    }

    @Override
    public void setParallelism(int parallelism) {
        // sequential - do nothing
    }

    @Override
    public boolean isSequential() {
        return true;
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("QuickSelect Sequential ");
        if (minSize) {
            sb.append("| Minimum Array Size = " + MIN_SEQUENCE_SIZE);
        }
        return sb.toString();
    }
}
