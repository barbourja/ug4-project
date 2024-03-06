package fft;

import static java.lang.Math.*;

public class Main {

    public static void main(String[] args) {
        testMinSize();
    }

    private static void testParallelism() { // Testing varying parallelism
        int N = 23;
        int inputSize = (int) Math.pow(2, N);
        int seqMinSize = 8;

        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        FFTStrategy sequential = new Sequential(seqMinSize);
        FFTStrategy forkJoin = new ForkJoin(1, 1, sequential);
        FFTStrategy threaded = new Threaded(1, 1, sequential);


        FFTTestSuite fftTest = new FFTTestSuite(10);
        fftTest.testVaryingParallelism(sequential, inputSize, parallelismValues,true);
        fftTest.testVaryingParallelism(forkJoin, inputSize,  parallelismValues, true);
        fftTest.testVaryingParallelism(threaded, inputSize,  parallelismValues, true);
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


        FFTStrategy sequential = new Sequential(seqMinSize);
        FFTStrategy forkJoin = new ForkJoin(1, parallelism, sequential);
        FFTStrategy threaded = new Threaded(1, parallelism, sequential);

        FFTTestSuite fftTest = new FFTTestSuite(10);
        fftTest.testVaryingMinSize(sequential, inputSize, parallelism, minSizeValues,true);
        fftTest.testVaryingMinSize(forkJoin, inputSize, parallelism, minSizeValues, true);
        fftTest.testVaryingMinSize(threaded, inputSize, parallelism, minSizeValues, true);

    }
}
