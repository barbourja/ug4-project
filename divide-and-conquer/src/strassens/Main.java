package strassens;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws Utils.IncorrectMatrixDimensions {

        int n = 1024;
        Random rand = new Random();
        int[][] mat1 = new int[n][n];
        int[][] mat2 = new int[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                mat1[row][col] = rand.nextInt(20);
                mat2[row][col] = rand.nextInt(20);
            }
        }

        Matrix realmat1 = new ConcreteMatrix(mat1);
        Matrix realmat2 = new ConcreteMatrix(mat2);
        Matrix res_strassen = new ConcreteMatrix(new int[n][n]);
        Matrix working = new ConcreteMatrix(new int[n][n]);

        System.out.println("Running direct naive: ");
        long startTime = System.nanoTime();
        Matrix res_direct = new ConcreteMatrix(new int[n][n]);
        realmat1.mult(realmat2, res_direct);
        long timeTaken = System.nanoTime() - startTime;
        System.out.println(timeTaken/1000000 + " ms");

        System.out.println("Running strassen in place: ");
        SequentialInPlace strat = new SequentialInPlace(128);
        startTime = System.nanoTime();
        strat.execute(realmat1, realmat2, working, res_strassen);
        timeTaken = System.nanoTime() - startTime;
        System.out.println(timeTaken/1000000 + " ms");

        System.out.println(res_direct.equals(res_strassen));
//        int n = 5000;
//        Random rand = new Random();
//        int[][] mat1 = new int[n][n];
//        int[][] mat2 = new int[n][n];
//        for (int row = 0; row < n; row++) {
//            for (int col = 0; col < n; col++) {
//                mat1[row][col] = rand.nextInt(100);
//                mat2[row][col] = rand.nextInt(100);
//            }
//        }
//        int minMatrixSize = n/5;
//
//        StrassensStrategy sequential = new Sequential(minMatrixSize/4);
//        StrassensStrategy forkJoin = new ForkJoin(minMatrixSize, 8, sequential);
//        StrassensStrategy threaded = new Threaded(minMatrixSize, 8, sequential);
//
//        System.out.println("Executing naive...");
//        long startTime = System.nanoTime();
//        int[][] naiveResult = Utils.matMult(mat1, mat2);
//        long timeTaken = System.nanoTime() - startTime;
//        System.out.println((timeTaken/1000000) + " ms");
//
//        System.out.println("Executing sequential...");
//        startTime = System.nanoTime();
//        int[][] seqResult = sequential.execute(mat1, mat2);
//        timeTaken = System.nanoTime() - startTime;
//        System.out.println((timeTaken/1000000) + " ms");
//
//        System.out.println("Executing fork/join...");
//        startTime = System.nanoTime();
//        int[][] forkJoinResult = forkJoin.execute(mat1, mat2);
//        timeTaken = System.nanoTime() - startTime;
//        System.out.println((timeTaken/1000000) + " ms");
//
//        System.out.println("Executing threaded...");
//        startTime = System.nanoTime();
//        int[][] threadedResult = threaded.execute(mat1, mat2);
//        timeTaken = System.nanoTime() - startTime;
//        System.out.println((timeTaken/1000000) + " ms");
//
//        if (!Arrays.deepEquals(naiveResult, seqResult) || !Arrays.deepEquals(seqResult, forkJoinResult) || !Arrays.deepEquals(forkJoinResult, threadedResult)) {
//            throw new RuntimeException("INCONGRUENT RESULTS!");
//        }
    }
}
