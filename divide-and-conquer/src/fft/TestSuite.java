package fft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class TestSuite {

    public static Long[] testVaryingParallelism(FFTStrategy strategyUnderTest, Integer numInputs, Integer minSize, boolean prettyPrinting) { // return array of average run times
        ArrayList<Long> averageRuntimes = new ArrayList<>();
        System.out.println(strategyUnderTest.toString(true, false) + " - Varying parallelism");

        int[] valuesToTest = new int[]{1, 2, 4, 8, 16, 32};

        if (strategyUnderTest instanceof Sequential) {
            valuesToTest = new int[]{1};
        }

        for (int parallelism : valuesToTest) {
            long runtime = testVaryingInputs(strategyUnderTest, numInputs, minSize, parallelism, prettyPrinting);
            if (prettyPrinting) {
                System.out.print("  |Average runtime: " + runtime + "ms|\n\n");
            }
            averageRuntimes.add(runtime);
        }

        if (!prettyPrinting) { // dump csv
            for (int i = 0; i < valuesToTest.length - 1; i++) {
                System.out.print(valuesToTest[i] + ",");
            }
            System.out.print(valuesToTest[valuesToTest.length - 1] + "\n");
            for (int i = 0; i < averageRuntimes.size() - 1; i++) {
                System.out.print(averageRuntimes.get(i) + ",");
            }
            System.out.print(averageRuntimes.get(averageRuntimes.size() - 1) + "\n");
        }

        return averageRuntimes.toArray(new Long[0]);
    }

    public static Long[] testVaryingMinSize(FFTStrategy strategyUnderTest, Integer numInputs, Integer parallelism, boolean prettyPrinting) { // return array of average run times
        parallelism = strategyUnderTest instanceof Sequential ? 1 : parallelism;

        ArrayList<Long> averageRuntimes = new ArrayList<>();
        System.out.println(strategyUnderTest.toString(false, true) + " - Varying minimum input size");

        int[] valuesToTest = new int[]{100, 1000, 10000, 100000};

        for (int minSize : valuesToTest) {
            long runtime = testVaryingInputs(strategyUnderTest, numInputs, minSize, parallelism, prettyPrinting);
            if (prettyPrinting) {
                System.out.printf("\n  |Average runtime: " + runtime + "ms|\n\n");
            }
            averageRuntimes.add(runtime);
        }

        if (!prettyPrinting) { // dump csv
            for (int i = 0; i < valuesToTest.length - 1; i++) {
                System.out.print(valuesToTest[i] + ",");
            }
            System.out.print(valuesToTest[valuesToTest.length - 1] + "\n");
            for (int i = 0; i < averageRuntimes.size() - 1; i++) {
                System.out.print(averageRuntimes.get(i) + ",");
            }
            System.out.print(averageRuntimes.get(averageRuntimes.size() - 1) + "\n");
        }

        return averageRuntimes.toArray(new Long[0]);
    }

    private static long testVaryingInputs(FFTStrategy strategyUnderTest, Integer numInputs, Integer minSize, Integer parallelism, boolean prettyPrinting) { // return average run time across inputs
        Random rand = new Random();
        ArrayList<Complex[]> inputs = new ArrayList<>();
        for (int i = 0; i < numInputs; i++) {
            int n = (int) Math.pow(2, i + 9);
            Complex[] input = new Complex[n];
            for (int j = 0; j < n; j++) {
                input[j] = new Complex(rand.nextDouble(), rand.nextDouble());
            }
            inputs.add(input);
        }

        return Math.round(runTestOnInputs(strategyUnderTest, inputs, minSize, parallelism, prettyPrinting));
    }


    private static double runTestOnInputs(FFTStrategy strategyUnderTest, Collection<Complex[]> inputs, Integer minSize, Integer parallelism, boolean prettyPrinting) { // return average run time across inputs
        ArrayList<Long> runtimes = new ArrayList<>();
        for (Complex[] input : inputs) {
            long runtime = runSimpleTest(strategyUnderTest, input, minSize, parallelism);
            runtimes.add(runtime);
            if (prettyPrinting) {
                printResultAsString(input.length, minSize, parallelism, runtime);
            }
        }

        return runtimes.stream().mapToLong(Long::valueOf).average().getAsDouble();
    }

    private static long runSimpleTest(FFTStrategy strategyUnderTest, Complex[] input, Integer minSize, Integer parallelism) { // return run time
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

    private static void printResultAsString(int inputLength, int minSize, int parallelism, long elapsedTime) {
        System.out.print("  Input Size: " + inputLength);
        System.out.print(" | Minimum Input Size: " + minSize);
        System.out.print(" | Parallelism: " + parallelism);
        System.out.println(" | Run time: " + elapsedTime + "ms");
    }
}
