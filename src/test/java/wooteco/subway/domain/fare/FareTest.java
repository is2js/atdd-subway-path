package wooteco.subway.domain.fare;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
class FareTest {

    @Test
    void create_with_initValue() {
        //given
        final Fare fare = new Fare();
        final int expected = 1250;

        //when
        final int actual = fare.getValue();

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"9, 1250", "10, 1250", "11,1350", "15,1350", "16, 1450", "50, 2050", "51,2150"})
    void applyDistancePolicy(final int distance, final int expected) {
        //given
        final Fare fare = new Fare();

        //when
        final int actual = fare.applyDistancePolicy(distance)
            .getValue();

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"900, 2150", "1000, 2250"})
    void applyMaxLineExtraFarePolicy(final int maxExtraFare, final int expected) {
        //given
        final Fare fare = new Fare();

        //when
        final int actual = fare.applyMaxLineExtraFarePolicy(maxExtraFare)
            .getValue();

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("applyAgeDiscountPolicy_methodSource")
    void applyAgeDiscountPolicy(final int age, final Fare expected) {
        final Fare fare = new Fare();

        final Fare actual = fare.applyAgeDiscountPolicy(age);

        assertThat(actual).isEqualTo(expected);
    }

    public Stream<Arguments> applyAgeDiscountPolicy_methodSource() {
        return Stream.of(
            Arguments.of(5, new Fare(1250)),
            Arguments.of(6, new Fare((int) ((1250 - 350) * (1 - 0.5)))),
            Arguments.of(12, new Fare((int) ((1250 - 350) * (1 - 0.5)))),
            Arguments.of(13, new Fare((int) ((1250 - 350) * (1 - 0.2)))),
            Arguments.of(18, new Fare((int) ((1250 - 350) * (1 - 0.2)))),
            Arguments.of(20, new Fare(1250))
        );
    }
}
