package strassens;

import javax.swing.text.MaskFormatter;

public class ConcreteMatrix implements Matrix{

    private int[][] matrix;
    private int rows;
    private int cols;

    public ConcreteMatrix(int[][] values) {
        this.matrix = values;
        this.rows = values.length;
        this.cols = values[0].length;
    }

    public void appendRow(int[] values) {
        if (values.length != cols) {
            throw new RuntimeException("Number of values in row must match number of existing columns");
        }
        int[][] matrix = new int[rows + 1][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix[row][col] = this.matrix[row][col];
            }
        }
        for (int i = 0; i < values.length; i++) {
            matrix[rows + 1][i] = values[i];
        }
        this.matrix = matrix;
        this.rows = rows + 1;
    }

    public void appendCol(int[] values) {
        if (values.length != rows) {
            throw new RuntimeException("Number of values in col must match number of existing rows");
        }
        int[][] matrix = new int[rows][cols + 1];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix[row][col] = this.matrix[row][col];
            }
        }
        for (int i = 0; i < values.length; i++) {
            matrix[i][cols + 1] = values[i];
        }
        this.matrix = matrix;
        this.cols = cols + 1;
    }

    @Override
    public void updateMatrix(Matrix matrixToCopy) {
        if (!this.dimEquals(matrixToCopy)) {
            throw new RuntimeException("Matrix to copy must have same dimensions");
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix[row][col] = matrixToCopy.getValue(row, col);
            }
        }
    }

    @Override
    public void updateRow(int[] values, int rowIndex) {
        if (values.length != cols) {
            throw new RuntimeException("Number of values in row must match number of existing columns");
        }

        for (int i = 0; i < values.length; i++) {
            matrix[rowIndex][i] = values[i];
        }
    }

    @Override
    public void updateCol(int[] values, int colIndex) {
        if (values.length != cols) {
            throw new RuntimeException("Number of values in col must match number of existing rows");
        }

        for (int i = 0; i < values.length; i++) {
            matrix[i][colIndex] = values[i];
        }
    }

    @Override
    public int getValue(int rowIndex, int colIndex) {
        return this.matrix[rowIndex][colIndex];
    }

    @Override
    public void updateValue(int value, int rowIndex, int colIndex) {
        if (rowIndex < 0 || colIndex < 0 || rowIndex >= rows || colIndex >= cols ) {
            throw new RuntimeException("Indices out of range");
        }
        this.matrix[rowIndex][colIndex] = value;
    }

    @Override
    public Matrix add(Matrix other, Matrix result) {
        if (!this.dimEquals(other)) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                result.updateValue(matrix[row][col] + other.getValue(row, col), row, col);
            }
        }
        return result;
    }

    @Override
    public Matrix sub(Matrix other, Matrix result) {
        if (!this.dimEquals(other)) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                result.updateValue(matrix[row][col] - other.getValue(row, col), row, col);
            }
        }
        return result;
    }

    @Override
    public Matrix mult(Matrix other, Matrix result) {
        if (cols != other.getNumRows() || result.getNumRows() != rows || result.getNumCols() != other.getNumCols()) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < other.getNumCols(); col++) {
                result.updateValue(0, row, col);
                for (int k = 0; k < other.getNumRows(); k++) {
                    result.updateValue(result.getValue(row, col) + matrix[row][k] * other.getValue(k, col), row, col);
                }
            }
        }
        return result;
    }

    @Override
    public boolean dimEquals(Matrix other) {
        return rows == other.getNumRows() && cols == other.getNumCols();
    }

    @Override
    public boolean isSquare() {
        return rows == cols;
    }

    @Override
    public Matrix[] quadrantSplit() {
        if (!isSquare()) {
            throw new RuntimeException("Matrix must be square for quadrant split");
        }
        int split = rows / 2;
        Matrix[] matSplit = new Matrix[4];
        matSplit[0] = new MatrixView(this, 0, split - 1, 0, split - 1); // upper left
        matSplit[1] = new MatrixView(this, 0, split - 1 , split, cols - 1); // upper right
        matSplit[2] = new MatrixView(this, split, rows - 1, 0, split - 1); // lower left
        matSplit[3] = new MatrixView(this, split, rows - 1, split, cols - 1); // lower right
        return matSplit;
    }

    public void setUnderlyingMatrix(int[][] values) {
        this.matrix = values;
        this.rows = values.length;
        this.cols = values[0].length;
    }

    public int[][] getUnderlyingMatrix() {
        return this.matrix;
    }

    @Override
    public int[] getRow(int rowIndex) {
        int[] row = new int[cols];
        for (int col = 0; col < cols; col++) {
            row[col] = matrix[rowIndex][col];
        }
        return row;
    }

    @Override
    public int[] getCol(int colIndex) {
        int[] col = new int[rows];
        for (int row = 0; row < rows; row++) {
            col[row] = matrix[row][colIndex];
        }
        return col;
    }

    @Override
    public int getNumRows() {
        return rows;
    }

    @Override
    public int getNumCols() {
        return cols;
    }

    @Override
    public String toString() {
        StringBuilder printStr = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                printStr.append(matrix[row][col] + " ");
            }
            printStr.append("\n");
        }
        return printStr.toString();
    }
}
