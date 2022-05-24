package wooteco.subway.domain.path;

import java.util.List;

public class Path {

    private final List<Long> stationIds;
    private final double distance;


    public Path(final List<Long> stationIds, final double distance) {
        this.stationIds = stationIds;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Path{" +
            "stationIds=" + stationIds +
            ", distance=" + distance +
            '}';
    }
}
