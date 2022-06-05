package wooteco.subway.domain.fare;

import java.util.Objects;

public class Fare {
    public static final int DEFAULT_FARE = 1250;
    public static final int FIRST_ADDITIONAL_FARE_DISTANCE = 10;
    public static final int SECOND_ADDITIONAL_FARE_DISTANCE = 50;

    private final int value;

    public Fare() {
        this(DEFAULT_FARE);
    }

    public Fare(final int value) {
        this.value = value;
    }

    public Fare applyDistancePolicy(final int distance) {
        if (distance <= FIRST_ADDITIONAL_FARE_DISTANCE) {
            return new Fare(value);
        }
        if (distance <= SECOND_ADDITIONAL_FARE_DISTANCE) {
            final int result = this.value + calculateOverFare(distance - FIRST_ADDITIONAL_FARE_DISTANCE, 5);
            return new Fare(result);
        }

        final int firstResult = calculateOverFare(
            Math.min(distance, SECOND_ADDITIONAL_FARE_DISTANCE) - FIRST_ADDITIONAL_FARE_DISTANCE, 5);
        final int secondResult = calculateOverFare(distance - SECOND_ADDITIONAL_FARE_DISTANCE, 8);
        return new Fare(value + firstResult + secondResult);
    }


    public int calculateOverFare(final int distance, final int unitDistance) {
        return ((distance - 1) / unitDistance + 1) * 100;
    }

    public Fare applyMaxLineExtraFarePolicy(final int maxExtraFare) {
        return new Fare(value + maxExtraFare);
    }

    /*
- [ ] 청소년: 운임에서 350원 공제한 금액의 20% 할인
- [ ] 어린이: 운임에서 350원 공제한 금액의 50% 할인

  ```
  - 청소년: 13세 이상~19세 미만
  - 어린이: 6세 이상~13세 미만
  ```
  */
    public Fare applyAgeDiscountPolicy(final int age) {
        if (6 <= age && age < 13) {
            return new Fare((int) ((value - 350) * (1 - 0.5)));
        }
        if (13 <= age && age < 19) {
            return new Fare((int) ((value - 350) * (1 - 0.2)));
        }
        return new Fare(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Fare fare = (Fare) o;
        return getValue() == fare.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return "Fare{" +
            "value=" + value +
            '}';
    }
}
