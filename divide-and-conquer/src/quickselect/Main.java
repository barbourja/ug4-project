package quickselect;

import static java.lang.Math.*;

public class Main {

    public static void main(String[] args) {
        testMinSize();
    }

    public static void testParallelism() {  // Testing varying parallelism
        int N = 26;
        int inputSize = (int) Math.pow(2, N);

        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        QuickSelectStrategy<Integer> sequential = new Sequential();
        QuickSelectStrategy<Integer> forkJoin = new ForkJoin(1, 1, sequential);
        QuickSelectStrategy<Integer> threaded = new Threaded(1, 1, sequential);


        QuickSelectTestSuite quickSelectTest = new QuickSelectTestSuite(10);
        quickSelectTest.testVaryingParallelism(sequential, inputSize, parallelismValues,true);
        quickSelectTest.testVaryingParallelism(forkJoin, inputSize,  parallelismValues, true);
        quickSelectTest.testVaryingParallelism(threaded, inputSize,  parallelismValues, true);
    }

    private static void testMinSize() { // Testing varying min size (granularity/threading overhead)
        int N = 26;
        int inputSize = (int) Math.pow(2, N);
        int parallelism = 2048;

        int maxLevelReached = (int) ceil(log(parallelism) / log(2));
        int minimumMinSize = (int) ceil(inputSize / pow(2, maxLevelReached));

        Integer[] minSizeValues = new Integer[7];
        int currMinSize = minimumMinSize;
        for (int i = 0; i < minSizeValues.length; i++) {
            minSizeValues[i] = currMinSize;
            currMinSize = currMinSize * 2;
        }


        QuickSelectStrategy<Integer> sequential = new Sequential<>();
        QuickSelectStrategy<Integer> forkJoin = new ForkJoin<>(1, parallelism, sequential);
        QuickSelectStrategy<Integer> threaded = new Threaded<>(1, parallelism, sequential);

        QuickSelectTestSuite mergeSortTest = new QuickSelectTestSuite(10);
        mergeSortTest.testVaryingMinSize(sequential, inputSize, parallelism, minSizeValues,true);
        mergeSortTest.testVaryingMinSize(forkJoin, inputSize, parallelism, minSizeValues, true);
        mergeSortTest.testVaryingMinSize(threaded, inputSize, parallelism, minSizeValues, true);

    }
}
