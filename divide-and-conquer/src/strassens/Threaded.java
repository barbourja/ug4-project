package strassens;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Math.floor;
import static java.lang.Math.log;

public class Threaded implements StrassensStrategy{

    protected int MIN_MATRIX_SIZE;
    protected int PARALLELISM;
    protected final StrassensStrategy BASE_CASE_STRATEGY;

    protected final int DIVISION_FACTOR = 7;
    protected int MAX_LEVEL;
    protected int threadCount;

    public Threaded(int minMatrixSize, int parallelism, StrassensStrategy baseCaseStrategy) {
        this.MIN_MATRIX_SIZE = minMatrixSize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
        this.MAX_LEVEL = (int) floor(log((DIVISION_FACTOR - 1) * PARALLELISM)/log(DIVISION_FACTOR));
    }

    private synchronized void updateNumThreads(int numThreadChange) {
        threadCount += numThreadChange;
    }

    private int requestThreads(int level) { // returns number of threads allocated
        int allocate;
        int remainingThreads;
        synchronized (this) {
            remainingThreads = PARALLELISM - threadCount;
        }
        if (level < MAX_LEVEL && DIVISION_FACTOR <= remainingThreads) { // allocate all requested threads
            allocate = DIVISION_FACTOR;
        }
        else if (level < MAX_LEVEL && remainingThreads > 0) { // partially fulfil request
            allocate = remainingThreads;
        }
        else {
            return 0;
        }
        updateNumThreads(allocate);
        return allocate;
    }

    private class strassenMultTask implements Runnable {

        private final Matrix mat1;
        private final Matrix mat2;
        private final Matrix res;
        private final int dimension;
        private final int CURR_LEVEL;

        public strassenMultTask(Matrix mat1, Matrix mat2, Matrix res, int currLevel) {
            this.mat1 = mat1;
            this.mat2 = mat2;
            this.res = res;
            this.dimension = mat1.getNumRows();
            this.CURR_LEVEL = currLevel;
        }

        public Matrix getResult() {
            return res;
        }

        private void computeDirectly() {
            BASE_CASE_STRATEGY.execute(mat1, mat2, res);
        }

        private boolean baseCondition() {
            return dimension <= MIN_MATRIX_SIZE;
        }

