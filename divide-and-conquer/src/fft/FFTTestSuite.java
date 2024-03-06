package fft;

import generic.GenericStrategy;
import generic.GenericTestSuite;

import java.util.ArrayList;
import java.util.Random;

public class FFTTestSuite extends GenericTestSuite {

    private final long SEED = 2013710958;
    private final Random rand = new Random();

    public FFTTestSuite(int numRunsPerInput) {
        super(numRunsPerInput);
    }

    @Override
    public long testInput(GenericStrategy strategyUnderTest, Integer inputSize, Integer minSize, Integer parallelism, boolean fullPrinting) { // return average run time across runs of input
        if (!(strategyUnderTest instanceof FFTStrategy)) {
            throw new RuntimeException("Incorrect strategy type passed! Expected FFT!");
        }

        ArrayList<Long> runtimes = new ArrayList<>();
        rand.setSeed(SEED);
        for (int i = 0; i < NUM_RUNS_PER_INPUT; i++) {
            Complex[] input = new Complex[inputSize];
            for (int j = 0; j < inputSize; j++) {
                input[j] = new Complex(rand.nextDouble(), rand.nextDouble());
            }
            runtimes.add(runSimpleTest((FFTStrategy) strategyUnderTest, minSize, parallelism, input));
        }
        long avgRuntime = Math.round(runtimes.stream().mapToDouble(Double::valueOf).average().getAsDouble());
        if (fullPrinting) {
            System.out.print("  ");
            System.out.print("Min. Size=" + minSize + " | Parallelism=" + parallelism + " | Runtimes: ");
            for (int i = 0; i < runtimes.size() - 1; i++) {
                System.out.print(runtimes.get(i) + ",");
            }
            System.out.print(runtimes.get(runtimes.size() - 1) + " | Avg.: " + avgRuntime + "\n");
        }
        return avgRuntime;
    }

    private long runSimpleTest(FFTStrategy strategyUnderTest, Integer minSize, Integer parallelism, Complex[] input) {
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
