package wooteco.subway.ui.dto.request;

import java.util.Objects;
import wooteco.subway.domain.section.Section;

public class SectionRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = Objects.requireNonNull(upStationId, ERROR_NULL);
        this.downStationId = Objects.requireNonNull(downStationId, ERROR_NULL);
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Section toEntity(final Long lineId) {
        return new Section(lineId, this.upStationId, this.downStationId, this.distance);
    }
}
