package strassens;

import generic.GenericStrategy;
import generic.GenericTestSuite;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.*;

public class StrassensTestSuite extends GenericTestSuite {

    private final long SEED = 1994847393;
    private final Random rand = new Random();

    private final int DIMENSION_DIVISION_FACTOR = 2;

    public StrassensTestSuite(int numRunsPerInput) {
        super(numRunsPerInput);
    }

    @Override
    public Long[] testVaryingParallelism(GenericStrategy strategyUnderTest, Integer inputSize, Integer[] valuesToTest, boolean fullPrinting) { // adjusted minsize calculation as matrix dimension reduces as power of 2
        System.out.println(strategyUnderTest.toString(false, false) + "| Input Size = " + inputSize + " - Varying parallelism");

        if (strategyUnderTest.isSequential()) {
            valuesToTest = new Integer[]{1};
        }

        ArrayList<Long> runtimes = new ArrayList<>();
        for (int parallelism : valuesToTest) {
            int minSize;
            if (!strategyUnderTest.isSequential()) {
                int maxLevelReached = (int) ceil(log(parallelism) / log(DIMENSION_DIVISION_FACTOR));
                minSize = (int) ceil(inputSize / pow(DIMENSION_DIVISION_FACTOR, maxLevelReached));
            }
            else {
                minSize = strategyUnderTest.getMinSize();
            }
            long runtime = testInput(strategyUnderTest, inputSize, minSize, parallelism, fullPrinting);
            runtimes.add(runtime);
        }

        // dump csv
        System.out.print("  ");
        for (int i = 0; i < valuesToTest.length - 1; i++) {
            System.out.print(valuesToTest[i] + ",");
        }
        System.out.print(valuesToTest[valuesToTest.length - 1] + "\n");

        System.out.print("  ");
        for (int i = 0; i < runtimes.size() - 1; i++) {
            System.out.print(runtimes.get(i) + ",");
        }
        System.out.print(runtimes.get(runtimes.size() - 1) + "\n");

        return runtimes.toArray(new Long[0]);
    }

    @Override
    public long testInput(GenericStrategy strategyUnderTest, Integer inputSize, Integer minSize, Integer parallelism, boolean fullPrinting) {
        if (!(strategyUnderTest instanceof StrassensStrategy)) {
            throw new RuntimeException("Incorrect strategy type passed! Expected Strassens!");
        }
        ArrayList<Long> runtimes = new ArrayList<>();
        rand.setSeed(SEED);
        for (int i = 0; i < NUM_RUNS_PER_INPUT; i++) {
            int[][] input1 = new int[inputSize][inputSize];
            int[][] input2 = new int[inputSize][inputSize];
            for (int row = 0; row < inputSize; row++) {
                for (int col = 0; col < inputSize; col++) {
                    input1[row][col] = rand.nextInt();
                    input2[row][col] = rand.nextInt();
                }
            }
            int k = rand.nextInt(inputSize);
            runtimes.add(runSimpleTest((StrassensStrategy) strategyUnderTest, new ConcreteMatrix(input1), new ConcreteMatrix(input2), new ConcreteMatrix(new int[inputSize][inputSize]), minSize, parallelism));
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

    private static long runSimpleTest(StrassensStrategy strategyUnderTest, Matrix mat1, Matrix mat2, Matrix res, Integer minSize, Integer parallelism) { // return run time
        if (minSize != null) {
            strategyUnderTest.setMinSize(minSize);
        }
        if (parallelism != null) {
            strategyUnderTest.setParallelism(parallelism);
        }
        long startTime, elapsedTime;
        startTime = System.nanoTime(); // ns
        strategyUnderTest.execute(mat1, mat2, res);
        elapsedTime = (System.nanoTime() - startTime) / 1000000; // ms

        return elapsedTime;
    }
}
