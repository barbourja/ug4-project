package mergesort;

public class Main {
    public static void main(String[] args) {
        //TODO: sort dependency on sequential in testing
        MergeSortStrategy<Integer> sequential = new Sequential<>(1000);
        MergeSortStrategy<Integer> forkJoin = new ForkJoin<>(1000, 16, sequential);
        MergeSortStrategy<Integer> threaded = new Threaded<>(1000, 16, sequential);

        final int N = 20;
        int inputSize = (int) Math.pow(2, N);

        MergeSortTestSuite msTest = new MergeSortTestSuite(5);
        msTest.testVaryingParallelism(sequential, inputSize, 100, true);
        msTest.testVaryingParallelism(forkJoin, inputSize, 100, true);
        msTest.testVaryingParallelism(threaded, inputSize, 100, true);
    }
}