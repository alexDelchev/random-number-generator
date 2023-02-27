# Random number generator

A random number generator which returns values based on
predefined probabilities. Uses float arithmetic and
`java.util.Random` to determine return values.

## Build

`mvn clean package`

## Run

`java -jar ./target/random-number-generator-1.0-SNAPSHOT.jar`

### Example output

```
Values: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
Probabilities: [0.03, 0.08, 0.04, 0.05, 0.5, 0.15, 0.02, 0.05, 0.03, 0.05]
Results with 1000000.0 iterations:
Value 1 with probability 0.030000: 0.029697
Value 2 with probability 0.080000: 0.079832
Value 3 with probability 0.040000: 0.039802
Value 4 with probability 0.050000: 0.049925
Value 5 with probability 0.500000: 0.500936
Value 6 with probability 0.150000: 0.149947
Value 7 with probability 0.020000: 0.020020
Value 8 with probability 0.050000: 0.050245
Value 9 with probability 0.030000: 0.029915
Value 10 with probability 0.050000: 0.049681
```
