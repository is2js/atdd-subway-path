package wooteco.subway.domain.fare;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.IntPredicate;

public enum DistanceOverFarePolicy {
    DEFAULT_FARE_SECTION(distance -> 0 < distance && distance <= 10,
        (value, distance) -> value),
    FIRST_ADDITION_FARE_SECTION(distance -> 10 < distance && distance <= 50,
        DistanceOverFarePolicy::calculateFirstAdditionFare),
    SECOND_ADDITION_FARE_SECTION(distance -> distance >= 50,
        DistanceOverFarePolicy::calculateSectionAdditionFare),
    ;

    private final IntPredicate condition;

    private final BinaryOperator<Integer> overFarePolicy;

    DistanceOverFarePolicy(final IntPredicate condition, final BinaryOperator<Integer> overFarePolicy) {
        this.condition = condition;
        this.overFarePolicy = overFarePolicy;
    }

    static DistanceOverFarePolicy from(final int distance) {
        return Arrays.stream(values())
            .filter(it -> it.condition.test(distance))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] 거리는 0이하는 입력 불가능합니다."));
    }

    public int apply(final int value, final int distance) {
        return overFarePolicy.apply(value, distance);
    }

    private static int calculateFirstAdditionFare(final Integer value, final Integer distance) {
        return value + calculateOverFare(distance - 10, 5);
    }

    public static int calculateOverFare(final int distance, final int unitDistance) {
        return ((distance - 1) / unitDistance + 1) * 100;
    }

    private static int calculateSectionAdditionFare(final int value, final int distance) {
        final int firstResult = calculateOverFare(
            Math.min(distance, 50) - 10, 5);
        final int secondResult = calculateOverFare(distance - 50, 8);
        return value + firstResult + secondResult;
    }
}
