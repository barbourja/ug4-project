package fft;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoin implements FFTStrategy {

    protected int MIN_SEQUENCE_SIZE;
    protected int PARALLELISM;
    protected final FFTStrategy BASE_CASE_STRATEGY;

    public ForkJoin(int minSequenceSize, int parallelism, FFTStrategy baseCaseStrategy) {
        if (parallelism < 1 || minSequenceSize < 1) {
            throw new RuntimeException("Parallelism/minimum sequence size cannot be < 1.");
        }
        this.MIN_SEQUENCE_SIZE = minSequenceSize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private class FFTTask extends RecursiveTask {

        private final Complex[] f;

        public FFTTask(Complex[] f) {
            this.f = f;
        }

        private Complex[] computeDirectly() {
            return BASE_CASE_STRATEGY.execute(f);
        }

        @Override
        protected Complex[] compute() {
            int n = f.length;
            if (n <= MIN_SEQUENCE_SIZE) { // base case
                return computeDirectly();
            }
            else { // perform fft
                if (n % 2 != 0) {
                    throw new RuntimeException("N must be even to perform FFT!");
                }
                Complex[] F = new Complex[n];
                Complex[] f_odd = new Complex[n/2];
                Complex[] f_even = new Complex[n/2];
                boolean odd = false;
                for (int i = 0; i < n; i++) {
                    if (odd) {
                        f_odd[i/2] = f[i];
                    }
                    else {
                        f_even[i/2] = f[i];
                    }
                    odd = !odd;
                }

                FFTTask evenTask = new FFTTask(f_even);
                FFTTask oddTask = new FFTTask(f_odd);
                invokeAll(evenTask, oddTask);
                Complex[] F_even = (Complex[]) evenTask.join();
                Complex[] F_odd = (Complex[]) oddTask.join();

                for (int i = 0; i < n/2; i++) {
                    double exp_i = (-2 * i * Math.PI) / n;
                    Complex twiddle = new Complex(Math.cos(exp_i), Math.sin(exp_i));
                    Complex oddTerm = F_odd[i].mult(twiddle);
                    F[i] = F_even[i].add(oddTerm);
                    F[n/2 + i] = F_even[i].sub(oddTerm);
                }
                return F;
            }
        }
    }

    @Override
    public Complex[] execute(Complex[] f) {
        FFTTask task = new FFTTask(f);
        ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
        pool.execute(task);
        return (Complex[]) task.join();
    }

    @Override
    public int getMinSize() {
        return MIN_SEQUENCE_SIZE;
    }

    @Override
    public int getParallelism() {
        return PARALLELISM;
    }

    @Override
    public void setMinSize(int size) {
        if (size >= 1) {
            this.MIN_SEQUENCE_SIZE = size;
        }
    }

    @Override
    public void setParallelism(int parallelism) {
        if (parallelism >= 1) {
            this.PARALLELISM = parallelism;
        }
    }

    @Override
    public boolean isSequential() {
        return false;
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("FFT ForkJoin ");
        if (minSize) {
            sb.append("| Minimum Sequence Size = " + MIN_SEQUENCE_SIZE + " ");
        }
        if (parallelism) {
            sb.append("| Parallelism = " + PARALLELISM + " ");
        }
        return sb.toString();
    }
}
