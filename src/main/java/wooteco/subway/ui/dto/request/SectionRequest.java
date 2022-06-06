package wooteco.subway.ui.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;

public class SectionRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    @NotNull(message = "[ERROR] 상행역을 입력하세요")
    private Long upStationId;
    @NotNull(message = "[ERROR] 하행역을 입력하세요")
    private Long downStationId;
    @Positive(message = "[ERROR] 거리는 양수야 합니다.")
    private Integer distance;

    private SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }


    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Section toEntity(final Line line, final Station upStation, final Station downStation) {
        return new Section(line, upStation, downStation, this.distance);
    }
}
