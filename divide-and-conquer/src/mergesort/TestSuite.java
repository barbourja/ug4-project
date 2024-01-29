package mergesort;

import java.util.ArrayList;
import java.util.Random;

public class TestSuite {

    private static final int NUM_RUNS_PER_INPUT = 5;

    public static Long[] testVaryingParallelism(MergeSortStrategy<Integer> strategyUnderTest, Integer inputSize, Integer minSize, boolean prettyPrinting) { // return array of average run times
        ArrayList<Long> runtimes = new ArrayList<>();
        System.out.println(strategyUnderTest.toString(true, false) + " | Input Size = " + inputSize + " - Varying parallelism");

        int[] valuesToTest = new int[]{1, 2, 4, 8, 16, 32, 64};

        if (strategyUnderTest instanceof Sequential) {
            valuesToTest = new int[]{1};
        }

        for (int parallelism : valuesToTest) {
            long runtime = testInput(strategyUnderTest, inputSize, minSize, parallelism);
            if (prettyPrinting) {
                System.out.print("Parallelism = " + parallelism + "  | Average runtime (across " + NUM_RUNS_PER_INPUT + " runs): " + runtime + "ms |\n\n");
            }
            runtimes.add(runtime);
        }

        if (!prettyPrinting) { // dump csv
            for (int i = 0; i < valuesToTest.length - 1; i++) {
                System.out.print(valuesToTest[i] + ",");
            }
            System.out.print(valuesToTest[valuesToTest.length - 1] + "\n");
            for (int i = 0; i < runtimes.size() - 1; i++) {
                System.out.print(runtimes.get(i) + ",");
            }
            System.out.print(runtimes.get(runtimes.size() - 1) + "\n");
        }

        return runtimes.toArray(new Long[0]);
    }

    public static Long[] testVaryingMinSize(MergeSortStrategy<Integer> strategyUnderTest, Integer inputSize, Integer parallelism, boolean prettyPrinting) { // return array of average run times
        parallelism = strategyUnderTest instanceof Sequential ? 1 : parallelism;

        ArrayList<Long> runtimes = new ArrayList<>();
        System.out.println(strategyUnderTest.toString(false, true) + " | Input Size = " + inputSize + " - Varying minimum input size");

        int[] valuesToTest = new int[]{1, 10, 100, 1000, 10000};

        for (int minSize : valuesToTest) {
            long runtime = testInput(strategyUnderTest, inputSize, minSize, parallelism);
            if (prettyPrinting) {
                System.out.print("Minimum input size = " + minSize + "  | Average runtime (across " + NUM_RUNS_PER_INPUT + " runs): " + runtime + "ms |\n\n");
            }
            runtimes.add(runtime);
        }

        if (!prettyPrinting) { // dump csv
            for (int i = 0; i < valuesToTest.length - 1; i++) {
                System.out.print(valuesToTest[i] + ",");
            }
            System.out.print(valuesToTest[valuesToTest.length - 1] + "\n");
            for (int i = 0; i < runtimes.size() - 1; i++) {
                System.out.print(runtimes.get(i) + ",");
            }
            System.out.print(runtimes.get(runtimes.size() - 1) + "\n");
        }

        return runtimes.toArray(new Long[0]);
    }

    private static long testInput(MergeSortStrategy<Integer> strategyUnderTest, Integer inputSize, Integer minSize, Integer parallelism) { // return average run time across runs of input
        ArrayList<Long> runtimes = new ArrayList<>();
        for (int i = 0; i < NUM_RUNS_PER_INPUT; i++) {
            Random rand = new Random();
            Integer[] input = new Integer[inputSize];
            for (int j = 0; j < inputSize; j++) {
                input[j] = rand.nextInt();
            }
            runtimes.add(runSimpleTest(strategyUnderTest, input, minSize, parallelism));
        }
        return Math.round(runtimes.stream().mapToDouble(Double::valueOf).average().getAsDouble());
    }

    private static long runSimpleTest(MergeSortStrategy<Integer> strategyUnderTest, Integer[] input, Integer minSize, Integer parallelism) { // return run time
        if (minSize != null) {
            strategyUnderTest.setMinSize(minSize);
        }
        if (parallelism != null) {
            strategyUnderTest.setParallelism(parallelism);
        }

        long startTime, elapsedTime;
        startTime = System.nanoTime(); // ns
        strategyUnderTest.execute(input);
        elapsedTime = (System.nanoTime() - startTime) / 1000000; // ms

        return elapsedTime;
    }
}
