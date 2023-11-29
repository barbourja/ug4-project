package strassens;

public class Sequential implements StrassensStrategy{

    protected final int MIN_MATRIX_SIZE;

    public Sequential(int minMatrixSize) {
        this.MIN_MATRIX_SIZE = minMatrixSize;
    }

    private Matrix strassenMult(Matrix mat1, Matrix mat2, Matrix res) { // assume matrices perfectly square + even
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
            Matrix[] workingQuadrants = new Matrix[5]; // create 4 working quadrants to make the 8 required to run sequentially (4 working + 4 result)
            for (int i = 0; i < workingQuadrants.length; i++) {
                workingQuadrants[i] = new ConcreteMatrix(new int[dimension/2][dimension/2]);
            }

            // computing strassen partials
            // reserve resQuadrants [0,3] and workingQuadrants [0,2] for results
            // using winograd form
            Matrix u = strassenMult(
                    mat1Split[2].sub(mat1Split[0], resQuadrants[0]),
                    mat2Split[1].sub(mat2Split[3], resQuadrants[1]),
                    workingQuadrants[0]
            );
            Matrix v = strassenMult(
                    mat1Split[2].add(mat1Split[3], resQuadrants[2]),
                    mat2Split[1].sub(mat2Split[0], workingQuadrants[3]),
                    workingQuadrants[1]);
            Matrix p1 = strassenMult(mat1Split[0], mat2Split[0], workingQuadrants[2]);
            Matrix p2 = strassenMult(
                    resQuadrants[2].sub(mat1Split[0], resQuadrants[2]),
                    mat2Split[0].add(mat2Split[3], resQuadrants[1])
                            .subInPlace(mat2Split[1]),
                    resQuadrants[3]);
            Matrix p3 = strassenMult(
                    mat1Split[1],
                    mat2Split[2],
                    resQuadrants[0]
            );
            Matrix p4 = strassenMult(
                    mat1Split[0].add(mat1Split[1], resQuadrants[2])
                            .subInPlace(mat1Split[2])
                            .subInPlace(mat1Split[3]),
                    mat2Split[3],
                    resQuadrants[1]
            );
            Matrix p5 = strassenMult(
                    mat1Split[3],
                    mat2Split[2].add(workingQuadrants[3], workingQuadrants[3])
                            .subInPlace(mat2Split[3]),
                    resQuadrants[2]
            );

            // combining partials (winograd)
            Matrix w = p1.add(p2, p2); // (w = p1 + p2), p2 no longer needed -> store directly in p2(resQuadrant[3])

            Matrix res0 = p3.add(p1, p3); // res0 = p3 + p1, p3 no longer needed -> store directly in p3(resQuadrants[0])
            Matrix res1 = p4.add(w, p4).add(v, p4); // res1 = p4 + w + v, p4 no longer needed -> store directly in p4(resQuadrants[1])
            Matrix res2 = p5.add(w, p5).add(u, p5); // res2 = p5 + w + u, p5 no longer needed -> store directly in p5(resQuadrants[2])
            Matrix res3 = w.add(u, w).add(v, w); // res3 = w + u + v, w, u & v no longer needed -> store directly in w(resQuadrants[3])
        }
        return res;
    }

    @Override
    public Matrix execute(Matrix mat1, Matrix mat2, Matrix res) {
        return strassenMult(mat1, mat2, res);
    }
}
