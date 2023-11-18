package strassens;

import static strassens.Utils.*;

public class Sequential implements StrassensStrategy{

    protected final int MIN_MATRIX_SIZE;

    public Sequential(int minMatrixSize) {
        this.MIN_MATRIX_SIZE = minMatrixSize;
    }

    public int[][] execute(int[][] mat1, int[][] mat2) throws Utils.IncorrectMatrixDimensions{
        boolean zeroPadded = false;
        if (mat1.length != mat2.length || mat1[0].length != mat2[0].length) {
            throw new Utils.IncorrectMatrixDimensions("mat1 and mat2 must be square matrices sharing same N!");
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
        else { // perform strassen algorithm //TODO
            int[][][] mat1Split = splitMat(mat1);
            int[][][] mat2Split = splitMat(mat2);

            int[][][] partialProducts = calculateStrassensPartials(mat1Split, mat2Split);
            return null;
        }
    }
}
