package wooteco.subway.domain.path;

public interface ShortestPathFinder {
    Path find(Long source, Long target);
}
