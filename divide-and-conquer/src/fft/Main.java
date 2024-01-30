package fft;

public class Main {

    public static void main(String[] args) {
        //TODO: sort dependency on sequential in testing
        FFTStrategy sequential = new Sequential(1000);
        FFTStrategy forkJoin = new ForkJoin(1000, 16, sequential);
        FFTStrategy threaded = new Threaded(1000, 16, sequential);

        final int N = 20;
        int inputSize = (int) Math.pow(2, N);

        FFTTestSuite fftTest = new FFTTestSuite(5);
        fftTest.testVaryingParallelism(sequential, inputSize, 100, true);
        fftTest.testVaryingParallelism(forkJoin, inputSize, 100, true);
        fftTest.testVaryingParallelism(threaded, inputSize, 100, true);

    }
}
