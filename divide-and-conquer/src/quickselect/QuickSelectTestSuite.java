package quickselect;

import generic.GenericStrategy;
import generic.GenericTestSuite;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.*;

public class QuickSelectTestSuite extends GenericTestSuite {

    public QuickSelectTestSuite(int numRunsPerInput) {
        super(numRunsPerInput);
    }

    @Override
    public Long[] testVaryingParallelism(GenericStrategy strategyUnderTest, Integer inputSize, Integer[] valuesToTest, boolean fullPrinting) { // adjusted minsize calculation to allow parallel algorithms to create all/more threads
        System.out.println(strategyUnderTest.toString(false, false) + "| Input Size = " + inputSize + " - Varying parallelism");

        if (strategyUnderTest.isSequential()) {
            valuesToTest = new Integer[]{1};
        }

        ArrayList<Long> runtimes = new ArrayList<>();
        for (int parallelism : valuesToTest) {
            int minSize;
            if (!strategyUnderTest.isSequential()) {
                int maxLevelReached = (int) ceil(log(parallelism) / log(2));
                minSize = (int) ceil(inputSize / pow(2, maxLevelReached));
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
        if (!(strategyUnderTest instanceof QuickSelectStrategy)) {
            throw new RuntimeException("Incorrect strategy type passed! Expected QuickSelect!");
        }
        ArrayList<Long> runtimes = new ArrayList<>();
        for (int i = 0; i < NUM_RUNS_PER_INPUT; i++) {
            Random rand = new Random();
            Integer[] input = new Integer[inputSize];
            for (int j = 0; j < inputSize; j++) {
                input[j] = rand.nextInt();
            }
            int k = rand.nextInt(inputSize);
            runtimes.add(runSimpleTest((QuickSelectStrategy<Integer>) strategyUnderTest, input, k, minSize, parallelism));
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

    private static long runSimpleTest(QuickSelectStrategy<Integer> strategyUnderTest, Integer[] input, Integer kthElem, Integer minSize, Integer parallelism) { // return run time
        if (minSize != null) {
            strategyUnderTest.setMinSize(minSize);
        }
        if (parallelism != null) {
            strategyUnderTest.setParallelism(parallelism);
        }

        long startTime, elapsedTime;
        startTime = System.nanoTime(); // ns
        strategyUnderTest.execute(input, kthElem);
        elapsedTime = (System.nanoTime() - startTime) / 1000000; // ms

        return elapsedTime;
    }
}
