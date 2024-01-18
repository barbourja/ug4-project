package mergesort;


public class Main {
    public static void main(String[] args) {
        //TODO: sort dependency on sequential in testing
        MergeSortStrategy<Integer> sequential = new Sequential<>(100);
        MergeSortStrategy<Integer> forkJoin = new ForkJoin<>(100, 16, sequential);
        MergeSortStrategy<Integer> threaded = new Threaded<>(100, 16, sequential);

//        TestSuite.testVaryingMinSize(sequential, 6, 1, true);

        sequential.setMinSize(100); // set sequential to best performing min size
        TestSuite.testVaryingMinSize(forkJoin, 8, 16, true);
        TestSuite.testVaryingMinSize(threaded, 8, 16, true);
    }
}