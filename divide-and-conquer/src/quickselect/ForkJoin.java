package quickselect;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import static quickselect.Utils.random_partition;

public class ForkJoin<T extends Comparable<T>> implements QuickSelectStrategy<T>{
    protected final int MIN_ARRAY_SIZE;
    protected final int PARALLELISM;
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
            if (k < 0 || k > end - start) {
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
                k = start + k;
                int pivotIndex = random_partition(searchArr, start, end);
                if (pivotIndex == k) {
                    return searchArr[k];
                }
                else if (k < pivotIndex) {
                    QuickSelectTask task = new QuickSelectTask(searchArr, start, pivotIndex - 1, k - start);
                    task.fork();
                    return task.join();
                }
                else {
                    QuickSelectTask task = new QuickSelectTask(searchArr, pivotIndex + 1, end, k - (start + pivotIndex + 1));
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
}
