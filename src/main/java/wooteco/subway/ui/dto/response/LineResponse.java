package wooteco.subway.ui.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wooteco.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    public LineResponse(final Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = line.getSections()
            .getConnectedSections()
            .stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .distinct()
            .map(StationResponse::new)
            .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
