package fft;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Random rand = new Random();
        int n = (int) Math.pow(2, 18);
        Complex[] f = new Complex[n];
        for (int i = 0; i < n; i++) {
            f[i] = new Complex(rand.nextDouble(), rand.nextDouble());
        }
        int minSequenceSize = n/1000;

        FFTStrategy sequential = new Sequential(minSequenceSize/4);
        FFTStrategy forkJoin = new ForkJoin(minSequenceSize, 16, sequential);
        FFTStrategy threaded = new Threaded(minSequenceSize, 16, sequential);

//        System.out.println("Executing sequential...");
//        long startTime = System.nanoTime();
//        Complex[] seqResult = sequential.execute(f);
//        long timeTaken = System.nanoTime() - startTime;
//        System.out.println((timeTaken/1000000) + " ms");

        System.out.println("Executing fork/join...");
        long startTime = System.nanoTime();
        forkJoin.execute(f);
        long timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken/1000000) + " ms");

        System.out.println("Executing threaded...");
        startTime = System.nanoTime();
        threaded.execute(f);
        timeTaken = System.nanoTime() - startTime;
        System.out.println((timeTaken/1000000) + " ms");

//        if (!Arrays.equals(seqResult, forkJoinResult)) {
//            throw new RuntimeException("INCONGRUENT RESULTS!");
//        }
    }
}
