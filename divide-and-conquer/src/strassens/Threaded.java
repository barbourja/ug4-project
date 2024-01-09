package strassens;

import static java.lang.Math.floor;
import static java.lang.Math.log;

public class Threaded implements StrassensStrategy{

    protected final int MIN_MATRIX_SIZE;
    protected final int PARALLELISM;
    protected final StrassensStrategy BASE_CASE_STRATEGY;

    protected final int DIVISION_FACTOR = 7;
    protected final int MAX_LEVEL;
    protected int threadCount;

    public Threaded(int minMatrixSize, int parallelism, StrassensStrategy baseCaseStrategy) {
        this.MIN_MATRIX_SIZE = minMatrixSize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
        this.MAX_LEVEL = (int) floor(log((DIVISION_FACTOR - 1) * PARALLELISM)/log(DIVISION_FACTOR));
    }

    private synchronized void updateNumThreads(int numThreadChange) {
        threadCount += numThreadChange;
        System.out.println(threadCount); //TODO: remove debug
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

            boolean threaded = requestThreads(CURR_LEVEL);

            if (baseCondition() || !threaded) { // base case
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

                Thread[] threads = new Thread[7]; // create a thread for each task and run them
                for (int i = 0; i < tasks.length; i++) {
                    threads[i] = new Thread(tasks[i]);
                    threads[i].start();
                }

                for (Thread runningThread : threads) { // wait for threads to complete
                    try {
                        runningThread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                updateNumThreads(-DIVISION_FACTOR);

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
        Thread startThread = new Thread(startTask);
        threadCount = 1;
        startThread.start();
        try {
            startThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return startTask.getResult();
    }
}
