package wooteco.subway.domain.fare;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AgeDiscountPolicyTest {

    @Test
    void from() {
        assertThatThrownBy(() -> AgeDiscountPolicy.from(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 나이는 1~150살 사이만 입력 가능합니다.");
    }

    @ParameterizedTest
    @MethodSource("applyProvider")
    void apply(final int age, final int value, final int discountAmount, final double discountRate) {
        //given
        final Fare expected = new Fare((int) ((value - discountAmount) * (1 - discountRate)));

        //when
        final AgeDiscountPolicy ageDiscountPolicy = AgeDiscountPolicy.from(age);
        final Fare actual = ageDiscountPolicy.apply(value);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    public static Stream<Arguments> applyProvider() {
        return Stream.of(
            Arguments.of(19, 1000, 0, 0),
            Arguments.of(15, 1000, 350, 0.2),
            Arguments.of(7, 1000, 350, 0.5),
            Arguments.of(3, 1000, 0, 0)
        );
    }
}
