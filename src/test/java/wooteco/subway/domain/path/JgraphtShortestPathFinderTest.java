package wooteco.subway.domain.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.testutils.SubWayFixtures.사번_사당역;
import static wooteco.subway.testutils.SubWayFixtures.삼번_잠실역;
import static wooteco.subway.testutils.SubWayFixtures.오번_신림역;
import static wooteco.subway.testutils.SubWayFixtures.이번_선릉역;
import static wooteco.subway.testutils.SubWayFixtures.일번_강남역;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1_2_3;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_2번역_거리_10;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_2번역_3번역_거리_12;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_3번역_4번역_거리_5;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_4번역_5번역_거리_3;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.fare.Fare;
import wooteco.subway.domain.section.Sections;

class JgraphtShortestPathFinderTest {

    @Test
    void create() {
        assertDoesNotThrow(() -> JgraphtShortestPathFinder.of(일호선_구간_1_2_3));
    }

    @Test
    void create_fail_invalid_sections() {
        assertThatThrownBy(() -> JgraphtShortestPathFinder.of(new Sections(Collections.emptyList())))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 지하철역이 부족하여 경로를 만들 수 없습니다.");
    }

    @DisplayName("1번역과 5번역 사이 최단거리를 계산하면, 최단 경로와 최단거리를 구한다.")
    @Test
    void find() {
        //given
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3
        ));

        // when
        final ShortestPathFinder shortestPathFinder = JgraphtShortestPathFinder.of(sections);
        final Path actual = shortestPathFinder.find(일번_강남역, 오번_신림역);

        // 예상
        final Path expected = new Path(List.of(일번_강남역, 이번_선릉역, 삼번_잠실역, 사번_사당역, 오번_신림역), 30, new Fare());

        // 검증
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }

    public static Stream<Arguments> find_adjacent_stations() {
        return Stream.of(
            Arguments.of(일번_강남역, 이번_선릉역, 10),
            Arguments.of(이번_선릉역, 삼번_잠실역, 12)
        );
    }

    @DisplayName("인접한 역끼리 최단거리를 계산하면, 최단 경로와 최단거리를 구한다.")
    @ParameterizedTest
    @MethodSource
    void find_adjacent_stations(final Station source, final Station target, final int expectedDistance) {
        //given
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3
        ));

        // when
        final ShortestPathFinder shortestPathFinder = JgraphtShortestPathFinder.of(sections);
        final Path actual = shortestPathFinder.find(source, target);

        // 예상
        final Path expected = new Path(List.of(source, target), expectedDistance, new Fare());

        // 검증
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }

    @DisplayName("떨어진 역도 최단거리를 계산하면, 최단 경로와 최단거리를 구한다.")
    @ParameterizedTest
    @MethodSource
    void find_remote_stations(final Station source, final Station target,
                              final List<Station> expectedStations, final int expectedDistance) {
        //given
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3
        ));

        // when
        final ShortestPathFinder shortestPathFinder = JgraphtShortestPathFinder.of(sections);
        final Path actual = shortestPathFinder.find(source, target);

        // 예상
        final Path expected = new Path(expectedStations, expectedDistance, new Fare());

        // 검증
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }

    public static Stream<Arguments> find_remote_stations() {
        return Stream.of(
            Arguments.of(일번_강남역, 삼번_잠실역, List.of(일번_강남역, 이번_선릉역, 삼번_잠실역), 22)
        );
    }

    @DisplayName("최단거리를 구할 때, 같은 역을 입력하면 예외를 발생시킨다.")
    @Test
    void find_invalid() {
        //given
        //given
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3
        ));
        final Station source = 일번_강남역;
        final Station target = 일번_강남역;

        final ShortestPathFinder shortestPathFinder = JgraphtShortestPathFinder.of(sections);

        // when
        assertThatThrownBy(() -> shortestPathFinder.find(source, target))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 경로를 찾으려면 같은 역을 입력할 수 없습니다.");
    }
}
