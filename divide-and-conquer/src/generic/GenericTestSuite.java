package generic;

import java.util.ArrayList;

abstract public class GenericTestSuite {
    protected final int NUM_RUNS_PER_INPUT;

    public GenericTestSuite(int numRunsPerInput) {
        NUM_RUNS_PER_INPUT = numRunsPerInput;
    }

    public Long[] testVaryingParallelism(GenericStrategy strategyUnderTest, Integer inputSize, Integer minSize, boolean fullPrinting) { // return array of average run times
        strategyUnderTest.setMinSize(minSize);
        ArrayList<Long> runtimes = new ArrayList<>();
        System.out.println(strategyUnderTest.toString(true, false) + " | Input Size = " + inputSize + " - Varying parallelism");

        int[] valuesToTest = new int[]{1, 2, 4, 8, 16, 32, 64};

        if (strategyUnderTest.isSequential()) {
            valuesToTest = new int[]{1};
        }

        for (int parallelism : valuesToTest) {
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

    public Long[] testVaryingMinSize(GenericStrategy strategyUnderTest, Integer inputSize, Integer parallelism, boolean fullPrinting) { // return array of average run times
        strategyUnderTest.setParallelism(parallelism);
        ArrayList<Long> runTimes = new ArrayList<>();
        System.out.println(strategyUnderTest.toString(false, true) + " | Input Size = " + inputSize + " - Varying minimum input size");

        int[] valuesToTest = new int[]{1, 10, 100, 1000, 10000};

        for (int minSize : valuesToTest) {
            long runtime = testInput(strategyUnderTest, inputSize, minSize, parallelism, fullPrinting);
            runTimes.add(runtime);
        }

        // dump csv
        System.out.print("  ");
        for (int i = 0; i < valuesToTest.length - 1; i++) {
            System.out.print(valuesToTest[i] + ",");
        }
        System.out.print(valuesToTest[valuesToTest.length - 1] + "\n");

        System.out.print("  ");
        for (int i = 0; i < runTimes.size() - 1; i++) {
            System.out.print(runTimes.get(i) + ",");
        }
        System.out.print(runTimes.get(runTimes.size() - 1) + "\n");

        return runTimes.toArray(new Long[0]);
    }

    public abstract long testInput(GenericStrategy strategyUnderTest, Integer inputSize, Integer minSize, Integer parallelism, boolean fullPrinting); // return average run time across runs of input

}
