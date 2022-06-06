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

    public Fare applyAgeDiscountPolicy(final int age) {
        final AgeDiscountPolicy ageDiscountPolicy = AgeDiscountPolicy.from(age);
        return new Fare(ageDiscountPolicy.apply(value));
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
