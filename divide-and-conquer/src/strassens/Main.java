package strassens;

import static java.lang.Math.*;

public class Main {
    public static void main(String[] args) {
        testMinSize();
    }

    public static void testParallelism() { // Testing varying parallelism
        int N = 11;
        int inputSize = (int) Math.pow(2, N);
        int seqMinSize = 32;

        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        StrassensStrategy sequential = new Sequential(seqMinSize);
        StrassensStrategy forkJoin = new ForkJoin(1, 1, sequential);
        StrassensStrategy threaded = new Threaded(1, 1, sequential);


        StrassensTestSuite strassensTest = new StrassensTestSuite(10);
        strassensTest.testVaryingParallelism(sequential, inputSize, parallelismValues,true);
        strassensTest.testVaryingParallelism(forkJoin, inputSize,  parallelismValues, true);
        strassensTest.testVaryingParallelism(threaded, inputSize,  parallelismValues, true);
    }

    private static void testMinSize() { // Testing varying min size (granularity/threading overhead)
        int N = 11;
        int inputSize = (int) Math.pow(2, N);
        int parallelism = 128;
        int seqMinSize = 16;

        int maxLevelReached = (int) ceil(log(parallelism) / log(2));
        int minimumMinSize = (int) ceil(inputSize / pow(2, maxLevelReached));
        Integer[] minSizeValues = new Integer[7];
        int currMinSize = minimumMinSize;
        for (int i = 0; i < minSizeValues.length; i++) {
            minSizeValues[i] = currMinSize;
            currMinSize = currMinSize * 2;
        }


        StrassensStrategy sequential = new Sequential(seqMinSize);
        StrassensStrategy forkJoin = new ForkJoin(1, parallelism, sequential);
        StrassensStrategy threaded = new Threaded(1, parallelism, sequential);

        StrassensTestSuite strassensTest = new StrassensTestSuite(10);
        strassensTest.testVaryingMinSize(sequential, inputSize, parallelism, minSizeValues,true);
        strassensTest.testVaryingMinSize(forkJoin, inputSize, parallelism, minSizeValues, true);
        strassensTest.testVaryingMinSize(threaded, inputSize, parallelism, minSizeValues, true);

    }
}
