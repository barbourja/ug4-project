package strassens;

import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws Utils.IncorrectMatrixDimensions {
        int n = 2500;
        Random rand = new Random();
        int[][] mat1 = new int[n][n];
        int[][] mat2 = new int[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                mat1[row][col] = rand.nextInt(100);
                mat2[row][col] = rand.nextInt(100);
            }
        }
        int minMatrixSize = n/5;

        StrassensStrategy sequential = new Sequential(minMatrixSize/4);
        StrassensStrategy forkJoin = new ForkJoin(minMatrixSize, 8, sequential);

        long startTime = System.nanoTime();
        int[][] seqResult = sequential.execute(mat1, mat2);
        long timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken/1000000) + " ms");

        startTime = System.nanoTime();
        int[][] forkJoinResult = forkJoin.execute(mat1, mat2);
        timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken/1000000) + " ms");

//        System.out.print(Arrays.deepToString(seqResult));
//        System.out.print(Arrays.deepToString(forkJoinResult));
        if (!Arrays.deepEquals(seqResult, forkJoinResult)) {
            throw new RuntimeException("INCONGRUENT RESULTS!");
        }
    }
}
