package wooteco.subway.ui.dto.request;

import java.util.Objects;
import wooteco.subway.domain.Line;

public class LineRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;


    private LineRequest() {
    }

    public LineRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(final String name,
                       final String color,
                       final long upStationId,
                       final long downStationId,
                       final int distance) {
        this.name = Objects.requireNonNull(name, ERROR_NULL);
        this.color = Objects.requireNonNull(color, ERROR_NULL);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toEntity(final Long id) {
        return new Line(this.name, this.color);
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

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
