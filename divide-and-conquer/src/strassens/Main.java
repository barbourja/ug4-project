package strassens;

import java.util.Random;

public class Main {
    public static void main(String[] args) {

        int n = 2048;
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
        Matrix res_direct = new ConcreteMatrix(new int[n][n]);
        Matrix res_sequential = new ConcreteMatrix(new int[n][n]);
        Matrix res_forkjoin = new ConcreteMatrix(new int[n][n]);
        Matrix res_threaded = new ConcreteMatrix(new int[n][n]);

        long startTime;
        long timeTaken;

//        System.out.println("Running direct naive...");
//        long startTime = System.nanoTime();
//        res_direct = realmat1.mult(realmat2, res_direct);
//        long timeTaken = System.nanoTime() - startTime;
//        System.out.println(timeTaken/1000000 + " ms");

        System.out.println("Running sequential...");
        StrassensStrategy sequential = new Sequential(32);
        startTime = System.nanoTime();
        sequential.execute(realmat1, realmat2, res_sequential);
        timeTaken = System.nanoTime() - startTime;
        System.out.println(timeTaken/1000000 + " ms");

        System.out.println("Running fork/join...");
        StrassensStrategy forkJoin = new ForkJoin(256, 16, sequential);
        startTime = System.nanoTime();
        forkJoin.execute(realmat1, realmat2, res_forkjoin);
        timeTaken = System.nanoTime() - startTime;
        System.out.println(timeTaken/1000000 + " ms");

        System.out.println("Running threaded...");
        StrassensStrategy threaded = new Threaded(256, 16, sequential);
        startTime = System.nanoTime();
        threaded.execute(realmat1, realmat2, res_threaded);
        timeTaken = System.nanoTime() - startTime;
        System.out.println(timeTaken/1000000 + " ms");

        System.out.println(res_direct.equals(res_sequential));
        System.out.println(res_sequential.equals(res_forkjoin));
        System.out.println(res_forkjoin.equals(res_threaded));
    }
}
