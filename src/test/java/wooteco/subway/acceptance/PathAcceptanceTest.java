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
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_강남역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_사당역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_서울대역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_선릉역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_신림역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_잠실역;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.ui.dto.request.PathRequest;

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
        requestPostStation(STATION_REQUEST_서울대역, "/stations");

        requestPostLine(LINE_REQUEST_2호선_STATION_1_5_거리55, "/lines");

        requestPostSection(SECTION_REQUEST_1번역_2번역_거리5, "/lines/1/sections");
        requestPostSection(SECTION_REQUEST_2번역_3번역_거리10, "/lines/1/sections");
        requestPostSection(SECTION_REQUEST_3번역_4번역_거리15, "/lines/1/sections");
        //1-5 1-2-5 1-2-3-5  1-2-3-4-5   여기서 또 4-5번을 추가하면 안된다...
        // TODO: 1-2-3-5 에서 3-4를 추가했으면 이미 1-2-3-4-5가 완성된 상태 => 4-5를 추가하면 안됨.
//        requestPostSection(SECTION_REQUEST_4번역_5번역_거리20, "/lines/1/sections");

        // 같은 노선에 대해서는, a-b-c에서 a---c를 추가할 수 없다..
//        requestPostSection(SECTION_REQUEST_1번역_3번역_거리15인데_좁혀서_10, "/lines/1/sections");

        // 새로운 1-6-5를 만들어서 주자. 출발과 끝이 같은 다른 경로는 가능하다.
//        requestPostSection(SECTION_REQUEST_1번역_6번역_거리20, "/lines/1/sections");
        // 1-5가 이미 default로 존재하는 상황에서 1-6(20)을 추가하면 자동으로 6-5(35)가 생성된다. 같은 line이기 때문이다..
//        requestPostSection(SECTION_REQUEST_6번역_5번역_거리20, "/lines/1/sections");

        final ExtractableResponse<Response> response = requestGetPath(new PathRequest(1L, 3L, 20),
            "/paths");

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.body().jsonPath().getList("stations"))
                .hasSize(3)
                .extracting("id", "name")
                .containsExactly(
                    // 응답시 1-6-5경로라도, stationId in Paht -> stationDao에서 Station으로 조회시.. 순서대로x id순으로
                    tuple(1, "강남역"),
                    tuple(2, "잠실역"),
                    tuple(3, "선릉역"))
        );
    }
}
