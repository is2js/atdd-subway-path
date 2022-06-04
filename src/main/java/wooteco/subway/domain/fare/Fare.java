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

    public int calculate(final int distance) {
        if (distance <= FIRST_ADDITIONAL_FARE_DISTANCE) {
            return value;
        }
        if (distance <= SECOND_ADDITIONAL_FARE_DISTANCE) {
            return value + calculateOverFare(distance - FIRST_ADDITIONAL_FARE_DISTANCE, 5);
        }

        return value
            + calculateOverFare(
            Math.min(distance, SECOND_ADDITIONAL_FARE_DISTANCE) - FIRST_ADDITIONAL_FARE_DISTANCE, 5)
            + calculateOverFare(distance - SECOND_ADDITIONAL_FARE_DISTANCE, 8);
    }

    public int calculateOverFare(final int distance, final int unitDistance) {
        return ((distance - 1) / unitDistance + 1) * 100;
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
