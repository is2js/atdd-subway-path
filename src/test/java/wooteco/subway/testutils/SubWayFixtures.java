package wooteco.subway.testutils;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.ui.dto.request.LineRequest;
import wooteco.subway.ui.dto.request.SectionRequest;
import wooteco.subway.ui.dto.request.StationRequest;

public class SubWayFixtures {
    public static final LineRequest LINE_REQUEST_신분당선_STATION_1_2 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    public static final LineRequest LINE_REQUEST_2호선_STATION_1_5_거리55 = new LineRequest("신분당선", "bg-red-600", 1L, 5L,
        55);
    public static final LineRequest LINE_REQUEST_신분당선2_FOR_PUT = new LineRequest("신분당선2", "bg-red-600");
    public static final LineRequest LINE_REQUEST_분당선_STATION_1_3 = new LineRequest("분당선", "bg-red-601", 1L, 3L, 12);
    public static final LineRequest LINE_REQUEST_중앙선_STATION_1_3 = new LineRequest("중앙선", "bg-red-602", 1L, 3L, 12);
    public static final LineRequest LINE_REQUEST_2호선_STATION_1_3 = new LineRequest("2호선", "bg-red-603", 1L, 3L, 12);
    public static final Line 일호선_파랑 = new Line("1호선", "blue");
    public static final Line 이호선_그린 = new Line("2호선", "green");
    public static final StationRequest STATION_REQUEST_강남역 = new StationRequest("강남역");
    public static final StationRequest STATION_REQUEST_잠실역 = new StationRequest("잠실역");
    public static final StationRequest STATION_REQUEST_역삼역 = new StationRequest("역삼역");
    public static final StationRequest STATION_REQUEST_선릉역 = new StationRequest("선릉역");
    public static final StationRequest STATION_REQUEST_사당역 = new StationRequest("사당역");
    public static final StationRequest STATION_REQUEST_신림역 = new StationRequest("신림역");
    public static final StationRequest STATION_REQUEST_서울대역 = new StationRequest("서울대역");
    public static final Station 강남역 = new Station("강남역");
    public static final Station 선릉역 = new Station("선릉역");
    public static final Station 잠실역 = new Station("잠실역");
    public static final Station 일번_강남역 = new Station(1L, "강남역");
    public static final Station 이번_선릉역 = new Station(2L, "선릉역");
    public static final Station 삼번_잠실역 = new Station(3L, "잠실역");
    public static final Station 사번_사당역 = new Station(4L, "신림역");
    public static final Station 오번_신림역 = new Station(5L, "사당역");
    public static final SectionRequest SECTION_REQUEST_1번역_2번역_거리5 = new SectionRequest(1L, 2L, 5);
    public static final SectionRequest SECTION_REQUEST_2번역_3번역_거리10 = new SectionRequest(2L, 3L, 10);
    public static final SectionRequest SECTION_REQUEST_3번역_4번역_거리15 = new SectionRequest(3L, 4L, 15);
    public static final Section 일호선_구간_1번역_2번역 = new Section(1L, 일번_강남역, 이번_선릉역, 1);
    public static final Section 일호선_구간_1번역_3번역 = new Section(1L, 일번_강남역, 삼번_잠실역, 2);
    public static final Section 일호선_구간_1번역_2번역_거리_10 = new Section(1L, 일번_강남역, 이번_선릉역, 10);
    public static final Section 일호선_구간_2번역_3번역_거리_12 = new Section(2L, 이번_선릉역, 삼번_잠실역, 12);
    public static final Section 일호선_구간_1번역_3번역_거리_22 = new Section(3L, 1L, 일번_강남역, 삼번_잠실역, 22);
    public static final Section 일호선_구간_3번역_4번역_거리_5 = new Section(4L, 1L, 삼번_잠실역, 사번_사당역, 5);
    public static final Section 일호선_구간_4번역_5번역_거리_3 = new Section(5L, 1L, 사번_사당역, 오번_신림역, 3);
    public static final Section 일호선_구간_1번역_5번역_거리_31 = new Section(6L, 1L, 일번_강남역, 오번_신림역, 31);
}
