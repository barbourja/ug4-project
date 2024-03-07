package fft;

import generic.GenericStrategy;

public interface FFTStrategy extends GenericStrategy {
    Complex[] execute(Complex[] f);
}
