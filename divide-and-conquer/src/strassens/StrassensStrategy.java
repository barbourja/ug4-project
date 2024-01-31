package strassens;

import generic.GenericStrategy;

public interface StrassensStrategy extends GenericStrategy {
    Matrix execute(Matrix mat1, Matrix mat2, Matrix res);
}
