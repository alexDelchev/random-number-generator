package org.example.random;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * A {@code RandomNumberGenerator} which returns predefined values
 * based on predefined probabilities.
 *
 * @implNote This class uses {@code float} arithmetic and {@link Random},
 * so precision is only approximate and results from
 * {@link #nextNum()} ultimately rely on the approximate
 * uniform distribution of {@code Random} return values.
 */
public class ProbabilityBasedRandomNumberGenerator implements RandomNumberGenerator {

  private static final float REQUIRED_TOTAL_PROBABILITY = 1.0f;

  private static final float MARGIN = 0.0001f;

  private final Random random = new Random();

  private final int[] values;

  private final float[] probabilities;

  // Holds the distribution between 0.0 and 1.0 of the possible return values
  // based on their respective probabilities. Used to determine the result of
  // #nextNum by generating a random float between 0.0 and 1.0 and checking
  // between which values it is located.
  private final float[] distribution;

  /**
   * Constructs a new instance of {@code ProbabilityBasedRandomNumberGenerator}.
   * Accepts the possible predefined return values and their probabilities as
   * {@code int} and {@code float} arrays respectively. Both arrays must be
   * non-null and non-empty, be of the same length, and each value and its
   * probability should have the same index.
   *
   * @param values {@code int} array containing the possible return values of
   *        {@link RandomNumberGenerator#nextNum()}. Must not be null or empty.
   * @param probabilities {@code float} array containing the respective
   *        probabilities of the {@code values}. Must be the same size as the
   *        values array. Each element represents the probability the
   *        corresponding element of {@code values} will be returned when
   *        {@code RandomNumberGenerator#nextNum()} is called.
   * @throws IllegalArgumentException if parameters are invalid
   */
  public ProbabilityBasedRandomNumberGenerator(int[] values, float[] probabilities) {
    validateInputs(values, probabilities);

    this.values = Arrays.copyOf(values, values.length);
    this.probabilities = Arrays.copyOf(probabilities, probabilities.length);
    this.distribution = plotProbabilities(probabilities);
  }

  private void validateInputs(int[] values, float[] probabilities) {
    if (values == null) {
      throw new IllegalArgumentException("Parameter values cannot be null");
    }

    if (probabilities == null) {
      throw new IllegalArgumentException("Parameter probabilities cannot be null");
    }

    if (values.length != probabilities.length) {
      throw new IllegalArgumentException("Parameters values and probabilities must " +
          "be of the same length");
    }

    if (values.length == 0) {
      throw new IllegalArgumentException("Parameters values and probabilities must " +
          "not be empty");
    }

    float probabilitySum = sum(probabilities);
    if (!isApproximatelyEqual(REQUIRED_TOTAL_PROBABILITY, probabilitySum, MARGIN)) {
      throw new IllegalArgumentException("Parameter probabilities is invalid. " +
          "Contained elements should approximately add up to 1.0, but are " +
          probabilitySum);
    }
  }

  private float sum(float[] data) {
    return sum(data, 0, data.length - 1);
  }

  /*
   * Returns the sum of the elements between the given start (inclusive)
   * and end (inclusive) indices. Corrects the floating point number summation
   * error as much as possible using the Kahan summation algorithm.
   */
  private float sum(float[] data, int startInclusive, int endInclusive) {
    float sum = 0.0f;
    float error = 0.0f;

    for (int i = startInclusive; i <= endInclusive; i++) {
      float value = data[i] - error;
      float currentSum = sum + value;
      error = (currentSum - sum) - value;
      sum = currentSum;
    }

    return sum;
  }

  private boolean isApproximatelyEqual(float expected, float actual, float margin) {
    return Math.abs(expected - actual) <= margin;
  }

  /*
   * Effectively returns a new float array of the same size whose elements
   * represent the sum of the respective preceding elements from the original
   * array.
   */
  private float[] plotProbabilities(float[] probabilities) {
    float[] result = new float[probabilities.length];

    for (int i = 0; i < probabilities.length; i++) {
      result[i] = sum(probabilities, 0, i);
    }

    return result;
  }

  /**
   * Returns a random number whose value is equal to one of the
   * {@code values} used when constructing this instance. If called enough
   * times, the occurrences of the returned values will approximately
   * correspond to the {@code probabilities} used when constructing this
   * instance.
   *
   * @return {@code int} value from the possible predefined {@code values}
   * based on the predefined {@code probabilities}
   */
  @Override
  public int nextNum() {
    // get an approximately uniformly distributed float value
    // between 0.0 and 1.0. Use it to check in which
    // distribution range it is located and return the
    // predefined value with the same index
    float randomNumber = random.nextFloat();

    if (randomNumber <= distribution[0]) {
      return values[0];
    }

    for (int i = 1; i < distribution.length; i++) {
      if (randomNumber > distribution[i - 1] && randomNumber <= distribution[i]) {
        return values[i];
      }
    }

    // Edge case: the generated randomNumber is higher than the
    // highest distribution range, which is smaller than 1 due
    // to summation errors. In this case return the last given
    // possible value
    return values[values.length - 1];
  }

  public int[] getValues() {
    return Arrays.copyOf(values, values.length);
  }

  public float[] getProbabilities() {
    return Arrays.copyOf(probabilities, probabilities.length);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    // Check only values and probabilities - distribution is deterministically
    // derivative from probabilities and random is irrelevant
    ProbabilityBasedRandomNumberGenerator other = (ProbabilityBasedRandomNumberGenerator) o;
    return Arrays.equals(values, other.values)
        && Arrays.equals(probabilities, other.probabilities);
  }

  @Override
  public int hashCode() {
    // Hash only fields used in equals
    return Objects.hash(Arrays.hashCode(values), Arrays.hashCode(probabilities));
  }
}
