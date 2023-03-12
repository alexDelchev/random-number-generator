package org.example;

import org.example.random.ProbabilityBasedRandomNumberGenerator;
import org.example.random.RandomNumberGenerator;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {

  public static void main(String[] args) {
    int[] values = new int[]{
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    };
    System.out.println("Values: " + Arrays.toString(values));

    float[] probabilities = new float[]{
        0.03f, 0.08f, 0.04f, 0.05f, 0.5f,
        0.15f, 0.02f, 0.05f, 0.03f, 0.05f
    };
    System.out.println("Probabilities: " + Arrays.toString(probabilities));

    RandomNumberGenerator generator =
        new ProbabilityBasedRandomNumberGenerator(new Random(), values, probabilities);

    float iterations = 1_000_000;
    AtomicInteger[] hits = atomicIntArray(values.length);

    // note: java.util.Random can be slow when used in parallel
    IntStream.generate(generator::nextNum)
        .parallel()
        .limit((int) iterations)
        .forEach(result -> evaluateResult(values, result, hits));

    System.out.println("Results with " + iterations + " iterations:");
    for (int i = 0; i < values.length; i++) {
      int value = values[i];
      float probability = probabilities[i];
      float rate = hits[i].get() / iterations;

      System.out.printf("Value %d with probability %f: %f%n",
          value, probability, rate);
    }
  }

  private static void evaluateResult(int[] values, int result, AtomicInteger[] hits) {
    for (int i = 0; i < values.length; i++) {
      if (result == values[i]) {
        hits[i].incrementAndGet();
      }
    }
  }

  private static AtomicInteger[] atomicIntArray(int size) {
    AtomicInteger[] result = new AtomicInteger[size];

    for (int i=0; i<size; i++) {
      result[i] = new AtomicInteger();
    }

    return result;
  }
}
