package quickselect;

import static quickselect.Utils.random_partition;

public class Threaded<T extends Comparable<T>> implements QuickSelectStrategy<T>{

    protected final int MIN_ARRAY_SIZE;
    protected final int PARALLELISM;
    protected final QuickSelectStrategy<T> BASE_CASE_STRATEGY;
    protected final int DIVISION_FACTOR = 1;
    protected final int MAX_LEVEL;
    protected int threadCount;

    public Threaded(int minArraySize, int parallelism, QuickSelectStrategy<T> baseCaseStrategy) {
        this.MIN_ARRAY_SIZE = minArraySize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
        this.MAX_LEVEL = PARALLELISM - 1; // special case for max level (due to division factor of 1 => no parallelism)
    }

    private synchronized void updateNumThreads(int numThreadChange) {
        threadCount += numThreadChange;
        System.out.println("Thread Count: " + threadCount); //TODO: remove debug
    }

    private synchronized boolean requestThreads(int level) {
        if ((level < MAX_LEVEL) && (threadCount + DIVISION_FACTOR <= PARALLELISM)) {
            updateNumThreads(DIVISION_FACTOR);
            return true;
        }
        return false;
    }

    private class QuickSelectTask implements Runnable {
        private final T[] searchArr;
        private final int start;
        private final int end;
        private int k;
        private final int CURR_LEVEL;
        private T res = null;

        public QuickSelectTask(T[] searchArr, int start, int end, int k, int currLevel) {
            if (k < 0 || k > end - start) {
                throw new RuntimeException("Invalid value of k!");
            }
            this.searchArr = searchArr;
            this.start = start;
            this.end = end;
            this.k = k;
            this.CURR_LEVEL = currLevel;
        }

        public T getResult() {
            return res;
        }

        private boolean baseCondition() {
            return end - start + 1 <= MIN_ARRAY_SIZE;
        }

        private void computeDirectly() {
            res = BASE_CASE_STRATEGY.execute(searchArr, start, end, k);
        }

        @Override
        public void run() {
            boolean threaded = requestThreads(CURR_LEVEL);

            if (baseCondition() || !threaded) {
                computeDirectly();
            }
            else {
                k = start + k;
                int pivotIndex = random_partition(searchArr, start, end);
                if (pivotIndex == k) {
                    res = searchArr[k];
                }
                else {
                    QuickSelectTask task;
                    if (k < pivotIndex) {
                        task = new QuickSelectTask(searchArr, start, pivotIndex - 1, k - start, CURR_LEVEL + 1);
                    }
                    else {
                        task = new QuickSelectTask(searchArr, pivotIndex + 1, end, k - (start + pivotIndex + 1), CURR_LEVEL + 1);
                    }
                    Thread thread = new Thread(task);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateNumThreads(-DIVISION_FACTOR);
                    res = task.getResult();
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
        QuickSelectTask startTask = new QuickSelectTask(searchArr, start, end, k, 0);
        Thread thread = new Thread(startTask);
        threadCount = 1;
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return startTask.getResult();
    }
}
