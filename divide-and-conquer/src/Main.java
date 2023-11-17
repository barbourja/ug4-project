import mergesort.ForkJoin;
import mergesort.MergeSortStrategy;
import mergesort.Sequential;
import mergesort.Threaded;

import java.util.Arrays;
import java.util.Random;


public class Main {
    public static void main(String[] args) {
        Random rand = new Random();
        int numElems = 1000000;
        Integer[] testArr1 = new Integer[numElems];
        for (int i = 0; i < numElems; i++) {
            testArr1[i] = rand.nextInt(numElems/2);
        }
        Integer[] testArr2 = Arrays.copyOf(testArr1, testArr1.length);
        Integer[] testArr3 = Arrays.copyOf(testArr1, testArr1.length);
        int minArraySize = numElems/5;

        MergeSortStrategy<Integer> sequential = new Sequential<>(minArraySize/4);
        long startTime = System.nanoTime();
        sequential.execute(testArr1);
        long timeTaken = System.nanoTime() - startTime;
        System.out.println("Sequential: " + (timeTaken/1000000) + " ms");

        MergeSortStrategy<Integer> forkJoin = new ForkJoin<>(minArraySize, 8, sequential);
        startTime = System.nanoTime();
        forkJoin.execute(testArr2);
        timeTaken = System.nanoTime() - startTime;
        System.out.println("Fork/Join: " + (timeTaken/1000000) + " ms");

        MergeSortStrategy<Integer> threaded = new Threaded<>(minArraySize, 8, sequential);
        startTime = System.nanoTime();
        threaded.execute(testArr3);
        timeTaken = System.nanoTime() - startTime;
        System.out.println("Threaded: "+ (timeTaken/1000000) + " ms");

        for (int i = 0; i < testArr1.length - 1; i++) {
            if (testArr1[i] > testArr1[i + 1]) {
                System.out.println("testArr1 incorrect!");
                throw new RuntimeException();
            }
            if (testArr2[i] > testArr2[i + 1]) {
                System.out.println("testArr2 incorrect!");
                throw new RuntimeException();
            }
            if (testArr3[i] > testArr2[i + 1]) {
                System.out.println("testArr3 incorrect!");
                throw new RuntimeException();
            }
        }
    }
}