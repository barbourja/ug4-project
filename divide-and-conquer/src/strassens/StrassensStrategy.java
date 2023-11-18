package strassens;

public interface StrassensStrategy {
    int[][] execute(int[][] mat1, int[][] mat2) throws Utils.IncorrectMatrixDimensions;
}
