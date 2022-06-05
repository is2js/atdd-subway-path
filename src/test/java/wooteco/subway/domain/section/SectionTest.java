package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.testutils.SubWayFixtures.이번_선릉역;
import static wooteco.subway.testutils.SubWayFixtures.일번_강남역;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_2번역_거리_10;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_3번역_거리_22;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_2번역_3번역_거리_12;
import static wooteco.subway.testutils.SubWayFixtures.일호선_파랑;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("구간을 생성한다.")
    @Test
    void create_success() {
        //given & when
        final int distance = 1;

        //then
        assertDoesNotThrow(() -> new Section(일호선_파랑, 일번_강남역, 이번_선릉역, distance));
    }

    @DisplayName("구간 생성시, 상행 종점과 하행 종점이 같으면 예외를 발생한다.")
    @Test
    void create_fail_same_station() {
        //given & when
        final int distance = 1;

        //then
        assertThatThrownBy(() -> new Section(일호선_파랑, 일번_강남역, 일번_강남역, distance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 상행 종점과 하행 종점이 같을 수 없습니다.");
    }

    @DisplayName("구간 생성시, 부적절한 거리가 입력되면 예외를 발생한다.")
    @Test
    void create_fail_invalid_distance() {
        //given & when
        final int distance = 0;

        //then
        assertThatThrownBy(() -> new Section(일호선_파랑, 일번_강남역, 이번_선릉역, distance))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 부적절한 거리가 입력되었습니다. 0보다 큰 거리를 입력해주세요.");
    }

    @DisplayName("기존 구간에 대해 중간역을 포함한 추가 구간(up to middle)을 통해 새로운 구간(middle to down)을 생성한다.")
    @Test
    void createMiddleToDownSection() {
        //given & when
        final Section middleToDownSection = 일호선_구간_1번역_2번역_거리_10.createMiddleToDownSection(
            일호선_구간_1번역_3번역_거리_22);

        //then
        assertAll(
            () -> assertThat(middleToDownSection.getUpStation()).isEqualTo(
                일호선_구간_1번역_2번역_거리_10.getDownStation()),
            () -> assertThat(middleToDownSection.getDownStation()).isEqualTo(
                일호선_구간_1번역_3번역_거리_22.getDownStation())
        );
    }

    @DisplayName("기존 구간에 대해 중간역을 포함한 추가 구간(middle to down)을 통해 새로운 구간(up to middle)을 생성한다.")
    @Test
    void createUpToMiddleSection() {
        //given & when
        final Section upToMiddleSection = 일호선_구간_2번역_3번역_거리_12.createUpToMiddleSection(
            일호선_구간_1번역_3번역_거리_22);

        //then
        assertAll(
            () -> assertThat(upToMiddleSection.getUpStation()).isEqualTo(
                일호선_구간_1번역_3번역_거리_22.getUpStation()),
            () -> assertThat(upToMiddleSection.getDownStation()).isEqualTo(
                일호선_구간_2번역_3번역_거리_12.getUpStation())
        );
    }

    @DisplayName("중간역을 포함하던 두 구간을 이어 새로운 구간을 생성한다.")
    @Test
    void createUpToDownSection() {
        //given & when
        final Section newSection = 일호선_구간_1번역_2번역_거리_10.createUpToDownSection(
            일호선_구간_2번역_3번역_거리_12);

        //then
        assertAll(
            () -> assertThat(newSection.getUpStation()).isEqualTo(일호선_구간_1번역_2번역_거리_10.getUpStation()),
            () -> assertThat(newSection.getDownStation()).isEqualTo(일호선_구간_2번역_3번역_거리_12.getDownStation())
        );
    }
}
