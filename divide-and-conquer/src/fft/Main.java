package fft;

public class Main {

    public static void main(String[] args) {
        //TODO: sort dependency on sequential in testing
        FFTStrategy sequential = new Sequential(1000);
        FFTStrategy forkJoin = new ForkJoin(1000, 16, sequential);
        FFTStrategy threaded = new Threaded(1000, 16, sequential);

//        TestSuite.testVaryingMinSize(sequential, 10, 16, false);
//        TestSuite.testVaryingMinSize(forkJoin, 10, 16, false);
//        TestSuite.testVaryingMinSize(threaded, 10, 16, false);

        TestSuite.testVaryingParallelism(sequential, 13, 100, false);
        TestSuite.testVaryingParallelism(forkJoin, 13, 1000, false);
        TestSuite.testVaryingParallelism(threaded, 13, 1000, false);
    }
}
