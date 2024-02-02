package generic;

import java.util.ArrayList;

import static java.lang.Math.*;

abstract public class GenericTestSuite {
    protected final int NUM_RUNS_PER_INPUT;

    public GenericTestSuite(int numRunsPerInput) {
        NUM_RUNS_PER_INPUT = numRunsPerInput;
    }

    public Long[] testVaryingParallelism(GenericStrategy strategyUnderTest, Integer inputSize, Integer[] valuesToTest, boolean fullPrinting) { // return array of average run times
        System.out.println(strategyUnderTest.toString(false, false) + "| Input Size = " + inputSize + " - Varying parallelism");

        if (strategyUnderTest.isSequential()) {
            valuesToTest = new Integer[]{1};
        }

        ArrayList<Long> runtimes = new ArrayList<>();
        for (int parallelism : valuesToTest) {
            int minSize;
            if (!strategyUnderTest.isSequential()) {
                int maxLevelReached = (int) ceil(log((strategyUnderTest.getDivisionFactor() - 1) * parallelism) / log(strategyUnderTest.getDivisionFactor()));
                minSize = (int) ceil(inputSize / pow(strategyUnderTest.getDivisionFactor(), maxLevelReached));
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

    public Long[] testVaryingMinSize(GenericStrategy strategyUnderTest, Integer inputSize, Integer parallelism, Integer[] valuesToTest, boolean fullPrinting) { // return array of average run times
        strategyUnderTest.setParallelism(parallelism);
        System.out.println(strategyUnderTest.toString(false, true) + " | Input Size = " + inputSize + " - Varying minimum input size");

        ArrayList<Long> runTimes = new ArrayList<>();
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
