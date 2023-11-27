package strassens;

public interface Matrix {
    Matrix add(Matrix other, Matrix result);
    Matrix sub(Matrix other, Matrix result);
    Matrix mult(Matrix other, Matrix result);
    void updateMatrix(Matrix matrixToCopy);
    void updateRow(int[] values, int rowIndex);
    void updateCol(int[] values, int colIndex);
    int getValue(int rowIndex, int colIndex);
    void updateValue(int value, int rowIndex, int colIndex);
    boolean dimEquals(Matrix other);
    boolean isSquare();
    Matrix[] quadrantSplit();
    int[] getRow(int rowIndex);
    int[] getCol(int colIndex);
    int getNumRows();
    int getNumCols();
}
