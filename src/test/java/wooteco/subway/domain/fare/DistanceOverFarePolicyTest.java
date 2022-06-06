package wooteco.subway.domain.fare;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DistanceOverFarePolicyTest {

    public static Stream<Arguments> fromProvider() {
        return Stream.of(
            Arguments.of(1000, 5, 1000),
            Arguments.of(1000, 15, 1100),
            Arguments.of(1000, 55, 1900)
        );
    }

    @ParameterizedTest
    @MethodSource("fromProvider")
    void from(final int fare, final int distance, final int expected) {

        final DistanceOverFarePolicy distanceOverFarePolicy = DistanceOverFarePolicy.from(distance);
        final int actual = distanceOverFarePolicy.apply(fare, distance);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
