package fft;

public class Main {

    public static void main(String[] args) {
        FFTStrategy strat = new Sequential(2);

        int n = 4;
        Complex[] f = new Complex[n];
        f[0] = new Complex(-0.03480425839330703, 0);
        f[1] = new Complex(0.07910192950176387, 0);
        f[2] = new Complex(0.7233322451735928, 0);
        f[3] = new Complex(0.1659819820667019, 0);
        Complex[] F = strat.execute(f);
        for (int i = 0; i < F.length; i++) {
            System.out.println(F[i].toString());
        }

    }
}
