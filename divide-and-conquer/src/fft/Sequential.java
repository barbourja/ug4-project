package fft;

public class Sequential implements FFTStrategy {

    protected int MIN_SEQUENCE_SIZE;

    public Sequential(int minSequenceSize) {
        this.MIN_SEQUENCE_SIZE = minSequenceSize;
    }

    private Complex[] bruteForceDFT(Complex[] f) {
        int n = f.length;
        Complex[] F = new Complex[n];
        for (int k = 0; k < n; k++) {
            F[k] = new Complex(0, 0);
            for (int i = 0; i < n; i++) {
                double exp_i = (-2 * i * k * Math.PI) / n;
                Complex twiddle = new Complex(Math.cos(exp_i), Math.sin(exp_i));
                F[k] = F[k].add(f[i].mult(twiddle));
            }
        }
        return F;
    }

    public Complex[] execute(Complex[] f) {
        int n = f.length;
        if (n <= MIN_SEQUENCE_SIZE) { // base case
            return bruteForceDFT(f);
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
            Complex[] F_even = execute(f_even);
            Complex[] F_odd = execute(f_odd);
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

    @Override
    public int getMinSize() {
        return MIN_SEQUENCE_SIZE;
    }

    @Override
    public int getParallelism() {
        return 1;
    }

    @Override
    public void setMinSize(int size) {
        this.MIN_SEQUENCE_SIZE = size;
    }

    @Override
    public void setParallelism(int parallelism) {
        // sequential - do nothing
    }

    @Override
    public boolean isSequential() {
        return true;
    }

    @Override
    public String toString() {
        return "FFT Sequential | Minimum Sequence Size = " + MIN_SEQUENCE_SIZE;
    }

    @Override
    public String toString(boolean minSize, boolean parallelism) {
        StringBuilder sb = new StringBuilder();
        sb.append("FFT Sequential ");
        if (minSize) {
            sb.append("| Minimum Sequence Size = " + MIN_SEQUENCE_SIZE);
        }
        return sb.toString();
    }
}
