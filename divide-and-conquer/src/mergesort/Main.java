package mergesort;

import static java.lang.Math.*;

public class Main {
    public static void main(String[] args) {
        testParallelism();
    }

    public static void testParallelism() {  // Testing varying parallelism
        int N = 23;
        int inputSize = (int) Math.pow(2, N);
        int seqMinSize = 8;

        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        MergeSortStrategy<Integer> sequential = new Sequential(seqMinSize);
        MergeSortStrategy<Integer> forkJoin = new ForkJoin(1, 1, sequential);
        MergeSortStrategy<Integer> threaded = new Threaded(1, 1, sequential);


        MergeSortTestSuite mergeSortTest = new MergeSortTestSuite(10);
        mergeSortTest.testVaryingParallelism(sequential, inputSize, parallelismValues,true);
        mergeSortTest.testVaryingParallelism(forkJoin, inputSize,  parallelismValues, true);
        mergeSortTest.testVaryingParallelism(threaded, inputSize,  parallelismValues, true);
    }

    private static void testMinSize() { // Testing varying min size (granularity/threading overhead)
        int N = 23;
        int inputSize = (int) Math.pow(2, N);
        int seqMinSize = 8;
        int parallelism = 2048;

        int maxLevelReached = (int) ceil(log(parallelism) / log(2));
        int minimumMinSize = (int) ceil(inputSize / pow(2, maxLevelReached));

        Integer[] minSizeValues = new Integer[7];
        int currMinSize = minimumMinSize;
        for (int i = 0; i < minSizeValues.length; i++) {
            minSizeValues[i] = currMinSize;
            currMinSize = currMinSize * 2;
        }


        MergeSortStrategy<Integer> sequential = new Sequential(seqMinSize);
        MergeSortStrategy<Integer> forkJoin = new ForkJoin(1, parallelism, sequential);
        MergeSortStrategy<Integer> threaded = new Threaded(1, parallelism, sequential);

        MergeSortTestSuite mergeSortTest = new MergeSortTestSuite(10);
        mergeSortTest.testVaryingMinSize(sequential, inputSize, parallelism, minSizeValues,true);
        mergeSortTest.testVaryingMinSize(forkJoin, inputSize, parallelism, minSizeValues, true);
        mergeSortTest.testVaryingMinSize(threaded, inputSize, parallelism, minSizeValues, true);

    }
}