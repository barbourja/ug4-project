package strassens;

import generic.GenericStrategy;
import generic.GenericTestSuite;
import quickselect.QuickSelectStrategy;

import java.util.ArrayList;
import java.util.Random;

public class StrassensTestSuite extends GenericTestSuite {
    public StrassensTestSuite(int numRunsPerInput) {
        super(numRunsPerInput);
    }

    @Override
    public long testInput(GenericStrategy strategyUnderTest, Integer inputSize, Integer minSize, Integer parallelism, boolean fullPrinting) {
        if (!(strategyUnderTest instanceof StrassensStrategy)) {
            throw new RuntimeException("Incorrect strategy type passed! Expected Strassens!");
        }
        ArrayList<Long> runtimes = new ArrayList<>();
        for (int i = 0; i < NUM_RUNS_PER_INPUT; i++) {
            Random rand = new Random();
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
