package mergesort;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static <T extends Comparable<T>> List<T> merge(List<T> arr1, List<T> arr2) {
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

    public static <T extends Comparable<T>> void insertionSortInPlace(List<T> arrToSort) {
        for (int i = 1; i < arrToSort.size(); i++){
            int currPos = i;
            T currElem = arrToSort.get(currPos);

            while (currPos > 0 && currElem.compareTo(arrToSort.get(currPos - 1)) < 0) {
                arrToSort.set(currPos, arrToSort.get(currPos - 1));
                currPos--;
            }
            arrToSort.set(currPos, currElem);
        }
    }
}
