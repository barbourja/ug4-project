package fft;

public class Main {

    public static void main(String[] args) {

        FFTStrategy sequential = new Sequential(1000);
        FFTStrategy forkJoin = new ForkJoin(1000, 16, sequential);
        FFTStrategy threaded = new Threaded(1000, 16, sequential);

        // test input sizes
        int[] sizes = new int[14];
        for (int i = 12; i < 26; i++) {
            sizes[i - 12] = (int) Math.pow(2, i);
        }
        TestSuite.varyInputSize(sequential, sizes);
        TestSuite.varyInputSize(forkJoin, sizes);
        TestSuite.varyInputSize(threaded, sizes);
    }
}
