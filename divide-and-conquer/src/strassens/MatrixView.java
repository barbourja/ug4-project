package strassens;

import java.util.Objects;

public class MatrixView extends Matrix {

    private Matrix baseMatrix;
    private int startRow;
    private int startCol;
    private final int ROWS;
    private final int COLS;

    public MatrixView(Matrix baseMatrix, int startRow, int endRow, int startCol, int endCol) {
        if (startRow < 0 || startCol < 0 || endRow >= baseMatrix.getNumRows() || endCol >= baseMatrix.getNumCols()) {
            throw new RuntimeException("Invalid matrix view provided!");
        }
        this.baseMatrix = baseMatrix;
        this.ROWS = endRow - startRow + 1;
        this.COLS = endCol - startCol + 1;

        while (!(this.baseMatrix instanceof ConcreteMatrix)) { // perform reduction to shorten call chain -> speedup operations
            startRow += this.baseMatrix.getStartRow();
            startCol += this.baseMatrix.getStartCol();
            this.baseMatrix = this.baseMatrix.getUnderlying();
        }
        this.startRow = startRow;
        this.startCol = startCol;
    }

    @Override
    public Matrix add(Matrix other, Matrix result) {
        if (!this.dimEquals(other)) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                result.updateValue(baseMatrix.getValue(startRow + row, startCol + col) + other.getValue(row, col), row, col);
            }
        }
        return result;
    }

    @Override
    public Matrix sub(Matrix other, Matrix result) {
        if (!this.dimEquals(other)) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                result.updateValue(baseMatrix.getValue(startRow + row, startCol + col) - other.getValue(row, col), row, col);
            }
        }
        return result;
    }

    @Override
    public Matrix mult(Matrix other, Matrix result) {
        if (COLS != other.getNumRows() || result.getNumRows() != ROWS || result.getNumCols() != other.getNumCols()) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < other.getNumCols(); col++) {
                int res = 0;
                for (int k = 0; k < other.getNumRows(); k++) {
                    res += baseMatrix.getValue(startRow + row, startCol + k) * other.getValue(k, col);
                }
                result.updateValue(res, row, col);
            }
        }
        return result;
    }

    @Override
    public void updateMatrix(Matrix matrixToCopy) {
        if (!this.dimEquals(matrixToCopy)) {
            throw new RuntimeException("Matrix to copy must have same dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                baseMatrix.updateValue(matrixToCopy.getValue(row, col), startRow + row, startCol + col);
            }
        }
    }

    @Override
    public int getValue(int rowIndex, int colIndex) {
        return baseMatrix.getValue(startRow + rowIndex, startCol + colIndex);
    }

    @Override
    public void updateValue(int value, int rowIndex, int colIndex) {
        baseMatrix.updateValue(value, startRow + rowIndex, startCol + colIndex);
    }

    @Override
    public boolean dimEquals(Matrix other) {
        return ROWS == other.getNumRows() && COLS == other.getNumCols();
    }

    @Override
    public boolean isSquare() {
        return ROWS == COLS;
    }

    @Override
    public Matrix[] quadrantSplit() {
        if (!isSquare()) {
            throw new RuntimeException("Matrix must be square for quadrant split");
        }
        int split = ROWS / 2;
        Matrix[] matSplit = new Matrix[4];
        matSplit[0] = new MatrixView(this, 0, split - 1, 0, split - 1); // upper left
        matSplit[1] = new MatrixView(this, 0, split - 1 , split, COLS - 1); // upper right
        matSplit[2] = new MatrixView(this, split, ROWS - 1, 0, split - 1); // lower left
        matSplit[3] = new MatrixView(this, split, ROWS - 1, split, COLS - 1); // lower right
        return matSplit;
    }

    @Override
    public Matrix getUnderlying() {
        return baseMatrix;
    }


    @Override
    public int getStartRow() {
        return this.startRow;
    }

    @Override
    public int getStartCol() {
        return this.startCol;
    }

    @Override
    public int getNumRows() {
        return ROWS;
    }

    @Override
    public int getNumCols() {
        return COLS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatrixView that = (MatrixView) o;
        return startRow == that.startRow && startCol == that.startCol && ROWS == that.ROWS && COLS == that.COLS && baseMatrix.equals(that.baseMatrix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseMatrix, startRow, startCol, ROWS, COLS);
    }

    @Override
    public String toString() {
        StringBuilder printStr = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                printStr.append(baseMatrix.getValue(startRow + row, startCol + col)+ " ");
            }
            printStr.append("\n");
        }
        return printStr.toString();
    }
}
