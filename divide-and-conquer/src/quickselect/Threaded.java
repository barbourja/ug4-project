package quickselect;

import static quickselect.Utils.random_partition;

public class Threaded<T extends Comparable<T>> implements QuickSelectStrategy<T>{

    protected final int MIN_ARRAY_SIZE;
    protected final int PARALLELISM;
    protected final QuickSelectStrategy<T> BASE_CASE_STRATEGY;
    protected final int DIVISION_FACTOR = 1;
    protected int threadCount;

    public Threaded(int minArraySize, int parallelism, QuickSelectStrategy<T> baseCaseStrategy) {
        this.MIN_ARRAY_SIZE = minArraySize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private synchronized void updateThreadCount(int val) {
        threadCount += val;
        System.out.println(threadCount); //TODO: remove debug output
    }

    private synchronized boolean requestThreads() {
        if (threadCount + DIVISION_FACTOR <= PARALLELISM) {
            updateThreadCount(DIVISION_FACTOR);
            return true;
        }
        return false;
    }

    private class QuickSelectTask implements Runnable {
        private final T[] searchArr;
        private final int start;
        private final int end;
        private int k;
        private T res = null;

        public QuickSelectTask(T[] searchArr, int start, int end, int k) {
            if (k < 0 || k > end - start) {
                throw new RuntimeException("Invalid value of k!");
            }
            this.searchArr = searchArr;
            this.start = start;
            this.end = end;
            this.k = k;
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
            if (baseCondition()) {
                computeDirectly();
            }
            else {
                k = start + k;
                int pivotIndex = random_partition(searchArr, start, end);
                if (pivotIndex == k) {
                    res = searchArr[k];
                }
                else {
                    boolean threaded = requestThreads();

                    if (threaded) {
                        QuickSelectTask task;
                        if (k < pivotIndex) {
                            task = new QuickSelectTask(searchArr, start, pivotIndex - 1, k - start);
                        }
                        else {
                            task = new QuickSelectTask(searchArr, pivotIndex + 1, end, k - (start + pivotIndex + 1));
                        }
                        Thread thread = new Thread(task);
                        thread.start();
                        try {
                            thread.join();
                            updateThreadCount(-DIVISION_FACTOR);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        res = task.getResult();
                    }
                    else {
                        computeDirectly();
                    }
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
        Thread thread = new Thread(task);
        threadCount = 1;
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return task.getResult();
    }
}
