package fft;

public class Main {

    public static void main(String[] args) {
        // Testing varying parallelism

        int N = 20;
        int inputSize = (int) Math.pow(2, N);

        FFTStrategy sequential = new Sequential(1000);
        FFTStrategy forkJoin = new ForkJoin(1000, 16, sequential);
        FFTStrategy threaded = new Threaded(1000, 16, sequential);


        FFTTestSuite fftTest = new FFTTestSuite(5);
        fftTest.testVaryingParallelism(sequential, inputSize, 100, true);
        fftTest.testVaryingParallelism(forkJoin, inputSize, 100, true);
        fftTest.testVaryingParallelism(threaded, inputSize, 100, true);

    }
}
