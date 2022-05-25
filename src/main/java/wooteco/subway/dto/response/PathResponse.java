package wooteco.subway.dto.response;

import java.util.List;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.path.Path;

public class PathResponse {
    private List<Station> stations;

    private int distance;
    private int fare;

    public PathResponse() {
    }

    public PathResponse(final List<Station> stations, final int distance, final int calculateFare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = calculateFare;
    }

    public static PathResponse from(final List<Station> stations, final Path path) {
        return new PathResponse(stations, path.getDistance(), path.calculateFare());
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getFare() {
        return fare;
    }
}
