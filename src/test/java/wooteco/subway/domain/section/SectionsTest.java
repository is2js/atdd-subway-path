package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.testutils.SubWayFixtures.사번_사당역;
import static wooteco.subway.testutils.SubWayFixtures.삼번_잠실역;
import static wooteco.subway.testutils.SubWayFixtures.이번역_선릉;
import static wooteco.subway.testutils.SubWayFixtures.일번역_강남;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_2번역_거리_10;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_3번역_거리_22;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_2번역_3번역_거리_12;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SectionNotFoundException;

class SectionsTest {

    @DisplayName("특정 노선에 속한 구간정보를 생성한다")
    @Test
    void create_success() {
        //given & when
        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12);

        //then
        assertDoesNotThrow(() -> new Sections(sections));
    }

    @DisplayName("특정 노선에 구간이 존재하지 않는다면 구간정보를 생성할 수 없다.")
    @Test
    void create_fail() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 존재하지 않는 구간입니다.");
    }

    @DisplayName("기존 구간정보와 상행, 하행 종점 중 하나만 같은 구간은 구간 등록이 가능하다.")
    @Test
    void addSection_valid_only_one_station_same() {
        //given & when
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12));
        final Section targetSection = new Section(1L, 일번역_강남, 사번_사당역, 8);

        //then
        assertDoesNotThrow(() -> sections.addSection(targetSection));
    }

    @DisplayName("기존 구간정보와 같은, 상행/하행 종점을 포함하는 경우 예외가 발생한다.")
    @Test
    void addSection_invalid_all_same_up_and_down_station() {
        //given & when
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12));
        final Section targetSection = new Section(1L, 일번역_강남, 이번역_선릉, 12);

        //then
        assertThatThrownBy(() -> sections.addSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 추가할 구간 속 지하철역이 기존 구간에 이미 존재합니다.");
    }

    @DisplayName("기존 구간정보와 상행, 하행 종점 모두 다른 구간은 추가시 예외가 발생한다.")
    @Test
    void addSection_invalid_all_not_same_up_and_down_station() {
        //given & when
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12));
        final Section targetSection = new Section(1L, 이번역_선릉, 사번_사당역, 13);

        //then
        assertThatThrownBy(() -> sections.addSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 기존 구간보다 긴 구간을 추가할 순 없습니다.");
    }

    @DisplayName("상행 종점이 같은 구간을 추가할 때, 거리가 더 클 경우 예외가 발생한다.")
    @Test
    void addSection_invalid_distance() {
        //given & when
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12));
        final Section targetSection = new Section(1L, 일번역_강남, 사번_사당역, 11);

        //then
        assertThatThrownBy(() -> sections.addSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 기존 구간보다 긴 구간을 추가할 순 없습니다.");
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @DisplayName("상행종점이 같은 구간에 대해 역 사이에 새로운 역을 등록할 경우, 기존 구간(1~3)을 쪼개진 구간(1~2, 2~3) 중 하나(2~3)로 업데이트한다.")
    @Test
    void addSection_same_up_station() {
        //given
        final Sections sections_1_3 = new Sections(List.of(일호선_구간_1번역_3번역_거리_22));
        final Section section_1_2 = new Section(3L, 1L, 일번역_강남, 이번역_선릉, 15);

        // when
        final Section section_2_3 = sections_1_3.addSection(section_1_2).get();

        //then
        assertAll(
            () -> assertThat(section_2_3.getUpStation()).isEqualTo(section_1_2.getDownStation()),
            () -> assertThat(section_2_3.getDownStation()).isEqualTo(일호선_구간_1번역_3번역_거리_22.getDownStation())
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Disabled
    @DisplayName("하행종점이 같은 구간에 대해 역 사이에 새로운 역을 등록할 경우, 기존 구간(1~3)을 쪼개진 구간(1~2, 2~3) 중 하나(1~2)를 반환받아 updated한다.")
    @Test
    void addSection_same_down_station() {
        //given
        final Sections sections_1_3 = new Sections(List.of(일호선_구간_1번역_3번역_거리_22));
        final Section section_2_3 = new Section(3L, 1L, 이번역_선릉, 삼번_잠실역, 15);

        //when
        final Section section_1_2 = sections_1_3.addSection(section_2_3).get();

        //then
        assertAll(
            () -> assertThat(section_1_2.getUpStation()).isEqualTo(일호선_구간_1번역_3번역_거리_22.getUpStation()),
            () -> assertThat(section_1_2.getDownStation()).isEqualTo(section_2_3.getUpStation())
        );
    }

    @Disabled
    @DisplayName("구간을 추가할 때, 상행/하행 종점으로 새로운 역이 추가될 경우, 구간은 1개가 추가되며 update될 데이터는 없다.")
    @Test
    void addSection_end_station() {
        //given
        final Sections section_2_3 = new Sections(List.of(일호선_구간_2번역_3번역_거리_12));
        final Section section_1_2 = new Section(3L, 1L, 일번역_강남, 이번역_선릉, 15);

        //when
        final Section update = section_2_3.addSection(section_1_2)
            .orElse(null);

        //then
        assertAll(
            () -> assertThat(section_2_3.getValue()).hasSize(2),
            () -> assertThat(update).isNull()
        );
    }

    @DisplayName("구간 삭제시 존재하지 않는 지하철역 id가 입력되면 예외를 발생시킨다.")
    @Test
    void deleteSectionByStationId_invalid_not_existing_stationId() {
        //given
        final Sections sections = new Sections(List.of(일호선_구간_2번역_3번역_거리_12));

        //when & then
        Assertions.assertThatThrownBy(() -> sections.deleteSectionByStationId(1L))
            .isInstanceOf(SectionNotFoundException.class)
            .hasMessage("[ERROR] 해당 이름의 지하철역이 구간내 존재하지 않습니다.");
    }

    @DisplayName("구간 삭제시 기본 구간만 존재하면 예외를 발생시킨다.")
    @Test
    void deleteSectionByStationId_invalid_ony_default_section() {
        //given
        final Sections sections = new Sections(List.of(일호선_구간_2번역_3번역_거리_12));

        //when & then
        Assertions.assertThatThrownBy(() -> sections.deleteSectionByStationId(2L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 역 2개의 기본 구간만 존재하므로 더이상 구간 삭제할 수 없습니다.");
    }

    @DisplayName("구간 삭제시 상행 종점을 제거할 수 있다.")
    @Test
    void deleteSectionByStationId_valid_delete_up_station() {
        //given
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12));

        //when
        sections.deleteSectionByStationId(1L);
        final List<Section> actual = sections.getValue();

        //then
        assertAll(
            () -> assertThat(actual.get(0).getUpStation()).isEqualTo(이번역_선릉),
            () -> assertThat(actual).hasSize(1)
        );
    }

    @DisplayName("구간 삭제시 하행 종점을 제거할 수 있다.")
    @Test
    void deleteSectionByStationId_valid_delete_down_station() {
        //given
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12));

        //when
        sections.deleteSectionByStationId(3L);
        final List<Section> actual = sections.getValue();

        //then
        assertAll(
            () -> assertThat(actual.get(actual.size() - 1).getDownStation()).isEqualTo(이번역_선릉),
            () -> assertThat(actual).hasSize(1)
        );
    }

    @DisplayName("구간 삭제시 중간역을 제거할 수 있다.")
    @Test
    void deleteSectionByStationId_valid_delete_middle_station() {
        //given
        final Sections sections = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12));

        //when
        sections.deleteSectionByStationId(2L);
        final List<Section> actual = sections.getValue();

        //then
        assertAll(
            () -> assertThat(actual.get(0).getUpStation()).isEqualTo(일번역_강남),
            () -> assertThat(actual.get(actual.size() - 1).getDownStation()).isEqualTo(삼번_잠실역),
            () -> assertThat(actual).hasSize(1)
        );
    }

    @DisplayName("정렬된 구간을 반환한다.")
    @Test
    void getSortedSections() {

        final Sections sections = new Sections(List.of(
            일호선_구간_2번역_3번역_거리_12, 일호선_구간_1번역_2번역_거리_10));

        final Sections actual = new Sections(sections.getConnectedSections());

        final Sections expected = new Sections(List.of(
            일호선_구간_1번역_2번역_거리_10, 일호선_구간_2번역_3번역_거리_12));

        assertThat(actual.toString()).isEqualTo(expected.toString());
    }

    @DisplayName("")
    @Test
    void getUniqueStationIds() {

        final Sections sections = new Sections(List.of(
            일호선_구간_2번역_3번역_거리_12, 일호선_구간_1번역_2번역_거리_10));
        final List<Long> expected = List.of(1L, 2L, 3L);

        final List<Long> actual = sections.getUniqueStationIds();

        assertThat(actual).isEqualTo(expected);
    }
}
