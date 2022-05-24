package wooteco.subway.domain.path;

import java.util.List;

public class Path {

    private final List<Long> stationIds;
    private final int distance;

    public Path(final List<Long> stationIds, final int distance) {
        this.stationIds = stationIds;
        this.distance = distance;
    }

    public int calculateFare() {
        final int defaultFare = 1250;
        if (distance <= 10) {
            return defaultFare;
        }
        if (10 < distance) {
            return defaultFare + calculateOverFare(10, 5);
        }
        return defaultFare + calculateOverFare(10, 5) + calculateOverFare(50, 8);
    }

    private int calculateOverFare(final int standardDistance, final int unitDistance) {
        final int overDistance = distance - standardDistance;
        return (int) (Math.ceil((overDistance - 1) / unitDistance) + 1) * 100;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Path{" +
            "stationIds=" + stationIds +
            ", distance=" + distance +
            '}';
    }
}
