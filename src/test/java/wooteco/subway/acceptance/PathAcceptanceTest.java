package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.AcceptanceFixtures.requestGetPath;
import static wooteco.subway.acceptance.AcceptanceFixtures.requestPostLine;
import static wooteco.subway.acceptance.AcceptanceFixtures.requestPostSection;
import static wooteco.subway.acceptance.AcceptanceFixtures.requestPostStation;
import static wooteco.subway.testutils.SubWayFixtures.LINE_REQUEST_2호선_STATION_1_5_거리55;
import static wooteco.subway.testutils.SubWayFixtures.SECTION_REQUEST_1번역_2번역_거리5;
import static wooteco.subway.testutils.SubWayFixtures.SECTION_REQUEST_2번역_3번역_거리10;
import static wooteco.subway.testutils.SubWayFixtures.SECTION_REQUEST_3번역_4번역_거리15;
import static wooteco.subway.testutils.SubWayFixtures.SECTION_REQUEST_4번역_5번역_거리20;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_강남역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_사당역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_선릉역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_신림역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_잠실역;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.request.PathRequest;

@Transactional
@DisplayName("지하철 경로 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    @DisplayName("출발역과 도착역을 입력하면 경로를 응답한다.")
    @Test
    void find() {
        requestPostStation(STATION_REQUEST_강남역, "/stations");
        requestPostStation(STATION_REQUEST_잠실역, "/stations");
        requestPostStation(STATION_REQUEST_선릉역, "/stations");
        requestPostStation(STATION_REQUEST_사당역, "/stations");
        requestPostStation(STATION_REQUEST_신림역, "/stations");

        requestPostLine(LINE_REQUEST_2호선_STATION_1_5_거리55, "/lines");

        requestPostSection(SECTION_REQUEST_1번역_2번역_거리5, "/lines/1/sections");
        requestPostSection(SECTION_REQUEST_2번역_3번역_거리10, "/lines/1/sections");
        requestPostSection(SECTION_REQUEST_3번역_4번역_거리15, "/lines/1/sections");
        requestPostSection(SECTION_REQUEST_4번역_5번역_거리20, "/lines/1/sections");

        final ExtractableResponse<Response> response = requestGetPath(new PathRequest(1L, 5L, 15),
            "/paths");

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.body().jsonPath().getList("stations"))
                .hasSize(5)
                .extracting("id", "name")
                .containsExactly(
                    tuple(1, "강남역"),
                    tuple(2, "잠실역"),
                    tuple(3, "선릉역"),
                    tuple(4, "사당역"),
                    tuple(5, "신림역"))
        );
    }
}
