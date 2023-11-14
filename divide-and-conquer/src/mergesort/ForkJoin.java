package mergesort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static mergesort.Utils.merge;

public class ForkJoin<T extends Comparable<T>> implements MergeSortStrategy<T>{

    protected final int MIN_ARRAY_SIZE;
    protected final int PARALLELISM;
    protected final MergeSortStrategy<T> BASE_CASE_STRATEGY;

    public ForkJoin(int minArraySize, int parallelism, MergeSortStrategy<T> baseCaseStrategy) {
        this.MIN_ARRAY_SIZE = minArraySize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private class MergeSortTask extends RecursiveTask {

        private final List<T> arrToSort;

        public MergeSortTask(List<T> arrToSort) {
            this.arrToSort = arrToSort;
        }

        protected List<T> computeDirectly() {
            return BASE_CASE_STRATEGY.execute(arrToSort);
        }

        @Override
        protected List<T> compute() {
            if (arrToSort.size() <= MIN_ARRAY_SIZE) {
                return computeDirectly();
            }
            else {
                int midPoint = arrToSort.size() / 2;
                MergeSortTask lowerHalfTask = new MergeSortTask(new ArrayList<>(arrToSort.subList(0, midPoint)));
                MergeSortTask upperHalfTask = new MergeSortTask(new ArrayList<>(arrToSort.subList(midPoint, arrToSort.size())));

                lowerHalfTask.fork();
                upperHalfTask.fork();

                return merge((List<T>) lowerHalfTask.join(), (List<T>) upperHalfTask.join());
            }
        }
    }

    public List<T> execute(List<T> arrToSort) {
        MergeSortTask mergeTask = new MergeSortTask(arrToSort);
        ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
        pool.execute(mergeTask);
        return (List<T>) mergeTask.join();
    }
}
