package quickselect;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import static quickselect.Utils.random_partition;

public class ForkJoin<T extends Comparable<T>> implements QuickSelectStrategy<T>{
    protected int MIN_ARRAY_SIZE;
    protected int PARALLELISM;
    protected final QuickSelectStrategy<T> BASE_CASE_STRATEGY;

    public ForkJoin(int minArraySize, int parallelism, QuickSelectStrategy<T> baseCaseStrategy) {
        this.MIN_ARRAY_SIZE = minArraySize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private class QuickSelectTask extends RecursiveTask<T> {
        private final T[] searchArr;
        private final int start;
        private final int end;
        private int k;

        public QuickSelectTask(T[] searchArr, int start, int end, int k) {
            if (k < 0 || k > (end - start)) {
                throw new RuntimeException("Invalid value of k!");
            }
            this.searchArr = searchArr;
            this.start = start;
            this.end = end;
            this.k = k;
        }

        private T computeDirectly() {
            return BASE_CASE_STRATEGY.execute(searchArr, start, end, k);
        }

        @Override
        protected T compute() {
            if (end - start + 1 <= MIN_ARRAY_SIZE) {
                return computeDirectly();
            }
            else {
                int pivotIndex = random_partition(searchArr, start, end);
                int adjustedPivotIndex = pivotIndex - start;

                if (adjustedPivotIndex == k) {
                    return searchArr[pivotIndex];
                }
                else if (k < adjustedPivotIndex) {
                    QuickSelectTask task = new QuickSelectTask(searchArr, start, pivotIndex - 1, k);
                    task.fork();
                    return task.join();
                }
                else {
                    QuickSelectTask task = new QuickSelectTask(searchArr, pivotIndex + 1, end, k - (adjustedPivotIndex + 1));
                    task.fork();
                    return task.join();
                }

            }
        }
    }

    @Override
    public T execute(T[] searchArr, int k) {
        return execute(searchArr, 0, searchArr.length - 1, k);
    }

    @Override
    public T execute(T[] searchArr, int start, int end, int k) {
        QuickSelectTask task = new QuickSelectTask(searchArr, start, end, k);
        ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
        pool.execute(task);
        return task.join();
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
    public void setMinSize(int size) {
        if (size >= 1) {
            this.MIN_ARRAY_SIZE = size;
        }
    }

    @Override
    public void setParallelism(int parallelism) {
        if (parallelism >= 1) {
            this.PARALLELISM = parallelism;
        }
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("QuickSelect ForkJoin ");
        if (minSize) {
            sb.append("| Minimum Array Size = " + MIN_ARRAY_SIZE + " ");
        }
        if (parallelism) {
            sb.append("| Parallelism = " + PARALLELISM + " ");
        }
        return sb.toString();
    }
}
