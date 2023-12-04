package quickselect;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        int n = 1000000;
        int k = 1;
        Integer[] arr1 = new Integer[n];
        Integer[] arr2 = new Integer[n];
        Integer[] arr3 = new Integer[n];
        Integer[] arr4 = new Integer[n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            Integer val = rand.nextInt(n);
            arr1[i] = val;
            arr2[i] = val;
            arr3[i] = val;
            arr4[i] = val;
        }
        long startTime, timeTaken;

        QuickSelectStrategy<Integer> sequential = new Sequential<>();
        QuickSelectStrategy<Integer> forkJoin = new ForkJoin<>(n/16, 16, sequential);
        QuickSelectStrategy<Integer> threaded = new Threaded<>(n/16, 1, sequential);

        System.out.println("Running sequential...");
        startTime = System.nanoTime();
        Integer result_seq = sequential.execute(arr1, k);
        timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken/1000000) + " ms");

        System.out.println("Running fork/join...");
        startTime = System.nanoTime();
        Integer result_forkjoin = forkJoin.execute(arr2, k);
        timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken/1000000) + " ms");

        System.out.println("Running threaded...");
        startTime = System.nanoTime();
        Integer result_threaded = threaded.execute(arr3, k);
        timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken/1000000) + " ms");

        Arrays.sort(arr4);
        if (!arr4[k].equals(result_seq) || !result_seq.equals(result_forkjoin) || !result_forkjoin.equals(result_threaded)) {
            throw new RuntimeException("Incorrect results!");
        }
    }
}
