package strassens;

public class Utils {
    public static class IncorrectMatrixDimensions extends Exception {
        public IncorrectMatrixDimensions(String err) {
            super(err);
        }
    }

    public static int[][] matAdd(int[][] mat1, int[][] mat2) throws IncorrectMatrixDimensions {
        if (mat1.length != mat2.length || mat1[0].length != mat2[0].length) {
            throw new Utils.IncorrectMatrixDimensions("mat1 and mat2 must be square matrices sharing same N!");
        }
        int[][] res = new int[mat1.length][mat1[0].length];
        for (int row = 0; row < res.length; row++) {
            for (int col = 0; col < res[0].length; col++) {
                res[row][col] = mat1[row][col] + mat2[row][col];
            }
        }
        return res;
    }

    public static int[][] matSub(int[][] mat1, int[][] mat2) throws IncorrectMatrixDimensions {
        if (mat1.length != mat2.length || mat1[0].length != mat2[0].length) {
            throw new Utils.IncorrectMatrixDimensions("mat1 and mat2 must be square matrices sharing same N!");
        }
        int[][] res = new int[mat1.length][mat1[0].length];
        for (int row = 0; row < res.length; row++) {
            for (int col = 0; col < res[0].length; col++) {
                res[row][col] = mat1[row][col] - mat2[row][col];
            }
        }
        return res;
    }

    public static int[][] matMult(int[][] mat1, int[][] mat2) throws IncorrectMatrixDimensions {
        if (mat1[0].length != mat2.length) {
            throw new IncorrectMatrixDimensions("Dimension mismatch between mat1 and mat2!");
        }
        int[][] res = new int[mat1.length][mat2[0].length];
        for (int row = 0; row < mat1.length; row++) {
            for (int col = 0; col < mat2[0].length; col++) {
                res[row][col] = 0;
                for (int k = 0; k < mat2.length; k++) {
                    res[row][col] += (mat1[row][k] * mat2[k][col]);
                }
            }
        }
        return res;
    }

    public static int[][][] calculateStrassensPartials(int[][][] mat1Split, int[][][] mat2Split) throws IncorrectMatrixDimensions {
        if (mat1Split.length != 4 || mat2Split.length != 4 || mat1Split[0].length != mat1Split[0][0].length
        || mat2Split[0].length != mat2Split[0][0].length) {
            throw new IncorrectMatrixDimensions("Must have 8 split squares matrices to calculate Strassen's partial products!");
        }
        int split = mat1Split[0].length;
        int[][][] strassensResult = new int[7][split][split];
        strassensResult[0] = matMult(mat1Split[0], matSub(mat2Split[1], mat2Split[3]));
        strassensResult[1] = matMult(matAdd(mat1Split[0], mat1Split[1]), mat2Split[3]);
        strassensResult[2] = matMult(matAdd(mat1Split[2], mat1Split[3]), mat2Split[0]);
        strassensResult[3] = matMult(mat1Split[3], matSub(mat2Split[2], mat2Split[0]));
        strassensResult[4] = matMult(matAdd(mat1Split[0], mat1Split[3]), matAdd(mat2Split[0], mat2Split[3]));
        strassensResult[5] = matMult(matSub(mat1Split[1], mat1Split[3]), matAdd(mat2Split[2], mat2Split[3]));
        strassensResult[6] = matMult(matSub(mat1Split[0], mat1Split[2]), matAdd(mat2Split[0], mat2Split[1]));
        return  strassensResult;
    }



    public static int[][][] splitMat(int[][] mat) throws IncorrectMatrixDimensions {
        if (mat.length % 2 != 0 || mat.length != mat[0].length) {
            throw new IncorrectMatrixDimensions("Matrix must be square and dimension must be even to split!");
        }
        int split = mat.length / 2;
        int[][][] splitMats = new int[4][split][split]; //  0 = a, 1 = b, etc..
        for (int row = 0; row < mat.length; row++) {
            for (int col = 0; col < mat[0].length; col++) {
                if (row < split && col < split) { // belongs to sub mat A
                    splitMats[0][row][col] = mat[row][col];
                }
                else if (row < split && col >= split) { // belongs to sub mat B
                    splitMats[1][row][col - split] = mat[row][col];
                }
                else if (row >= split && col < split) { // belongs to sub mat C
                    splitMats[2][row- split][col] = mat[row][col];
                }
                else { // belongs to sub mat D
                    splitMats[3][row - split][col - split] = mat[row][col];
                }
            }
        }
        return splitMats;
    }

    public static int[][] combineMat(int[][][] splitMats) throws IncorrectMatrixDimensions {
        if (splitMats.length != 4 || splitMats[0].length != splitMats[0][0].length) {
            throw new IncorrectMatrixDimensions("Must be 4 submatrices and they must be square!");
        }
        int split = splitMats[0].length;
        int[][] combinedMat = new int[2 * split][2 * split];
        for (int row = 0; row < combinedMat.length; row++) {
            for (int col = 0; col < combinedMat[0].length; col++) {
                if (row < split && col < split) { // use sub mat A
                    combinedMat[row][col] = splitMats[0][row][col];
                }
                else if (row < split && col >= split) { // use sub mat B
                     combinedMat[row][col] = splitMats[1][row][col - split];
                }
                else if (row >= split && col < split) { // use sub mat C
                     combinedMat[row][col] = splitMats[2][row- split][col];
                }
                else { // use sub mat D
                    combinedMat[row][col] = splitMats[3][row - split][col - split];
                }
            }
        }
        return combinedMat;
    }

    public static int[][] zeroPad(int[][] mat) {
        int[][] zeroPadMat = new int[mat.length + 1][mat[0].length + 1];
        for (int row = 0; row < mat.length; row++) {
            for (int col = 0; col < mat[0].length; col++) {
                zeroPadMat[row][col] = mat[row][col];
            }
        }
        for (int row = 0; row < zeroPadMat.length; row++) {
            zeroPadMat[row][zeroPadMat[0].length - 1] = 0;
        }
        for (int col = 0; col < zeroPadMat[0].length; col++) {
            zeroPadMat[zeroPadMat.length - 1][col] = 0;
        }
        return zeroPadMat;
    }

    public static int[][] dropZeroPad(int[][] zeroPadMat) {
        int[][] originalMat = new int[zeroPadMat.length - 1][zeroPadMat[0].length - 1];
        for (int row = 0; row < originalMat.length; row++) {
            for (int col = 0; col < originalMat[0].length; col++) {
                originalMat[row][col] = zeroPadMat[row][col];
            }
        }
        return originalMat;
    }
}
