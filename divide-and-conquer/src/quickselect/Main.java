package quickselect;

public class Main {

    public static void main(String[] args) {
        //TODO: sort dependency on sequential in testing
        QuickSelectStrategy<Integer> sequential = new Sequential();
        QuickSelectStrategy<Integer> forkJoin = new ForkJoin(1000, 16, sequential);
        QuickSelectStrategy<Integer> threaded = new Threaded(1000, 16, sequential);


        final int N = 18;
        int inputSize = (int) Math.pow(2, N);

        QuickSelectTestSuite qsTest = new QuickSelectTestSuite(5);
        qsTest.testVaryingParallelism(sequential, inputSize, 100, true);
        qsTest.testVaryingParallelism(forkJoin, inputSize, 100, true);
        qsTest.testVaryingParallelism(threaded, inputSize, 100, true);
    }
}
