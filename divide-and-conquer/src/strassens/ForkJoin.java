package strassens;

import generic.GenericStrategy;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoin implements StrassensStrategy{

    protected int MIN_MATRIX_SIZE;
    protected int PARALLELISM;
    protected final StrassensStrategy BASE_CASE_STRATEGY;

    public ForkJoin(int minMatrixSize, int parallelism, StrassensStrategy baseCaseStrategy) {
        this.MIN_MATRIX_SIZE = minMatrixSize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private class strassenMultTask extends RecursiveTask {

        private final Matrix mat1;
        private final Matrix mat2;
        private final Matrix res;

        public strassenMultTask(Matrix mat1, Matrix mat2, Matrix res) {
            this.mat1 = mat1;
            this.mat2 = mat2;
            this.res = res;
        }

        private void computeDirectly() {
            BASE_CASE_STRATEGY.execute(mat1, mat2, res);
        }

        @Override
        protected Matrix compute() {
            if (!mat1.isSquare() || !mat2.isSquare() || !mat1.dimEquals(mat2)) {
                throw new RuntimeException("Square/equal assumption doesn't hold!");
            }
            int dimension = mat1.getNumRows();
            if (dimension <= MIN_MATRIX_SIZE) { // base case
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
                        workingQuadrants[0]); // u
                tasks[1] = new strassenMultTask(
                        mat1Split[2].add(mat1Split[3], workingQuadrants[5]),
                        mat2Split[1].sub(mat2Split[0], workingQuadrants[6]),
                        workingQuadrants[1]); // v
                tasks[2] = new strassenMultTask(mat1Split[0], mat2Split[0], workingQuadrants[2]); // p1
                tasks[3] = new strassenMultTask(
                        workingQuadrants[5].sub(mat1Split[0], workingQuadrants[7]),
                        mat2Split[0].add(mat2Split[3], workingQuadrants[8])
                                .subInPlace(mat2Split[1]),
                        resQuadrants[3]); // p2
                tasks[4] = new strassenMultTask(mat1Split[1], mat2Split[2], resQuadrants[0]); // p3
                tasks[5] = new strassenMultTask(
                        mat1Split[0].add(mat1Split[1], workingQuadrants[9])
                                .subInPlace(mat1Split[2])
                                .subInPlace(mat1Split[3]),
                        mat2Split[3],
                        resQuadrants[1]); // p4
                tasks[6] = new strassenMultTask(
                        mat1Split[3],
                        mat2Split[2].add(workingQuadrants[6], workingQuadrants[10])
                                .subInPlace(mat2Split[3]),
                        resQuadrants[2]); // p5

                for (strassenMultTask task : tasks) {
                    task.fork();
                }

                // combining partials (winograd)
                Matrix p1 = (Matrix) tasks[2].join();
                Matrix p2 = (Matrix) tasks[3].join();
                Matrix w = p1.add(p2, p2); // (w = p1 + p2), p2 no longer needed -> store directly in p2(resQuadrant[3])

                Matrix p3 = (Matrix) tasks[4].join();
                Matrix res0 = p3.add(p1, p3); // res0 = p3 + p1, p3 no longer needed -> store directly in p3(resQuadrants[0])

                Matrix v = (Matrix) tasks[1].join();
                Matrix p4 = (Matrix) tasks[5].join();
                Matrix res1 = p4.add(w, p4).add(v, p4); // res1 = p4 + w + v, p4 no longer needed -> store directly in p4(resQuadrants[1])

                Matrix u = (Matrix) tasks[0].join();
                Matrix p5 = (Matrix) tasks[6].join();
                Matrix res2 = p5.add(w, p5).add(u, p5); // res2 = p5 + w + u, p5 no longer needed -> store directly in p5(resQuadrants[2])

                Matrix res3 = w.add(u, w).add(v, w); // res3 = w + u + v, w, u & v no longer needed -> store directly in w(resQuadrants[3])
            }

            return res;
        }
    }

    @Override
    public Matrix execute(Matrix mat1, Matrix mat2, Matrix res) {
        strassenMultTask task = new strassenMultTask(mat1, mat2, res);
        ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
        pool.execute(task);
        return (Matrix) task.join();
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
    public GenericStrategy getBaseCaseStrategy() {
        return BASE_CASE_STRATEGY;
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
        }
    }

    @Override
    public boolean isSequential() {
        return false;
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("Strassens ForkJoin ");
        if (minSize) {
            sb.append("| Minimum Matrix Size = " + MIN_MATRIX_SIZE + " ");
        }
        if (parallelism) {
            sb.append("| Parallelism = " + PARALLELISM + " ");
        }
        return sb.toString();
    }
}
