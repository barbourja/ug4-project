package quickselect;

public class Main {

    public static void main(String[] args) {
        testParallelism();
    }

    public static void testParallelism() {  // Testing varying parallelism
        int N = 26;
        int inputSize = (int) Math.pow(2, N);

        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        QuickSelectStrategy<Integer> sequential = new Sequential();
        QuickSelectStrategy<Integer> forkJoin = new ForkJoin(1, 1, sequential);
        QuickSelectStrategy<Integer> threaded = new Threaded(1, 1, sequential);


        QuickSelectTestSuite quickSelectTest = new QuickSelectTestSuite(10);
        quickSelectTest.testVaryingParallelism(sequential, inputSize, parallelismValues,true);
        quickSelectTest.testVaryingParallelism(forkJoin, inputSize,  parallelismValues, true);
        quickSelectTest.testVaryingParallelism(threaded, inputSize,  parallelismValues, true);
    }
}
