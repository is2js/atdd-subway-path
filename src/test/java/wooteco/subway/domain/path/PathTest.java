package wooteco.subway.domain.path;

import static wooteco.subway.testutils.SubWayFixtures.삼번_잠실역;
import static wooteco.subway.testutils.SubWayFixtures.이번_선릉역;
import static wooteco.subway.testutils.SubWayFixtures.일번_강남역;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.domain.fare.Fare;

class PathTest {

    @DisplayName("given)최단거리를 제공하면 거리에 따른 요금을 계산하여 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"9,1250", "10,1250", "11,1350", "15,1350", "16, 1450", "50, 2050", "51,2150"})
    void calculateFare(final int distance, final int expected) {

        final Path path = new Path(new Fare(), List.of(일번_강남역, 이번_선릉역, 삼번_잠실역), distance, 0, 10);
        final int actual = path.calculateFare();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("given)최단거리를 제공하면 거리에 따른 요금에 노선별 추가요금의 최대값을 제공하면 계산하여 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"9, 0, 1250", "10, 900, 2150", "11, 1000, 2350"})
    void calculateFare(final int distance, final int maxExtraFare, final int expected) {

        final Path path = new Path(new Fare(), List.of(일번_강남역, 이번_선릉역, 삼번_잠실역), distance, maxExtraFare, 10);
        final int actual = path.calculateFare();

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
