package wooteco.subway.acceptance;

import static wooteco.subway.acceptance.AcceptanceFixtures.requestPostLine;
import static wooteco.subway.acceptance.AcceptanceFixtures.requestPostStation;
import static wooteco.subway.testutils.SubWayFixtures.LINE_REQUEST_분당선_STATION_1_3;
import static wooteco.subway.testutils.SubWayFixtures.LINE_REQUEST_신분당선_STATION_1_2;
import static wooteco.subway.testutils.SubWayFixtures.SECTION_REQUEST_1번역_2번역_거리5;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_강남역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_역삼역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_잠실역;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("새로운 하행 종점을 기존 구간에 등록한다.")
    @Test
    void addSection_new_station() {
        requestPostStation(STATION_REQUEST_강남역, "/stations");
        requestPostStation(STATION_REQUEST_잠실역, "/stations");
        requestPostLine(LINE_REQUEST_신분당선_STATION_1_2, "/lines");

        final ExtractableResponse<Response> response = AcceptanceFixtures.requestPostSection(
            SECTION_REQUEST_1번역_2번역_거리5,
            "/lines/1/sections");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운 역을 기존 구간의 중간역으로 구간 등록한다.")
    @Test
    void addSection_middle_station() {
        requestPostStation(STATION_REQUEST_강남역, "/stations");
        requestPostStation(STATION_REQUEST_잠실역, "/stations");
        requestPostStation(STATION_REQUEST_역삼역, "/stations");
        requestPostLine(LINE_REQUEST_분당선_STATION_1_3, "/lines");

        final ExtractableResponse<Response> response = AcceptanceFixtures.requestPostSection(
            SECTION_REQUEST_1번역_2번역_거리5,
            "/lines/1/sections");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간에서 중간역을 삭제하여 구간을 삭제한다.")
    @Test
    void deleteSection_delete_middle_station() {
        requestPostStation(STATION_REQUEST_강남역, "/stations");
        requestPostStation(STATION_REQUEST_잠실역, "/stations");
        requestPostStation(STATION_REQUEST_역삼역, "/stations");
        requestPostLine(LINE_REQUEST_분당선_STATION_1_3, "/lines");
        AcceptanceFixtures.requestPostSection(SECTION_REQUEST_1번역_2번역_거리5, "/lines/1/sections");

        final ExtractableResponse<Response> response = AcceptanceFixtures.requestDeleteSection(
            "/lines/1/sections?stationId=2");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간에서 하행 종점역을 삭제하여 구간을 삭제한다")
    @Test
    void deleteSection_delete_down_station() {
        requestPostStation(STATION_REQUEST_강남역, "/stations");
        requestPostStation(STATION_REQUEST_잠실역, "/stations");
        requestPostStation(STATION_REQUEST_역삼역, "/stations");
        requestPostLine(LINE_REQUEST_분당선_STATION_1_3, "/lines");
        AcceptanceFixtures.requestPostSection(SECTION_REQUEST_1번역_2번역_거리5, "/lines/1/sections");

        final ExtractableResponse<Response> response = AcceptanceFixtures.requestDeleteSection(
            "/lines/1/sections?stationId=3");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간에서 상행 종점역을 삭제하여 구간을 삭제한다")
    @Test
    void deleteSection_delete_up_station() {
        requestPostStation(STATION_REQUEST_강남역, "/stations");
        requestPostStation(STATION_REQUEST_잠실역, "/stations");
        requestPostStation(STATION_REQUEST_역삼역, "/stations");
        requestPostLine(LINE_REQUEST_분당선_STATION_1_3, "/lines");
        AcceptanceFixtures.requestPostSection(SECTION_REQUEST_1번역_2번역_거리5, "/lines/1/sections");

        final ExtractableResponse<Response> response = AcceptanceFixtures.requestDeleteSection(
            "/lines/1/sections?stationId=1");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
