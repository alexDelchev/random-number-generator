package org.example.random;


import org.junit.jupiter.api.Test;

class ProbabilityBasedRandomNumberGeneratorTest {

  @Test
  void nextNumShouldRespectProbabilities() {
    // GIVEN
    int iterations = 10_000;
    int[] values = new int[] {1, 2, 3, 4, 5};
    float[] probabilities = new float[] {0.1f, 0.2f, 0.4f, 0.2f, 0.1f};
    RandomNumberGenerator generator =
        new ProbabilityBasedRandomNumberGenerator(values, probabilities);

    // WHEN
    // generate random numbers and count how many times
    // each value has occurred
    int[] hits = new int[values.length];
    for (int i=0; i < iterations; i++) {
      int randomNumber = generator.nextNum();

      for (int j=0; j<values.length; j++) {
        if (randomNumber == values[j]) {
          hits[j] ++;
        }
      }
    }

    // THEN
    // check if values are approximately distributed
    // according to the probabilities
    float margin = 0.01f;
    for (int i=0; i< probabilities.length; i++) {
      float rate = hits[i] / (float) iterations;
      float expected = probabilities[i];

      assert Math.abs(rate - expected) <= margin :
          "Expected actual probability of " + values[i] +
              " to be " + expected + " with a margin of " + margin +
              ", but was " + rate;
    }
  }

  @Test
  void constructorNullArguments() {
    // GIVEN
    int[] values = new int[1];
    float[] probabilities = new float[1];

    // THEN
    assertThrown(() -> new ProbabilityBasedRandomNumberGenerator(values, null),
        IllegalArgumentException.class);
    assertThrown(() -> new ProbabilityBasedRandomNumberGenerator(null, probabilities),
        IllegalArgumentException.class);
    assertThrown(() -> new ProbabilityBasedRandomNumberGenerator(null, null),
        IllegalArgumentException.class);
  }

  @Test
  void constructorEmptyArguments() {
    // GIVEN
    int[] values = new int[1];
    float[] probabilities = new float[1];

    // THEN
    assertThrown(() -> new ProbabilityBasedRandomNumberGenerator(values, new float[0]),
        IllegalArgumentException.class);
    assertThrown(() -> new ProbabilityBasedRandomNumberGenerator(new int[0], probabilities),
        IllegalArgumentException.class);
    assertThrown(() -> new ProbabilityBasedRandomNumberGenerator(new int[0], new float[0]),
        IllegalArgumentException.class);
  }

  @Test
  void constructorDifferentLengthArguments() {
    // GIVEN
    int[] values = new int[2];
    float[] probabilities = new float[1];

    // THEN
    assertThrown(() -> new ProbabilityBasedRandomNumberGenerator(values, probabilities),
        IllegalArgumentException.class);
  }

  @Test
  void constructorInvalidProbabilities() {
    // GIVEN
    int[] values = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    // 10 x 0.01 = 1
    float[] correctProbabilities =
        new float[] {0.1f, 0.1f, 0.1f, 0.1f, 0.1f,
            0.1f, 0.1f, 0.1f, 0.1f, 0.1f};

    // 9 x 0.01 + 0.02 = 1.1
    float[] incorrectProbabilities =
        new float[] {0.1f, 0.1f, 0.1f, 0.1f, 0.1f,
            0.1f, 0.1f, 0.1f, 0.1f, 0.2f};

    // THEN
    assertNotThrown(() ->
        new ProbabilityBasedRandomNumberGenerator(values, correctProbabilities));
    assertThrown(() ->
        new ProbabilityBasedRandomNumberGenerator(values, incorrectProbabilities),
        IllegalArgumentException.class);
  }

  private void assertThrown(Runnable runnable,
                            Class<? extends Throwable> expectedException) {
    Throwable ex = null;

    try {
      runnable.run();
    } catch(Exception e) {
      ex = e;
    }

    assert ex != null : "Expected exception of type " +
        expectedException.getSimpleName() + ", but was not thrown";
    assert ex.getClass().equals(expectedException) :
        "Expected exception of type " + expectedException.getSimpleName() +
            ", but got " + ex.getClass().getSimpleName();

  }

  private void assertNotThrown(Runnable runnable) {
    Throwable ex = null;

    try {
      runnable.run();
    } catch (Exception e) {
      ex = e;
    }

    assert ex == null : "Expected no exception, but got " +
        ex.getClass().getSimpleName();
  }
}