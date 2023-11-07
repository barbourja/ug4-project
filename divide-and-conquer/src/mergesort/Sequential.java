package mergesort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sequential<T extends Comparable<T>> implements MergeSortStrategy<T>{
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

    public List<T> execute(List<T> arrToSort) {
        int MIN_THRESHOLD = 1;
        if (arrToSort.size() <= MIN_THRESHOLD) { // Base case
            arrToSort.sort(null);
            return new ArrayList<>(arrToSort);
        }
        else { // Recursive case
            int midPoint = arrToSort.size() / 2;
            List<T> lowerHalf = new ArrayList<>(arrToSort.subList(0, midPoint));
            List<T> upperHalf = new ArrayList<>(arrToSort.subList(midPoint, arrToSort.size()));

            return merge(execute(lowerHalf), execute(upperHalf));
        }
    }
}
