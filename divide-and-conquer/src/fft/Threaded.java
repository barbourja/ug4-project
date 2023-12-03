package fft;

public class Threaded implements FFTStrategy{

    protected final int MIN_SEQUENCE_SIZE;
    protected final int PARALLELISM;
    protected final FFTStrategy BASE_CASE_STRATEGY;
    protected final int DIVISION_FACTOR = 2;
    protected int threadCount;

    public Threaded(int minSequenceSize, int parallelism, FFTStrategy baseCaseStrategy) {
        this.MIN_SEQUENCE_SIZE = minSequenceSize;
        this.PARALLELISM = parallelism;
        this.BASE_CASE_STRATEGY = baseCaseStrategy;
    }

    private synchronized void updateThreadCount(int val) {
        threadCount += val;
        System.out.println(threadCount); //TODO: remove debug output
    }

    private synchronized boolean requestThreads() {
        if (threadCount + DIVISION_FACTOR <= PARALLELISM) {
            updateThreadCount(DIVISION_FACTOR);
            return true;
        }
        return false;
    }

    private class FFTTask implements Runnable {

        private final Complex[] f;
        private Complex[] res;

        public FFTTask(Complex[] f) {
            this.f = f;
            this.res = new Complex[f.length];
        }

        public Complex[] getResult() {
            return res;
        }

        private boolean baseCondition(int n) {
            return (n <= MIN_SEQUENCE_SIZE);
        }

        private void computeDirectly() {
            res = BASE_CASE_STRATEGY.execute(f);
        }

        @Override
        public void run() {
            int n = f.length;
            if (baseCondition(n)) { // base case
                computeDirectly();
            }
            else { // perform fft
                if (n % 2 != 0) {
                    throw new RuntimeException("N must be even to perform FFT!");
                }

                boolean threaded = requestThreads();

                if (threaded) {
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
                    Thread evenThread = new Thread(evenTask);
                    Thread oddThread = new Thread(oddTask);
                    evenThread.start();
                    oddThread.start();
                    try {
                        evenThread.join();
                        oddThread.join();
                        updateThreadCount(-DIVISION_FACTOR);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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

                else {
                    computeDirectly();
                }
            }
        }
    }

    @Override
    public Complex[] execute(Complex[] f) {
        FFTTask startTask = new FFTTask(f);
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
}
