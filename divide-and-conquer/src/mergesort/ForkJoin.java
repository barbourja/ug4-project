package mergesort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoin<T extends Comparable<T>> implements MergeSortStrategy<T>{

    private class MergeSortTask extends RecursiveTask {

        protected static final int MIN_THRESHOLD = 100000;
        private final List<T> arrToSort;


        public MergeSortTask(List<T> arrToSort) {
            this.arrToSort = arrToSort;
        }

        private List<T> merge(List<T> arr1, List<T> arr2) {
            List<T> mergedList = new ArrayList<>();
            while (arr1.size() > 0 || arr2.size() > 0) {
                T arr1Val = arr1.size() > 0 ? arr1.get(0) : null;
                T arr2Val = arr2.size() > 0 ? arr2.get(0) : null;
                if (arr1Val == null) { // Array 1 exhausted; add array 2 value to merged list
                    mergedList.add(arr2Val);
                    arr2.remove(0);
                }
                else if (arr2Val == null) { // Array 2 exhausted; add array 1 value to merged list
                    mergedList.add(arr1Val);
                    arr1.remove(0);
                }
                else if (arr1Val.compareTo(arr2Val) <= 0) { // Array 1 value less than or equal to array 2 value; add array 1 value to merged list
                    mergedList.add(arr1Val);
                    arr1.remove(0);
                }
                else {
                    mergedList.add(arr2Val); // Array 2 value less than array 1 value; add array 2 value to merged list
                    arr2.remove(0);
                }
            }

            return mergedList;
        }

        protected List<T> computeDirectly() {
            this.arrToSort.sort(null);
            return this.arrToSort;
        }

        @Override
        protected List<T> compute() {
            if (arrToSort.size() <= MIN_THRESHOLD) {
                return computeDirectly();

            }
            else {
                int midPoint = arrToSort.size() / 2;
                MergeSortTask lowerHalfTask = new MergeSortTask(new ArrayList<>(arrToSort.subList(0, midPoint)));
                MergeSortTask upperHalfTask = new MergeSortTask(new ArrayList<>(arrToSort.subList(midPoint, arrToSort.size())));

                lowerHalfTask.fork();
                upperHalfTask.fork();

                return merge((List<T>) lowerHalfTask.join(), (List<T>) upperHalfTask.join());
            }
        }
    }

    public List<T> execute(List<T> arrToSort) {
        MergeSortTask mergeTask = new MergeSortTask(arrToSort);
        ForkJoinPool pool = new ForkJoinPool(8);
        pool.execute(mergeTask);
        return (List<T>) mergeTask.join();
    }
}
