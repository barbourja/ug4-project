package fft;

public class Main {

    public static void main(String[] args) {
        // Testing varying parallelism
        int N = 23;
        int inputSize = (int) Math.pow(2, N);
        int seqMinSize = 8;

        Integer[] parallelismValues = new Integer[]{4, 8, 16, 32, 64};

        FFTStrategy sequential = new Sequential(seqMinSize);
        FFTStrategy forkJoin = new ForkJoin(1, 1, sequential);
        FFTStrategy threaded = new Threaded(1, 1, sequential);


        FFTTestSuite fftTest = new FFTTestSuite(5);
        fftTest.testVaryingParallelism(sequential, inputSize, parallelismValues,true);
        fftTest.testVaryingParallelism(forkJoin, inputSize,  parallelismValues, true);
        fftTest.testVaryingParallelism(threaded, inputSize,  parallelismValues, true);

    }
}
