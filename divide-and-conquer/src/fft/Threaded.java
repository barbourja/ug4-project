package fft;

import java.util.ArrayList;

import static java.lang.Math.floor;
import static java.lang.Math.log;

public class Threaded implements FFTStrategy{

    protected int MIN_SEQUENCE_SIZE;
    protected int PARALLELISM;
    protected final FFTStrategy BASE_CASE_STRATEGY;
    protected final int DIVISION_FACTOR = 2;
    protected final int MAX_LEVEL;
    protected int threadCount;

    public Threaded(int minSequenceSize, int parallelism, FFTStrategy baseCaseStrategy) {
        if (parallelism < 1 || minSequenceSize < 1) {
            throw new RuntimeException("Parallelism/minimum sequence size cannot be < 1.");
        }
        this.MIN_SEQUENCE_SIZE = minSequenceSize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
        this.MAX_LEVEL = (int) floor(log((DIVISION_FACTOR - 1) * PARALLELISM)/log(DIVISION_FACTOR));
    }

    private synchronized void updateNumThreads(int numThreadChange) {
        threadCount += numThreadChange;
    }

    private synchronized int requestThreads(int level) { // returns number of threads allocated
        int allocate;
        if (level < MAX_LEVEL && (threadCount + DIVISION_FACTOR <= PARALLELISM)) { // allocate all requested threads
            allocate = DIVISION_FACTOR;
            updateNumThreads(DIVISION_FACTOR);
        }
        else if (level < MAX_LEVEL && threadCount < PARALLELISM) { // partially fulfil request
            allocate = PARALLELISM - threadCount;
            updateNumThreads(allocate);
        }
        else {
            allocate = 0;
        }
        return allocate;
    }

    private class FFTTask implements Runnable {

        private final Complex[] f;
        private final int n;
        private final int CURR_LEVEL;
        private Complex[] res;

        public FFTTask(Complex[] f, int currLevel) {
            this.f = f;
            this.n = f.length;
            this.res = new Complex[f.length];
            this.CURR_LEVEL = currLevel;
        }

        public Complex[] getResult() {
            return res;
        }

        private boolean baseCondition() {
            return (n <= MIN_SEQUENCE_SIZE);
        }

        private void computeDirectly() {
            res = BASE_CASE_STRATEGY.execute(f);
        }

        @Override
        public void run() {
            if (n % 2 != 0) {
                throw new RuntimeException("N must be even to perform FFT!");
            }
            int numThreads = requestThreads(CURR_LEVEL);

            if (baseCondition() || numThreads == 0) { // base case
                computeDirectly();
            }
            else { // perform fft
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

                FFTTask evenTask = new FFTTask(f_even, CURR_LEVEL + 1);
                FFTTask oddTask = new FFTTask(f_odd, CURR_LEVEL + 1);

                if (numThreads == 1) { // restricted to 1 thread
                    Thread evenThread = new Thread(evenTask);
                    evenThread.start();
                    oddTask.run();
                    try {
                        evenThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else { // can use 2 threads
                    Thread evenThread = new Thread(evenTask);
                    Thread oddThread = new Thread(oddTask);
                    evenThread.start();
                    oddThread.start();
                    try {
                        evenThread.join();
                        oddThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                updateNumThreads(-numThreads);

                Complex[] F_even = evenTask.getResult();
                Complex[] F_odd = oddTask.getResult();

                for (int i = 0; i < n/2; i++) {
                    double exp_i = (-2 * i * Math.PI) / n;
                    Complex twiddle = new Complex(Math.cos(exp_i), Math.sin(exp_i));
                    Complex oddTerm = F_odd[i].mult(twiddle);
                    F[i] = F_even[i].add(oddTerm);
                    F[n/2 + i] = F_even[i].sub(oddTerm);
                }
                res = F;
            }
        }
    }

    @Override
    public Complex[] execute(Complex[] f) {
        FFTTask startTask = new FFTTask(f, 0);
        Thread startThread = new Thread(startTask);
        threadCount = 1;
        startThread.start();
        try {
            startThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return startTask.getResult();
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
    public String toString() {
        return "FFT Threaded | Minimum Sequence Size = " + MIN_SEQUENCE_SIZE + " | Parallelism = " + PARALLELISM;
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("FFT Threaded ");
        if (minSize) {
            sb.append("| Minimum Sequence Size = " + MIN_SEQUENCE_SIZE + " ");
        }
        if (parallelism) {
            sb.append("| Parallelism = " + PARALLELISM + " ");
        }
        return sb.toString();
    }
}
