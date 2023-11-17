package mergesort;

public class Utils {

    public static <T extends Comparable<T>> T[] merge(T[] arrToMerge, int start, int mid, int end) { // intervals are [start, mid) and [mid, end)
        T[] mergedList = (T[]) new Comparable[end - start];
        int i = start;
        int j = mid;
        int k = 0;
        while (i < mid || j < end) {
            T arr1Val = i < mid ? arrToMerge[i] : null;
            T arr2Val = j < end ? arrToMerge[j] : null;
            if (arr1Val == null) { // Array 1 exhausted; add array 2 value to merged list
                mergedList[k] = arr2Val;
                j++;
            }
            else if (arr2Val == null) { // Array 2 exhausted; add array 1 value to merged list
                mergedList[k] = arr1Val;
                i++;
            }
            else if (arr1Val.compareTo(arr2Val) <= 0) { // Array 1 value less than or equal to array 2 value; add array 1 value to merged list
                mergedList[k] = arr1Val;
                i++;
            }
            else {
                mergedList[k] = arr2Val;
                j++;
            }
            k++;
        }

        for (int z = 0; z < mergedList.length; z++) {
            arrToMerge[start + z] = mergedList[z];
        }

        return arrToMerge;
    }

    public static <T extends Comparable<T>> void insertionSortInPlace(T[] arrToSort, int start, int end) { // interval is [start, end)
        for (int i = start + 1; i < end; i++){
            int currPos = i;
            T currElem = arrToSort[currPos];

            while (currPos > start && currElem.compareTo(arrToSort[currPos - 1]) < 0) {
                arrToSort[currPos] = arrToSort[currPos - 1];
                currPos--;
            }
            arrToSort[currPos] = currElem;
        }
    }
}
