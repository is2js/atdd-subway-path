package wooteco.subway.domain.fare;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
    void calculate(final int distance, final int expected) {
        //given
        final Fare fare = new Fare();

        //when
        final int actual = fare.applyDistancePolicy(distance)
            .getValue();

        //then
        assertThat(actual).isEqualTo(expected);
    }

}
