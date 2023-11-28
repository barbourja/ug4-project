package strassens;

import static strassens.Utils.*;

public class SequentialOriginal {

    protected final int MIN_MATRIX_SIZE;

    public SequentialOriginal(int minMatrixSize) {
        this.MIN_MATRIX_SIZE = minMatrixSize;
    }

    private int[][][] calculateStrassensPartials(int[][][] mat1Split, int[][][] mat2Split) throws IncorrectMatrixDimensions {
        if (mat1Split.length != 4 || mat2Split.length != 4 || mat1Split[0].length != mat1Split[0][0].length
                || mat2Split[0].length != mat2Split[0][0].length || mat1Split[0].length != mat2Split[0].length) {
            throw new IncorrectMatrixDimensions("Must have 8 split square matrices (sharing same dim) to calculate Strassen's partial products!");
        }
        int split = mat1Split[0].length;
        int[][][] strassensResult = new int[7][split][split];
        strassensResult[0] = execute(mat1Split[0], matSub(mat2Split[1], mat2Split[3]));
        strassensResult[1] = execute(matAdd(mat1Split[0], mat1Split[1]), mat2Split[3]);
        strassensResult[2] = execute(matAdd(mat1Split[2], mat1Split[3]), mat2Split[0]);
        strassensResult[3] = execute(mat1Split[3], matSub(mat2Split[2], mat2Split[0]));
        strassensResult[4] = execute(matAdd(mat1Split[0], mat1Split[3]), matAdd(mat2Split[0], mat2Split[3]));
        strassensResult[5] = execute(matSub(mat1Split[1], mat1Split[3]), matAdd(mat2Split[2], mat2Split[3]));
        strassensResult[6] = execute(matSub(mat1Split[0], mat1Split[2]), matAdd(mat2Split[0], mat2Split[1]));
        return  strassensResult;
    }

    public int[][] execute(int[][] mat1, int[][] mat2) throws Utils.IncorrectMatrixDimensions{
        boolean zeroPadded = false;
        if (mat1.length != mat2.length || mat1[0].length != mat2[0].length || mat1.length == 0) {
            throw new Utils.IncorrectMatrixDimensions("mat1 and mat2 must be square matrices sharing same N! (>0)");
        }
        else if (mat1.length % 2 != 0){ // odd dimension
            zeroPadded = true;
            mat1 = zeroPad(mat1);
            mat2 = zeroPad(mat2);
        }

        int dimension = mat1.length; // guaranteed to be even by zero pad
        if (dimension <= MIN_MATRIX_SIZE) { // base case
            return !zeroPadded ? matMult(mat1, mat2) : dropZeroPad(matMult(mat1, mat2));
        }
        else { // perform strassen algorithm
            int[][][] mat1Split = splitMat(mat1);
            int[][][] mat2Split = splitMat(mat2);

            int[][][] partialProducts = calculateStrassensPartials(mat1Split, mat2Split);
            return !zeroPadded ? combineStrassensPartials(partialProducts) : dropZeroPad(combineStrassensPartials(partialProducts));
        }
    }
}
