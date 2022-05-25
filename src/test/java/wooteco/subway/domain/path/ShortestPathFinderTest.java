package wooteco.subway.domain.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_2번역_거리_10;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_3번역_거리_22;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_5번역_거리_31;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_2번역_3번역_거리_12;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_3번역_4번역_거리_5;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_4번역_5번역_거리_3;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import wooteco.subway.domain.section.Section;

class ShortestPathFinderTest {

    @Test
    void create() {
        // 2. graph를 만들 재료들을 생성자 주입하여 -> 정리 -> 필드(상태값)으로 가져 상황을 만든다.
        //    - 재료들에 외부라이브러리도 추가하되 -> 바뀔 수  있는 구상체로서, 추상체로 인터페이스도 만들어주면 좋다.
        //    - 여러 라이브러리들 중 하나의 구상체라고 가정 -> if로 하나를 선택하기 보다는 외부 factory를 주입받아 거기서 조달받아 1개로 받기
        final List<Long> stationIds = List.of(1L, 2L, 3L);

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_1번역_3번역_거리_22
        );

        // when & then (수행)
        assertDoesNotThrow(() -> ShortestPathFinder.of(stationIds, sections));
    }

    @Test
    void create_fail_invalid_stations() {
        // 2. graph를 만들 재료들을 생성자 주입하여 -> 정리 -> 필드(상태값)으로 가져 상황을 만든다.
        //    - 재료들에 외부라이브러리도 추가하되 -> 바뀔 수  있는 구상체로서, 추상체로 인터페이스도 만들어주면 좋다.
        //    - 여러 라이브러리들 중 하나의 구상체라고 가정 -> if로 하나를 선택하기 보다는 외부 factory를 주입받아 거기서 조달받아 1개로 받기
        final List<Long> stationIds = List.of(1L);

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_1번역_3번역_거리_22
        );

        // then(수행)
        assertThatThrownBy(() -> ShortestPathFinder.of(stationIds, sections))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 지하철역이 부족하여 경로를 만들 수 없습니다.");
    }

    @Test
    void create_fail_invalid_sections() {
        // 2. graph를 만들 재료들을 생성자 주입하여 -> 정리 -> 필드(상태값)으로 가져 상황을 만든다.
        //    - 재료들에 외부라이브러리도 추가하되 -> 바뀔 수  있는 구상체로서, 추상체로 인터페이스도 만들어주면 좋다.
        //    - 여러 라이브러리들 중 하나의 구상체라고 가정 -> if로 하나를 선택하기 보다는 외부 factory를 주입받아 거기서 조달받아 1개로 받기
        final List<Long> stationIds = List.of(1L, 2L, 3L);

        final List<Section> sections = List.of();

        // when & then (수행)
        assertThatThrownBy(() -> ShortestPathFinder.of(stationIds, sections))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 구간이 비어서 경로를 만들 수 없습니다.");
    }

    @DisplayName("1번역과 5번역 사이 최단거리를 계산하면, 최단 경로와 최단거리를 구한다.")
    @Test
    void find() {
        //given
        final List<Long> stationIds = List.of(1L, 2L, 3L, 4L, 5L);

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3,
            일호선_구간_1번역_5번역_거리_31
        );

        final Long source = 1L;
        final Long target = 5L;

        // when
        final ShortestPathFinder shortestPathFinder = ShortestPathFinder.of(stationIds, sections);
        final Path actual = shortestPathFinder.find(source, target);

        // 예상
        final Path expected = new Path(List.of(1L, 2L, 3L, 4L, 5L), 30);

        // 검증
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }

    @DisplayName("인접한 역끼리 최단거리를 계산하면, 최단 경로와 최단거리를 구한다.")
    @ParameterizedTest
    @CsvSource(value = {"1:2:10", "2:3:12"}, delimiter = ':')
    void find_adjacent_stations(final Long source, final Long target, final int expectedDistance) {
        //given
        final List<Long> stationIds = List.of(1L, 2L, 3L, 4L, 5L);

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3,
            일호선_구간_1번역_5번역_거리_31
        );

        // when
        final ShortestPathFinder shortestPathFinder = ShortestPathFinder.of(stationIds, sections);
        final Path actual = shortestPathFinder.find(source, target);

        // 예상
        final Path expected = new Path(List.of(source, target), expectedDistance);

        // 검증
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }

    @DisplayName("떨어진 역도 최단거리를 계산하면, 최단 경로와 최단거리를 구한다.")
    @ParameterizedTest
    @MethodSource
    void find_remote_stations(final Long source, final Long target,
                              final List<Long> expectedStationIds, final int expectedDistance) {
        final List<Long> stationIds = List.of(1L, 2L, 3L, 4L, 5L);

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3,
            일호선_구간_1번역_5번역_거리_31
        );

        // when
        final ShortestPathFinder shortestPathFinder = ShortestPathFinder.of(stationIds, sections);
        final Path actual = shortestPathFinder.find(source, target);

        // 예상
        final Path expected = new Path(expectedStationIds, expectedDistance);

        // 검증
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }

    public static Stream<Arguments> find_remote_stations() {
        return Stream.of(
            Arguments.of(1L, 3L, List.of(1L, 2L, 3L), 22)
        );
    }

    @DisplayName("최단거리를 구할 때, 같은 역을 입력하면 예외를 발생시킨다.")
    @Test
    void find_invalid() {
        //given
        final List<Long> stationIds = List.of(1L, 2L, 3L, 4L, 5L);

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3,
            일호선_구간_1번역_5번역_거리_31
        );

        final Long source = 1L;
        final Long target = 1L;

        final ShortestPathFinder shortestPathFinder = ShortestPathFinder.of(stationIds, sections);

        // when
        assertThatThrownBy(() -> shortestPathFinder.find(source, target))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 경로를 찾으려면 같은 역을 입력할 수 없습니다.");
    }
}
