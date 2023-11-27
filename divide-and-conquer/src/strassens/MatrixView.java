package strassens;

public class MatrixView implements Matrix{

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
        this.startRow = startRow;
        this.startCol = startCol;
        this.ROWS = endRow - startRow + 1;
        this.COLS = endCol - startCol + 1;

        while (!(this.baseMatrix.getUnderlying() instanceof ConcreteMatrix)) { // perform reduction to shorten call chain -> speedup operations
            MatrixView view = (MatrixView) this.baseMatrix.getUnderlying();
            this.startRow = this.startRow + view.getStartRow();
            this.startCol = this.startCol + view.getStartCol();
            this.baseMatrix = this.baseMatrix.getUnderlying();
        }
        this.baseMatrix = this.baseMatrix.getUnderlying();
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

    public int getStartRow() {
        return this.startRow;
    }

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
