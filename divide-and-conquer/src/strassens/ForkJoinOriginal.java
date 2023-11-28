package strassens;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static strassens.Utils.*;
import static strassens.Utils.matAdd;

public class ForkJoinOriginal {

    protected final int MIN_MATRIX_SIZE;
    protected final int PARALLELISM;
    protected final SequentialOriginal BASE_CASE_STRATEGY;

    public ForkJoinOriginal(int minMatrixSize, int parallelism, SequentialOriginal baseCaseStrategy) {
        this.MIN_MATRIX_SIZE = minMatrixSize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private class StrassensTask extends RecursiveTask {

        private int[][] mat1;
        private int[][] mat2;

        public StrassensTask(int[][] mat1, int[][] mat2) {
            this.mat1 = mat1;
            this.mat2 = mat2;
        }

        private int[][][] calculateStrassensPartials(int[][][] mat1Split, int[][][] mat2Split) throws Utils.IncorrectMatrixDimensions {
            if (mat1Split.length != 4 || mat2Split.length != 4 || mat1Split[0].length != mat1Split[0][0].length
                    || mat2Split[0].length != mat2Split[0][0].length || mat1Split[0].length != mat2Split[0].length) {
                throw new Utils.IncorrectMatrixDimensions("Must have 8 split square matrices (sharing same dim) to calculate Strassen's partial products!");
            }
            int split = mat1Split[0].length;
            int[][][] strassensResult = new int[7][split][split];

            StrassensTask[] tasks = new StrassensTask[7];
            tasks[0] = new StrassensTask(mat1Split[0], matSub(mat2Split[1], mat2Split[3]));
            tasks[1] = new StrassensTask(matAdd(mat1Split[0], mat1Split[1]), mat2Split[3]);
            tasks[2] = new StrassensTask(matAdd(mat1Split[2], mat1Split[3]), mat2Split[0]);
            tasks[3] = new StrassensTask(mat1Split[3], matSub(mat2Split[2], mat2Split[0]));
            tasks[4] = new StrassensTask(matAdd(mat1Split[0], mat1Split[3]), matAdd(mat2Split[0], mat2Split[3]));
            tasks[5] = new StrassensTask(matSub(mat1Split[1], mat1Split[3]), matAdd(mat2Split[2], mat2Split[3]));
            tasks[6] = new StrassensTask(matSub(mat1Split[0], mat1Split[2]), matAdd(mat2Split[0], mat2Split[1]));
            invokeAll(tasks);

            for (int i = 0; i < tasks.length; i++) {
                strassensResult[i] = (int[][]) tasks[i].getRawResult();
            }
            return  strassensResult;
        }

        private int[][] computeDirectly() {
            try {
                return BASE_CASE_STRATEGY.execute(mat1, mat2);
            } catch (Utils.IncorrectMatrixDimensions e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected int[][] compute() {
            if (mat1.length != mat2.length || mat1[0].length != mat2[0].length || mat1.length == 0) {
                throw new RuntimeException(new Utils.IncorrectMatrixDimensions("mat1 and mat2 must be square matrices sharing same N! (>0)"));
            }

            if (mat1.length <= MIN_MATRIX_SIZE) { // base case
                return computeDirectly();
            }
            else { // strassen's method
                boolean zeroPadded = false;
                if (mat1.length % 2 != 0){ // odd dimension
                    zeroPadded = true;
                    mat1 = zeroPad(mat1);
                    mat2 = zeroPad(mat2);
                }

                try {
                    int[][][] splitMat1 = splitMat(mat1);
                    int[][][] splitMat2 = splitMat(mat2);
                    int[][][] partialProducts = calculateStrassensPartials(splitMat1, splitMat2);

                    return !zeroPadded ? combineStrassensPartials(partialProducts) : dropZeroPad(combineStrassensPartials(partialProducts));
                } catch (Utils.IncorrectMatrixDimensions e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public int[][] execute(int[][] mat1, int[][] mat2) throws Utils.IncorrectMatrixDimensions {
        StrassensTask task = new StrassensTask(mat1, mat2);
        ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
        pool.execute(task);
        return (int[][]) task.join();
    }
}
