package wooteco.subway.ui.dto.request;

import wooteco.subway.domain.section.Section;

public class SectionRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private long upStationId;
    private long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Section toEntity(final Long lineId) {
        return new Section(lineId, this.upStationId, this.downStationId, this.distance);
    }
}
