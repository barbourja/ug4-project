package quickselect;

import java.util.Random;

public class Utils {

    public static <T extends Comparable<T>> int partition(T[] arr, int start, int end) { // select end element as pivot and partition
        T pivotVal = arr[end];

        int i = start - 1;
        for (int j = start; j < end; j++) {
            if (arr[j].compareTo(pivotVal) <= 0) {
                i++;
                // swap arr[j] and arr[i]
                T temp = arr[j];
                arr[j] = arr[i];
                arr[i] = temp;
            }
        }
        // swap arr[end] and arr[i+1]
        T temp = arr[end];
        arr[end] = arr[i + 1];
        arr[i + 1] = temp;

        return i + 1; // return final index of pivot
    }

    public static <T extends Comparable<T>> int random_partition(T[] arr, int start, int end) { // select random element as pivot and partition
        if (start < 0 || start >= arr.length || end < 0 || end >= arr.length || start > end) {
            throw new RuntimeException("Invalid indices passed!");
        }

        Random rand = new Random();
        int pivotIndex = rand.nextInt(start, end + 1);

        T temp = arr[end];
        arr[end] = arr[pivotIndex];
        arr[pivotIndex] = temp;

        return partition(arr, start, end);
    }

    public static <T extends Comparable<T>> void quickSort(T[] arr, int start, int end) { // quicksort array given start and end indices
        if (start < end) {
            int pivotIndex = random_partition(arr, start, end);
            quickSort(arr, start, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, end);
        }
    }

}
