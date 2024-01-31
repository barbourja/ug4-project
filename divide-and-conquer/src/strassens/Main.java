package strassens;

public class Main {
    public static void main(String[] args) {

        final int N = 2048;

        StrassensStrategy sequential = new Sequential(128);
        StrassensStrategy forkJoin = new ForkJoin(128, 16, sequential);
        StrassensStrategy threaded = new Threaded(128, 16, sequential);

        StrassensTestSuite strassenTest = new StrassensTestSuite(5);
        strassenTest.testVaryingParallelism(sequential, N, 128, true);
        strassenTest.testVaryingParallelism(forkJoin, N, 128, true);
        strassenTest.testVaryingParallelism(threaded, N, 128, true);

    }
}
