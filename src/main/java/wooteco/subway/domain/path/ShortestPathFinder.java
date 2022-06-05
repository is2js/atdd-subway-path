package wooteco.subway.domain.path;

import wooteco.subway.domain.Station;

public interface ShortestPathFinder {
    Path find(Station source, Station target, final int age);
}
