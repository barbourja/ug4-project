package fft;

import java.util.Random;

public class TestSuite {

    public static void varyInputSize(FFTStrategy strategyUnderTest, int[] valuesToTest) {
        System.out.println("\n" + strategyUnderTest.toString(false, false));
        long startTime, elapsedTime;
        Random rand = new Random();
        for (int inputSize : valuesToTest) {
            System.out.print("  Input Size: " + inputSize);
            Complex[] input = new Complex[inputSize];
            for (int i = 0; i < inputSize; i++) {
                input[i] = new Complex(rand.nextDouble(), rand.nextDouble());
            }
            startTime = System.nanoTime();
            strategyUnderTest.execute(input);
            elapsedTime = (System.nanoTime() - startTime) / 1000000; // in ms
            System.out.println(" | " + elapsedTime + "ms");
        }
    }

    public static void varyMinSize(FFTStrategy strategyUnderTest, int[] valuesToTest, Complex[] input) {
        System.out.println("\n" + strategyUnderTest.toString(false, true));
        long startTime, elapsedTime;
        for (int minInputSize : valuesToTest) {
            System.out.print("  Minimum Input Size: " + minInputSize);
            strategyUnderTest.setMinSize(minInputSize);
            startTime = System.nanoTime();
            strategyUnderTest.execute(input);
            elapsedTime = (System.nanoTime() - startTime) / 1000000; // in ms
            System.out.println(" | " + elapsedTime + "ms");
        }
    }

    public static void varyParallelism(FFTStrategy strategyUnderTest, int[] valuesToTest, Complex[] input) {
        System.out.println("\n" + strategyUnderTest.toString(true, false));
        long startTime, elapsedTime;
        for (int parallelism : valuesToTest) {
            System.out.print("  Parallelism: " + parallelism);
            strategyUnderTest.setParallelism(parallelism);
            startTime = System.nanoTime();
            strategyUnderTest.execute(input);
            elapsedTime = (System.nanoTime() - startTime) / 1000000; // in ms
            System.out.println(" | " + elapsedTime + "ms");
        }
    }
}