        @Override
        public void run() {
            if (!mat1.isSquare() || !mat2.isSquare() || !mat1.dimEquals(mat2)) {
                throw new RuntimeException("Square/equal assumption doesn't hold!");
            }

            int numThreads = requestThreads(CURR_LEVEL);

            if (baseCondition() || numThreads == 0) { // base case
                computeDirectly();
            }
            else { // perform strassen algorithm
                // splitting input matrices
                Matrix[] mat1Split = mat1.quadrantSplit();
                Matrix[] mat2Split = mat2.quadrantSplit();
                Matrix[] resQuadrants = res.quadrantSplit();

                Matrix[] workingQuadrants = new Matrix[11]; // create 11 working quadrants to make 15 required to run in parallel (11 working + 4 result)
                for (int i = 0; i < workingQuadrants.length; i++) {
                    workingQuadrants[i] = new ConcreteMatrix(new int[dimension/2][dimension/2]);
                }

                // reserve resQuadrants [0,3] and workingQuadrants[0,2] for partials
                // winograd form for greater memory efficiency
                strassenMultTask[] tasks = new strassenMultTask[7];
                tasks[0] = new strassenMultTask(
                        mat1Split[2].sub(mat1Split[0], workingQuadrants[3]),
                        mat2Split[1].sub(mat2Split[3], workingQuadrants[4]),
                        workingQuadrants[0], CURR_LEVEL + 1); // u
                tasks[1] = new strassenMultTask(
                        mat1Split[2].add(mat1Split[3], workingQuadrants[5]),
                        mat2Split[1].sub(mat2Split[0], workingQuadrants[6]),
                        workingQuadrants[1], CURR_LEVEL + 1); // v
                tasks[2] = new strassenMultTask(mat1Split[0], mat2Split[0], workingQuadrants[2], CURR_LEVEL + 1); // p1
                tasks[3] = new strassenMultTask(
                        workingQuadrants[5].sub(mat1Split[0], workingQuadrants[7]),
                        mat2Split[0].add(mat2Split[3], workingQuadrants[8])
                                .subInPlace(mat2Split[1]),
                        resQuadrants[3], CURR_LEVEL + 1); // p2
                tasks[4] = new strassenMultTask(mat1Split[1], mat2Split[2], resQuadrants[0], CURR_LEVEL + 1); // p3
                tasks[5] = new strassenMultTask(
                        mat1Split[0].add(mat1Split[1], workingQuadrants[9])
                                .subInPlace(mat1Split[2])
                                .subInPlace(mat1Split[3]),
                        mat2Split[3],
                        resQuadrants[1], CURR_LEVEL + 1); // p4
                tasks[6] = new strassenMultTask(
                        mat1Split[3],
                        mat2Split[2].add(workingQuadrants[6], workingQuadrants[10])
                                .subInPlace(mat2Split[3]),
                        resQuadrants[2], CURR_LEVEL + 1); // p5

                Queue<strassenMultTask> taskQueue = new ConcurrentLinkedQueue<>(Arrays.asList(tasks)); // queue the tasks
                ArrayList<Thread> runningThreads = new ArrayList<>();

                while (!taskQueue.isEmpty()) {
                    while (runningThreads.size() < numThreads && !taskQueue.isEmpty()) { // dispatch tasks for execution by available threads
                        Thread thread = new Thread(taskQueue.remove());
                        thread.start();
                        runningThreads.add(thread);
                    }
                    if (!taskQueue.isEmpty()) {
                        taskQueue.remove().run(); // run any remaining task in this thread
                    }
                    Iterator<Thread> iterator = runningThreads.iterator();
                    while (iterator.hasNext()) { // check status of other threads and join if complete
                        Thread threadToCheck = iterator.next();
                        if (threadToCheck.getState() == Thread.State.TERMINATED) {
                            try {
                                threadToCheck.join();
                                iterator.remove();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Iterator<Thread> iterator = runningThreads.iterator();
                while (iterator.hasNext()) { // join all outstanding threads
                    try {
                        iterator.next().join();
                        iterator.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                updateNumThreads(-numThreads);

                // combining partials (winograd)
                Matrix p1 = tasks[2].getResult();
                Matrix p2 = tasks[3].getResult();
                Matrix w = p1.add(p2, p2); // (w = p1 + p2), p2 no longer needed -> store directly in p2(resQuadrant[3])

                Matrix p3 = tasks[4].getResult();
                Matrix res0 = p3.add(p1, p3); // res0 = p3 + p1, p3 no longer needed -> store directly in p3(resQuadrants[0])

                Matrix v = tasks[1].getResult();
                Matrix p4 = tasks[5].getResult();
                Matrix res1 = p4.add(w, p4).add(v, p4); // res1 = p4 + w + v, p4 no longer needed -> store directly in p4(resQuadrants[1])

                Matrix u = tasks[0].getResult();
                Matrix p5 = tasks[6].getResult();
                Matrix res2 = p5.add(w, p5).add(u, p5); // res2 = p5 + w + u, p5 no longer needed -> store directly in p5(resQuadrants[2])

                Matrix res3 = w.add(u, w).add(v, w); // res3 = w + u + v, w, u & v no longer needed -> store directly in w(resQuadrants[3])
            }
        }
    }

    @Override
    public Matrix execute(Matrix mat1, Matrix mat2, Matrix res) {
        strassenMultTask startTask = new strassenMultTask(mat1, mat2, res, 0);
        threadCount = 1;
        startTask.run();
        return startTask.getResult();
    }

    @Override
    public int getMinSize() {
        return MIN_MATRIX_SIZE;
    }

    @Override
    public int getParallelism() {
        return PARALLELISM;
    }

    @Override
    public void setMinSize(int size) {
        if (size >= 1) {
            this.MIN_MATRIX_SIZE = size;
        }
    }

    @Override
    public void setParallelism(int parallelism) {
        if (parallelism >= 1) {
            this.PARALLELISM = parallelism;
            this.MAX_LEVEL = (int) floor(log((DIVISION_FACTOR - 1) * parallelism)/log(DIVISION_FACTOR));
        }
    }

    @Override
    public boolean isSequential() {
        return false;
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("Strassens Threaded ");
        if (minSize) {
            sb.append("| Minimum Matrix Size = " + MIN_MATRIX_SIZE + " ");
        }
        if (parallelism) {
            sb.append("| Parallelism = " + PARALLELISM + " ");
        }
        return sb.toString();
    }
}
