package quickselect;

import generic.GenericStrategy;
import generic.GenericTestSuite;

import java.util.ArrayList;
import java.util.Random;

public class QuickSelectTestSuite extends GenericTestSuite {

    public QuickSelectTestSuite(int numRunsPerInput) {
        super(numRunsPerInput);
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
