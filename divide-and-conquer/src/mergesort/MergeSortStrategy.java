package mergesort;

import java.util.List;

public interface MergeSortStrategy<T extends Comparable<T>> {
    List<T> execute(List<T> arrToSort);

}
