package wooteco.subway.domain.path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.testutils.Fixture.삼번_잠실역;
import static wooteco.subway.testutils.Fixture.이번역_선릉;
import static wooteco.subway.testutils.Fixture.일번역_강남;
import static wooteco.subway.testutils.Fixture.일호선_구간_1번역_2번역_거리_10;
import static wooteco.subway.testutils.Fixture.일호선_구간_1번역_3번역_거리_22;
import static wooteco.subway.testutils.Fixture.일호선_구간_2번역_3번역_거리_12;

import java.util.List;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;

class ShortestPathFinderTest {

    @Test
    void create() {
        // 2. graph를 만들 재료들을 생성자 주입하여 -> 정리 -> 필드(상태값)으로 가져 상황을 만든다.
        //    - 재료들에 외부라이브러리도 추가하되 -> 바뀔 수  있는 구상체로서, 추상체로 인터페이스도 만들어주면 좋다.
        //    - 여러 라이브러리들 중 하나의 구상체라고 가정 -> if로 하나를 선택하기 보다는 외부 factory를 주입받아 거기서 조달받아 1개로 받기

        final List<Station> stations = List.of(
            일번역_강남,
            이번역_선릉,
            삼번_잠실역
        );

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_1번역_3번역_거리_22
        );

        // then(수행)
        assertDoesNotThrow(() -> ShortestPathFinder.of(stations, sections));
    }

    @Test
    void create_fail_invalid_stations() {
        // 2. graph를 만들 재료들을 생성자 주입하여 -> 정리 -> 필드(상태값)으로 가져 상황을 만든다.
        //    - 재료들에 외부라이브러리도 추가하되 -> 바뀔 수  있는 구상체로서, 추상체로 인터페이스도 만들어주면 좋다.
        //    - 여러 라이브러리들 중 하나의 구상체라고 가정 -> if로 하나를 선택하기 보다는 외부 factory를 주입받아 거기서 조달받아 1개로 받기

        final List<Station> stations = List.of(
            일번역_강남
        );

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_1번역_3번역_거리_22
        );

        // then(수행)
        assertThatThrownBy(() -> ShortestPathFinder.of(stations, sections))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 지하철역이 부족하여 경로를 만들 수 없습니다.");
    }

    @Test
    void create_fail_invalid_sections() {
        // 2. graph를 만들 재료들을 생성자 주입하여 -> 정리 -> 필드(상태값)으로 가져 상황을 만든다.
        //    - 재료들에 외부라이브러리도 추가하되 -> 바뀔 수  있는 구상체로서, 추상체로 인터페이스도 만들어주면 좋다.
        //    - 여러 라이브러리들 중 하나의 구상체라고 가정 -> if로 하나를 선택하기 보다는 외부 factory를 주입받아 거기서 조달받아 1개로 받기

        final List<Station> stations = List.of(
            일번역_강남,
            이번역_선릉,
            삼번_잠실역
        );

        final List<Section> sections = List.of();

        // then(수행)
        assertThatThrownBy(() -> ShortestPathFinder.of(stations, sections))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 구간이 비어서 경로를 만들 수 없습니다.");
    }

}
