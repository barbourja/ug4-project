package strassens;

public class Main {
    public static void main(String[] args) {
        testParallelism();
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
}
