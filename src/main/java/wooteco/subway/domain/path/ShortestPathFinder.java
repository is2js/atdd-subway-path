package wooteco.subway.domain.path;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.domain.section.Section;

public class ShortestPathFinder {

    private final ShortestPathAlgorithm<Long, DefaultWeightedEdge> shortestPath;

    private ShortestPathFinder(
        final ShortestPathAlgorithm<Long, DefaultWeightedEdge> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public static ShortestPathFinder of(final List<Long> stations, final List<Section> sections) {
        validateStations(stations);
        validateSections(sections);

        final WeightedMultigraph<Long, DefaultWeightedEdge> graph =
            new WeightedMultigraph<>(DefaultWeightedEdge.class);

        addVertex(stations, graph);
        addEdge(sections, graph);

        return new ShortestPathFinder(new DijkstraShortestPath<>(graph));
    }

    private static void addVertex(final List<Long> stations,
                                  final WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        for (final Long stationId : stations) {
            graph.addVertex(stationId);
        }
    }

    private static void addEdge(final List<Section> sections,
                                final WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        for (final Section section : sections) {
            graph.setEdgeWeight(
                graph.addEdge(section.getUpStationId(), section.getDownStationId()),
                section.getDistance()
            );
        }
    }

    private static void validateStations(final List<Long> stations) {
        checkNull(stations);
        checkCountOfStations(stations);
    }

    private static void checkNull(final Object object) {
        Objects.requireNonNull(object, "[ERROR] 빈칸 입력은 허용하지 않는다.");
    }

    private static void checkCountOfStations(final List<Long> stationids) {
        if (stationids.size() <= 1) {
            throw new IllegalArgumentException("[ERROR] 지하철역이 부족하여 경로를 만들 수 없습니다.");
        }
    }

    private static void validateSections(final List<Section> sections) {
        checkNull(sections);
        checkCountOfSections(sections);
    }

    private static void checkCountOfSections(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 구간이 비어서 경로를 만들 수 없습니다.");
        }
    }

    public Path find(final Long source, final Long target) {
        validateStationIds(source, target);

        final GraphPath<Long, DefaultWeightedEdge> graphPath = findShortedPath(source, target)
            .orElseThrow(() -> new IllegalStateException("[ERROR] 해당 경로가 존재하지 않습니다."));

        return new Path(graphPath.getVertexList(), (int) graphPath.getWeight());
    }

    private Optional<GraphPath<Long, DefaultWeightedEdge>> findShortedPath(final Long source, final Long target) {
        return Optional.ofNullable(
            shortestPath.getPath(source, target));
    }

    private void validateStationIds(final Long source, final Long target) {
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException("[ERROR] 경로를 찾으려면 같은 역을 입력할 수 없습니다.");
        }
    }
}
