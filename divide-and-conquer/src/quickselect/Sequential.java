package quickselect;

import static quickselect.Utils.random_partition;

public class Sequential<T extends Comparable<T>> implements QuickSelectStrategy<T>{

    public Sequential() {}

    @Override
    public T execute(T[] searchArr, int k) {
        return execute(searchArr, 0, searchArr.length - 1, k);
    }

    @Override
    public T execute(T[] arr, int start, int end, int k) { // k in interval [0, end - start]
        if (k < 0 || k > end - start) {
            throw new RuntimeException("Invalid value of k!");
        }

        k = start + k;

        int pivotIndex = random_partition(arr, start, end);
        if (pivotIndex == k) {
            return arr[k];
        }
        else if (k < pivotIndex) {
            return execute(arr, start, pivotIndex - 1, k - start);
        }
        else {
            return execute(arr, pivotIndex + 1, end, k - (start + pivotIndex + 1));
        }
    }
}
