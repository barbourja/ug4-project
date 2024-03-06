package fft;

import java.util.Objects;

public class Complex {
    private double real;
    private double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Complex add(Complex other) {
        return new Complex(real + other.real(), imaginary + other.imaginary());
    }

    public Complex sub(Complex other) {
        return new Complex(real - other.real(), imaginary - other.imaginary());
    }

    public Complex mult(Complex other) {
        return new Complex(real * other.real() - imaginary * other.imaginary(), real * other.imaginary() + imaginary * other.real());
    }

    public Complex mutateFusedMultAdd(Complex other1, Complex other2) {
        double realMult = other1.real() * other2.real() - other1.real() * other2.imaginary();
        double imaginaryMult = other1.real() * other2.imaginary() + other1.imaginary() * other2.real();
        this.real = real + realMult;
        this.imaginary = imaginary + imaginaryMult;
        return this;
    }

    public Complex pow(double n) {
        return new Complex(Math.pow(this.r(), n) * Math.cos(n * this.theta()), Math.pow(this.r(), n) * Math.sin(n * this.theta()));
    }

    public double real() {
        return this.real;
    }

    public double imaginary() {
        return this.imaginary;
    }

    public double r() {
        return Math.hypot(real, imaginary);
    }

    public double theta() {
        return Math.atan2(imaginary, real);
    }

    public Complex clone() {
        return new Complex(real, imaginary);
    }

    public String toString() {
        if (this.imaginary == 0) {
            return Double.toString(this.real);
        }
        else if (this.real == 0) {
            return Double.toString(this.imaginary);
        }
        else {
            return this.real + (this.imaginary > 0 ? " + " : " - ") + Math.abs(this.imaginary) + "i";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complex complex = (Complex) o;
        return Double.compare(complex.real, real) == 0 && Double.compare(complex.imaginary, imaginary) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
    }
}
