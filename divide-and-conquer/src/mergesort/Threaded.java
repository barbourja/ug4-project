package mergesort;

import static java.lang.Math.*;
import static mergesort.Utils.merge;

public class Threaded<T extends Comparable<T>> implements MergeSortStrategy<T>{

    protected final int MIN_ARRAY_SIZE;
    protected final int PARALLELISM;
    protected final MergeSortStrategy<T> BASE_CASE_STRATEGY;
    protected final int DIVISION_FACTOR = 2;
    protected final int MAX_LEVEL;
    protected int threadCount;

    public Threaded(int minArraySize, int parallelism, MergeSortStrategy<T> baseCaseStrategy) {
        this.MIN_ARRAY_SIZE = minArraySize > 0 ? minArraySize : 1;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
        this.MAX_LEVEL = (int) floor(log((DIVISION_FACTOR - 1) * PARALLELISM)/log(DIVISION_FACTOR));
    }

    protected synchronized void updateNumThreads(int numThreadChange) {
        threadCount += numThreadChange;
        System.out.println("Thread Count: " + threadCount); //TODO: remove debug
    }

    private synchronized boolean requestThreads(int level) {
        if ((level < MAX_LEVEL) && (threadCount + DIVISION_FACTOR <= PARALLELISM)) {
            updateNumThreads(DIVISION_FACTOR);
            return true;
        }
        else {
            return false;
        }
    }

    private class MergeSortTask implements Runnable {

        private final T[] arrToSort;
        private final int start;
        private final int end;
        private final int CURR_LEVEL;

        public MergeSortTask(T[] arrToSort, int start, int end, int currLevel) {
            this.arrToSort = arrToSort;
            this.start = start;
            this.end = end;
            this.CURR_LEVEL = currLevel;
        }

        private boolean baseCondition() {
            return (end - start <= MIN_ARRAY_SIZE);
        }

        private void computeDirectly() {
            BASE_CASE_STRATEGY.execute(arrToSort, start, end);
        }

        @Override
        public void run() {
            boolean threaded = requestThreads(CURR_LEVEL);

            if (baseCondition() || !threaded) {
                computeDirectly();
            }
            else {
                int midPoint = (start + end) / 2;
                MergeSortTask lowerHalfTask = new MergeSortTask(arrToSort, start, midPoint, CURR_LEVEL + 1);
                MergeSortTask upperHalfTask = new MergeSortTask(arrToSort, midPoint, end, CURR_LEVEL + 1);

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
                updateNumThreads(-DIVISION_FACTOR);
                merge(arrToSort, start, midPoint, end);
            }
        }
    }

    public void execute(T[] arrToSort, int start, int end) {
        MergeSortTask startTask = new MergeSortTask(arrToSort, start, end, 0);
        Thread startThread = new Thread(startTask);
        threadCount = 1;
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
