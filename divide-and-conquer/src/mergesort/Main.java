package mergesort;

public class Main {
    public static void main(String[] args) {
        testParallelism();
    }

    public static void testParallelism() {  // Testing varying parallelism
        int N = 23;
        int inputSize = (int) Math.pow(2, N);
        int seqMinSize = 8;

        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        MergeSortStrategy<Integer> sequential = new Sequential(seqMinSize);
        MergeSortStrategy<Integer> forkJoin = new ForkJoin(1, 1, sequential);
        MergeSortStrategy<Integer> threaded = new Threaded(1, 1, sequential);


        MergeSortTestSuite mergeSortTest = new MergeSortTestSuite(10);
        mergeSortTest.testVaryingParallelism(sequential, inputSize, parallelismValues,true);
        mergeSortTest.testVaryingParallelism(forkJoin, inputSize,  parallelismValues, true);
        mergeSortTest.testVaryingParallelism(threaded, inputSize,  parallelismValues, true);
    }
}