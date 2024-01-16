package fft;

public interface FFTStrategy {
    Complex[] execute(Complex[] f);

    int getMinSize();

    int getParallelism();

    void setMinSize(int size);

    void setParallelism(int parallelism);

    String toString(boolean minSize, boolean parallelism);
}
