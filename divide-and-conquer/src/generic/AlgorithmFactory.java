package generic;

import java.util.Collection;

abstract class AlgorithmFactory<T extends Collection<T>> {
    abstract SequentialAlgorithm<T> createSequential(int minSize);

    abstract ForkJoinAlgorithm<T> createForkJoin(int minSize, int parallelism, Algorithm<T> baseCaseStrategy);

    abstract ThreadedAlgorithm<T> createThreaded(int minSize, int parallelism, Algorithm<T> baseCaseStrategy);
}
