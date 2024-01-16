package fft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class TestSuite {

    public static Double[] testVaryingParallelism(FFTStrategy strategyUnderTest, Integer numInputs, Integer minSize) { // return array of average run times
        ArrayList<Double> averageRuntimes = new ArrayList<>();
        System.out.println(strategyUnderTest.toString(false, false));

        int[] valuesToTest = new int[]{2, 4, 8, 16, 32};

        if (strategyUnderTest instanceof Sequential)
        for (int parallelism : valuesToTest) {
            double runtime = testVaryingInputs(strategyUnderTest, numInputs, minSize, parallelism);
            System.out.println("  |Average runtime: " + runtime + "ms|\n");
            averageRuntimes.add(runtime);
        }

        return averageRuntimes.toArray(new Double[0]);
    }

    public static Double[] testVaryingMinSize(FFTStrategy strategyUnderTest, Integer numInputs, Integer parallelism) { // return array of average run times
        ArrayList<Double> averageRuntimes = new ArrayList<>();
        System.out.println(strategyUnderTest.toString(false, false));

        int[] valuesToTest = new int[]{100, 1000, 10000, 100000};

        for (int minSize : valuesToTest) {
            double runtime = testVaryingInputs(strategyUnderTest, numInputs, minSize, parallelism);
            System.out.println("  |Average runtime: " + runtime + "ms|\n");
            averageRuntimes.add(runtime);
        }

        return averageRuntimes.toArray(new Double[0]);
    }

    private static double testVaryingInputs(FFTStrategy strategyUnderTest, Integer numInputs, Integer minSize, Integer parallelism) { // return average run time across inputs
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
        return runTestOnInputs(strategyUnderTest, inputs, minSize, parallelism);
    }


    private static double runTestOnInputs(FFTStrategy strategyUnderTest, Collection<Complex[]> inputs, Integer minSize, Integer parallelism) { // return average run time across inputs
        ArrayList<Long> runtimes = new ArrayList<>();
        for (Complex[] input : inputs) {
            runtimes.add(runSimpleTest(strategyUnderTest, input, minSize, parallelism));
        }
        System.out.print("\n");
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
        System.out.print("  Input Size: " + input.length);
        System.out.print(" | Minimum Input Size: " + strategyUnderTest.getMinSize());
        System.out.print(" | Parallelism: " + strategyUnderTest.getParallelism());
        startTime = System.nanoTime(); // ns
        strategyUnderTest.execute(input);
        elapsedTime = (System.nanoTime() - startTime) / 1000000; // ms
        System.out.println(" | Run time: " + elapsedTime + "ms");

        return elapsedTime;
    }
}
