package strassens;

public interface Matrix {
    Matrix add(Matrix other, Matrix result);
    Matrix sub(Matrix other, Matrix result);
    Matrix mult(Matrix other, Matrix result);
    void updateMatrix(Matrix matrixToCopy);
    int getValue(int rowIndex, int colIndex);
    void updateValue(int value, int rowIndex, int colIndex);
    boolean dimEquals(Matrix other);
    boolean isSquare();
    Matrix getUnderlying();
    Matrix[] quadrantSplit();
    int getNumRows();
    int getNumCols();
}
