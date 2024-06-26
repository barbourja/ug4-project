package mergesort;

import generic.GenericStrategy;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import static mergesort.Utils.merge;

public class ForkJoin<T extends Comparable<T>> implements MergeSortStrategy<T>{

    protected int MIN_ARRAY_SIZE;
    protected final int DIVISION_FACTOR = 2;
    protected int PARALLELISM;
    protected final MergeSortStrategy<T> BASE_CASE_STRATEGY;

    public ForkJoin(int minArraySize, int parallelism, MergeSortStrategy<T> baseCaseStrategy) {
        this.MIN_ARRAY_SIZE = minArraySize > 0 ? minArraySize : 1;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private class MergeSortTask extends RecursiveAction {

        private final T[] arrToSort;
        private final int start;
        private final int end;

        public MergeSortTask(T[] arrToSort, int start, int end) { // interval is [start, end)
            this.arrToSort = arrToSort;
            this.start = start;
            this.end = end;
        }

        private void computeDirectly() {
            BASE_CASE_STRATEGY.execute(arrToSort, start, end);
        }

        @Override
        protected void compute() {
            if (end - start <= MIN_ARRAY_SIZE) {
                computeDirectly();
            }
            else {
                int midPoint = (start + end) / 2;
                MergeSortTask lowerHalfTask = new MergeSortTask(arrToSort, start, midPoint);
                MergeSortTask upperHalfTask = new MergeSortTask(arrToSort, midPoint, end);

                invokeAll(lowerHalfTask, upperHalfTask);
                merge(arrToSort, start, midPoint, end);
            }
        }
    }

    public void execute(T[] arrToSort, int start, int end) {
        MergeSortTask task = new MergeSortTask(arrToSort, start, end);
        ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
        pool.execute(task);
        task.join();
    }

    public T[] execute(T[] arrToSort) {
        execute(arrToSort, 0, arrToSort.length);
        return arrToSort;
    }

    @Override
    public int getMinSize() {
        return MIN_ARRAY_SIZE;
    }

    @Override
    public int getParallelism() {
        return PARALLELISM;
    }

    @Override
    public int getDivisionFactor() {
        return DIVISION_FACTOR;
    }

    @Override
    public GenericStrategy getBaseCaseStrategy() {
        return BASE_CASE_STRATEGY;
    }

    @Override
    public void setMinSize(int size) {
        if (size >= 1) {
            MIN_ARRAY_SIZE = size;
        }
    }

    @Override
    public void setParallelism(int parallelism) {
        if (parallelism >= 1) {
            PARALLELISM = parallelism;
        }
    }

    @Override
    public boolean isSequential() {
        return false;
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("MergeSort ForkJoin ");
        if (minSize) {
            sb.append("| Minimum Array Size = " + MIN_ARRAY_SIZE);
        }
        if (parallelism) {
            sb.append("| Parallelism = " + PARALLELISM + " ");
        }
        return sb.toString();
    }
}
