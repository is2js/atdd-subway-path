package wooteco.subway.domain.path;

import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.fare.Fare;

public class Path {

    private final List<Station> stations;
    private final int distance;
    private Fare fare;

    public Path(final List<Station> stations, final int distance, final Fare fare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = fare;
    }

    public int calculateFare() {
        return fare.calculate(distance);
    }

    public List<Station> getStations() {
        return List.copyOf(stations);
    }

    public int getDistance() {
        return distance;
    }

    public Fare getFare() {
        return fare;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Path path = (Path) o;
        return getDistance() == path.getDistance() && Objects.equals(getStations(), path.getStations())
            && Objects.equals(getFare(), path.getFare());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStations(), getDistance(), getFare());
    }

    @Override
    public String toString() {
        return "Path{" +
            "stations=" + stations +
            ", distance=" + distance +
            ", fare=" + fare +
            '}';
    }
}
