package wooteco.subway.domain.fare;

import java.util.Objects;

public class Fare {
    public static final int DEFAULT_FARE = 1250;

    private final int value;

    public Fare() {
        this(DEFAULT_FARE);
    }

    public Fare(final int value) {
        this.value = value;
    }

    public Fare applyDistancePolicy(final int distance) {
        final DistanceOverFarePolicy distanceOverFarePolicy = DistanceOverFarePolicy.from(distance);
        return new Fare(distanceOverFarePolicy.apply(value, distance));
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
