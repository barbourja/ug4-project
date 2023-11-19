package strassens;

import java.util.Arrays;
import java.util.Random;

import static strassens.Utils.*;

public class Main {
    public static void main(String[] args) throws Utils.IncorrectMatrixDimensions {
        int n = 0;
        Random rand = new Random();
        int[][] mat1 = new int[n][n];
        int[][] mat2 = new int[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                mat1[row][col] = rand.nextInt(1000);
                mat2[row][col] = rand.nextInt(1000);
            }
        }

        StrassensStrategy strategy = new Sequential(2);
        System.out.println(Arrays.deepToString(matAdd(mat1, mat2)));
        System.out.println(Arrays.deepToString(matMult(mat1, mat2)));
        System.out.println(Arrays.deepToString(strategy.execute(mat1, mat2)));

        int[][] splitMat = new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}};
        System.out.println(Arrays.deepToString(splitMat(splitMat)));
        System.out.println(Arrays.deepToString(combineMat(splitMat(splitMat))));
    }
}
