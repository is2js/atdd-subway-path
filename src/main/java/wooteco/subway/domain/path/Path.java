package wooteco.subway.domain.path;

import java.util.List;

public class Path {

    private static final int DEFAULT_FARE = 1250;
    private static final int FIRST_ADDITIONAL_FARE_DISTANCE = 10;
    private static final int SECOND_ADDITIONAL_FARE_DISTANCE = 50;
    private final List<Long> stationIds;
    private final int distance;

    public Path(final List<Long> stationIds, final int distance) {
        this.stationIds = stationIds;
        this.distance = distance;
    }

    public int calculateFare() {
        if (distance <= FIRST_ADDITIONAL_FARE_DISTANCE) {
            return DEFAULT_FARE;
        }
        if (distance <= SECOND_ADDITIONAL_FARE_DISTANCE) {
            return DEFAULT_FARE + calculateOverFare(distance - FIRST_ADDITIONAL_FARE_DISTANCE, 5);
        }

        return DEFAULT_FARE
            + calculateOverFare(Math.min(distance, SECOND_ADDITIONAL_FARE_DISTANCE) - FIRST_ADDITIONAL_FARE_DISTANCE, 5)
            + calculateOverFare(distance - SECOND_ADDITIONAL_FARE_DISTANCE, 8);
    }

    private int calculateOverFare(final int distance, final int unitDistance) {
        return ((distance - 1) / unitDistance + 1) * 100;
    }

    public List<Long> getStationIds() {
        return List.copyOf(stationIds);
    }

    public int getDistance() {
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
