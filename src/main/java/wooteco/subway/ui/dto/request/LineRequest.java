package wooteco.subway.ui.dto.request;

import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import wooteco.subway.domain.Line;

public class LineRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    @NotBlank(message = "[ERROR] 이름을 입력하세요.")
    private String name;
    @NotBlank(message = "[ERROR] 색상을 입력하세요.")
    private String color;
    @NotNull(message = "[ERROR] 상행역을 입력하세요")
    private Long upStationId;
    @NotNull(message = "[ERROR] 하행역을 입력하세요")
    private Long downStationId;
    @Positive(message = "[ERROR] 거리는 양수야 합니다.")
    private Integer distance;
    @Positive(message = "[ERROR] 추가 요금은 양수야 합니다.")
    private Integer extraFare;


    private LineRequest() {
    }

    public LineRequest(final String name, final String color, final Integer extraFare) {
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public LineRequest(final String name,
                       final String color,
                       final Long upStationId,
                       final Long downStationId,
                       final Integer distance,
                       final Integer extraFare) {
        this.name = Objects.requireNonNull(name, ERROR_NULL);
        this.color = Objects.requireNonNull(color, ERROR_NULL);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.extraFare = extraFare;
    }

    public Line toEntity(final Long id) {
        return new Line(id, this.name, this.color, this.extraFare);
    }

    public Line toEntity() {
        return toEntity(null);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public Integer getExtraFare() {
        return extraFare;
    }

    @Override
    public String toString() {
        return "LineRequest{" +
            "name='" + name + '\'' +
            ", color='" + color + '\'' +
            ", upStationId=" + upStationId +
            ", downStationId=" + downStationId +
            ", distance=" + distance +
            ", extraFare=" + extraFare +
            '}';
    }
}
