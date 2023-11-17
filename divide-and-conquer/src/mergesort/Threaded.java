package mergesort;

import static mergesort.Utils.merge;

public class Threaded<T extends Comparable<T>> implements MergeSortStrategy<T>{

    protected final int MIN_ARRAY_SIZE;
    protected final int PARALLELISM;
    protected final MergeSortStrategy<T> BASE_CASE_STRATEGY;

    public Threaded(int minArraySize, int parallelism, MergeSortStrategy<T> baseCaseStrategy) {
        this.MIN_ARRAY_SIZE = minArraySize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private class MergeSortTask implements Runnable {

        private final T[] arrToSort;
        private final int start;
        private final int end;

        public MergeSortTask(T[] arrToSort, int start, int end) {
            this.arrToSort = arrToSort;
            this.start = start;
            this.end = end;
        }

        private void computeDirectly() {
            BASE_CASE_STRATEGY.execute(arrToSort, start, end);
        }

        @Override
        public void run() {
            if (end - start <= MIN_ARRAY_SIZE) {
                computeDirectly();
            }
            else {
                int midPoint = (start + end) / 2;
                MergeSortTask lowerHalfTask = new MergeSortTask(arrToSort, start, midPoint);
                MergeSortTask upperHalfTask = new MergeSortTask(arrToSort, midPoint, end);

                Thread lowerThread = new Thread(lowerHalfTask);
                Thread upperThread = new Thread(upperHalfTask);

                lowerThread.start();
                upperThread.start();
                try {
                    lowerThread.join();
                    upperThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                merge(arrToSort, start, midPoint, end);
            }
        }
    }

    public void execute(T[] arrToSort, int start, int end) {
        Thread startThread = new Thread(new MergeSortTask(arrToSort, start, end));
        startThread.start();
        try {
            startThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public T[] execute(T[] arrToSort) {
        execute(arrToSort, 0, arrToSort.length);
        return arrToSort;
    }

}
