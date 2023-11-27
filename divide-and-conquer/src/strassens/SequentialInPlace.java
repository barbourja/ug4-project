package strassens;

public class SequentialInPlace {

    protected final int MIN_MATRIX_SIZE;

    public SequentialInPlace(int minMatrixSize) {
        this.MIN_MATRIX_SIZE = minMatrixSize;
    }

    public void execute(Matrix mat1, Matrix mat2, Matrix working, Matrix res) { // assume matrices perfectly square + even
        if (!mat1.isSquare() || !mat2.isSquare() || !mat1.dimEquals(mat2)) {
            throw new RuntimeException("Square/equal assumption doesn't hold!");
        }
        int dimension = mat1.getNumRows();
        if (dimension <= MIN_MATRIX_SIZE) { // base case, directly multiply and store in result
            mat1.mult(mat2, res);
        }
        else { // perform strassen algorithm
            // splitting input matrices
            Matrix[] mat1Split = mat1.quadrantSplit();
            Matrix[] mat2Split = mat2.quadrantSplit();

            Matrix[] resQuadrants = res.quadrantSplit();
            Matrix[] workingQuadrants = working.quadrantSplit();
            Matrix tempQuadrant1 = new ConcreteMatrix(new int[mat1.getNumRows()/2][mat1.getNumRows()/2]);
            Matrix tempQuadrant2 = new ConcreteMatrix(new int[mat1.getNumRows()/2][mat1.getNumRows()/2]);

            // computing strassen partials
            execute(mat1Split[0], mat2Split[1].sub(mat2Split[3], workingQuadrants[0]), workingQuadrants[1], resQuadrants[0]); // p1
            execute(mat1Split[0].add(mat1Split[1], workingQuadrants[0]), mat2Split[3], workingQuadrants[1], resQuadrants[1]); // p2
            execute(mat1Split[2].add(mat1Split[3], workingQuadrants[0]), mat2Split[0], workingQuadrants[1], resQuadrants[2]); // p3
            execute(mat1Split[3], mat2Split[2].sub(mat2Split[0], workingQuadrants[0]), workingQuadrants[1], resQuadrants[3]); // p4
            execute(mat1Split[0].add(mat1Split[3], workingQuadrants[1]), mat2Split[0].add(mat2Split[3], workingQuadrants[2]), workingQuadrants[3], workingQuadrants[0]); // p5
            execute(mat1Split[1].sub(mat1Split[3], workingQuadrants[2]), mat2Split[2].add(mat2Split[3], workingQuadrants[3]), tempQuadrant1, workingQuadrants[1]); // p6
            execute(mat1Split[0].sub(mat1Split[2], workingQuadrants[3]), mat2Split[0].add(mat2Split[1], tempQuadrant1), tempQuadrant2, workingQuadrants[2]); // p7

            // combining partials to give result (4 matrix additions -> result)
            workingQuadrants[0].add(resQuadrants[3], workingQuadrants[3])
                    .sub(resQuadrants[1], workingQuadrants[3])
                    .add(workingQuadrants[1], workingQuadrants[3]); // result for resQuadrant[0] stored in workingQuadrants[3]
            resQuadrants[0].add(resQuadrants[1], tempQuadrant1); // result for resQuadrant[1] stored in tempQuadrant
            resQuadrants[1].updateMatrix(tempQuadrant1); // store result for resQuadrant[1], lose p2
            resQuadrants[0].add(workingQuadrants[0], tempQuadrant1)
                    .sub(resQuadrants[2], tempQuadrant1)
                    .sub(workingQuadrants[2], tempQuadrant1); // result for resQuadrant[3] stored in tempQuadrant
            resQuadrants[0].updateMatrix(workingQuadrants[3]); // store result for resQuadrant[0], lose p1
            resQuadrants[3].updateMatrix(tempQuadrant1); // store result for resQuadrant[3], lose p4
            resQuadrants[2].add(resQuadrants[3], resQuadrants[2]); // compute resQuadrant[2] result directly
        }
    }
}
