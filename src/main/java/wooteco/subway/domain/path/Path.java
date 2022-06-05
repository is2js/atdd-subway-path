package wooteco.subway.domain.path;

import java.util.List;
import java.util.Objects;
import org.jgrapht.GraphPath;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.fare.Fare;
import wooteco.subway.domain.path.JgraphtShortestPathFinder.SectionEdge;

public class Path {

    private final List<Station> stations;
    private final int distance;
    private final int maxExtraFare;
    private final Fare fare;

    public Path(final List<Station> stations, final int distance, final int maxExtraFare, final Fare fare) {
        this.stations = stations;
        this.distance = distance;
        this.maxExtraFare = maxExtraFare;
        this.fare = fare;
    }

    public static Path of(final GraphPath<Station, SectionEdge> graphPath, final Fare fare) {
        return new Path(graphPath.getVertexList(), (int) graphPath.getWeight(), calculateMaxExtraFare(graphPath), fare);
    }

    private static int calculateMaxExtraFare(final GraphPath<Station, SectionEdge> graphPath) {
        return graphPath.getEdgeList()
            .stream()
            .mapToInt(SectionEdge::toLineExtraFare)
            .max()
            .orElseThrow(() -> new IllegalStateException("[ERROR] 최단경로 상의 노선 중 가장 비싼 추가요금을 찾을 수 없습니다."));
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
