package fft;

public class Main {

    public static void main(String[] args) {

        FFTStrategy sequential = new Sequential(1000);
        FFTStrategy forkJoin = new ForkJoin(1000, 16, sequential);
        FFTStrategy threaded = new Threaded(1000, 16, sequential);

        TestSuite.testVaryingMinSize(sequential, 10, 16);
        TestSuite.testVaryingMinSize(forkJoin, 10, 16);
        TestSuite.testVaryingMinSize(threaded, 10, 16);
    }
}
