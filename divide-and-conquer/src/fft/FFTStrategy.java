package fft;

import generic.GenericStrategy;

public interface FFTStrategy extends GenericStrategy {
    Complex[] execute(Complex[] f);

    int getMinSize();

    int getParallelism();

    void setMinSize(int size);

    void setParallelism(int parallelism);

    String toString(boolean minSize, boolean parallelism);
}
