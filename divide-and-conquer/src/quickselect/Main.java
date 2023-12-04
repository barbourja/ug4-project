package quickselect;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        int n = 100000000;
        int k = 1;
        Integer[] arr1 = new Integer[n];
        Integer[] arr2 = new Integer[n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            Integer val = rand.nextInt(n * 2);
            arr1[i] = val;
            arr2[i] = val;
        }
        long startTime, timeTaken;

        QuickSelectStrategy<Integer> sequential = new Sequential<>();
        QuickSelectStrategy<Integer> forkJoin = new ForkJoin<>(n/32, 16, sequential);

        System.out.println("Running sequential...");
        startTime = System.nanoTime();
        sequential.execute(arr1, k);
        timeTaken = System.nanoTime() - startTime;
        System.out.println("Sequential: " + (timeTaken/1000000) + " ms");

        System.out.println("Running fork/join...");
        startTime = System.nanoTime();
        forkJoin.execute(arr2, k);
        timeTaken = System.nanoTime() - startTime;
        System.out.println("Sequential: " + (timeTaken/1000000) + " ms");




    }
}
