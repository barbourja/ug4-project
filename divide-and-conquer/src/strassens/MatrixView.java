package strassens;

public class MatrixView implements Matrix{

    private Matrix baseMatrix;
    private int startRow;
    private int endRow;
    private int startCol;
    private int endCol;
    private int rows;
    private int cols;

    public MatrixView(Matrix baseMatrix, int startRow, int endRow, int startCol, int endCol) {
        if (startRow < 0 || startCol < 0 || endRow >= baseMatrix.getNumRows() || endCol >= baseMatrix.getNumCols()) {
            throw new RuntimeException("Invalid matrix view provided!");
        }
        this.baseMatrix = baseMatrix;
        this.startRow = startRow;
        this.startCol = startCol;
        this.rows = endRow - startRow + 1;
        this.cols = endCol - startCol + 1;
    }

    @Override
    public Matrix add(Matrix other, Matrix result) {
        if (!this.dimEquals(other)) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
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
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                result.updateValue(baseMatrix.getValue(startRow + row, startCol + col) - other.getValue(row, col), row, col);
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
                    result.updateValue(result.getValue(row, col) + baseMatrix.getValue(startRow + row, startCol + k) * other.getValue(k, col), row, col);
                }
            }
        }
        return result;
    }

    @Override
    public void updateMatrix(Matrix matrixToCopy) {
        if (!this.dimEquals(matrixToCopy)) {
            throw new RuntimeException("Matrix to copy must have same dimensions");
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                baseMatrix.updateValue(matrixToCopy.getValue(row, col), startRow + row, startCol + col);
            }
        }
    }

    @Override
    public void updateRow(int[] values, int rowIndex) {
        if (values.length != cols) {
            throw new RuntimeException("Number of values in row must match number of existing columns");
        }
        for (int i = 0; i < values.length; i++) {
            baseMatrix.updateValue(values[i],startRow + rowIndex, startCol + i);
        }
    }

    @Override
    public void updateCol(int[] values, int colIndex) {
        if (values.length != cols) {
            throw new RuntimeException("Number of values in col must match number of existing rows");
        }
        for (int i = 0; i < values.length; i++) {
            baseMatrix.updateValue(values[i],startRow + i, startCol + colIndex);
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

    @Override
    public int[] getRow(int rowIndex) {
        int[] row = new int[cols];
        for (int col = 0; col < cols; col++) {
            row[col] = baseMatrix.getValue(startRow + rowIndex, startCol + col);
        }
        return row;
    }

    @Override
    public int[] getCol(int colIndex) {
        int[] col = new int[rows];
        for (int row = 0; row < rows; row++) {
            col[row] = baseMatrix.getValue(startRow + row, startCol + colIndex);
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
                printStr.append(baseMatrix.getValue(startRow + row, startCol + col)+ " ");
            }
            printStr.append("\n");
        }
        return printStr.toString();
    }
}
