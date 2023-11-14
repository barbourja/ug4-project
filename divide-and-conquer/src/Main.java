import mergesort.ForkJoin;
import mergesort.MergeSortStrategy;
import mergesort.Sequential;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random rand = new Random();
        ArrayList<Integer> listOfInt = new ArrayList<>();
        for (int i = 0; i < 200000; i++) {
            listOfInt.add(rand.nextInt(100000));
        }

        MergeSortStrategy<Integer> strategy = new Sequential<>(25000);
        long startTime = System.nanoTime();
        List<Integer> sortedList = strategy.execute(listOfInt);
        long timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken / 1000000) + " ms");


        strategy = new ForkJoin<>(25000, 8, new Sequential<Integer>(25000));
        startTime = System.nanoTime();
        sortedList = strategy.execute(listOfInt);
        timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken / 1000000) + " ms");
    }
}