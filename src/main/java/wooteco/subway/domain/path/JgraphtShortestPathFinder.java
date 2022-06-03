package wooteco.subway.domain.path;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;

public class JgraphtShortestPathFinder implements ShortestPathFinder {

    private final ShortestPathAlgorithm<Station, DefaultWeightedEdge> shortestPath;

    private JgraphtShortestPathFinder(
        final ShortestPathAlgorithm<Station, DefaultWeightedEdge> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public static JgraphtShortestPathFinder of(final Sections sections) {
        final WeightedMultigraph<Station, DefaultWeightedEdge> graph =
            new WeightedMultigraph<>(DefaultWeightedEdge.class);

        final List<Station> stations = sections.getUniqueStations();
        validateStations(stations);
        validateSections(sections.getValue());

        addVertex(stations, graph);
        addEdge(sections.getValue(), graph);

        return new JgraphtShortestPathFinder(new DijkstraShortestPath<>(graph));
    }

    private static void addVertex(final List<Station> stations,
                                  final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (final Station station : stations) {
            graph.addVertex(station);
        }
    }

    private static void addEdge(final List<Section> sections,
                                final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (final Section section : sections) {
            graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()
            );
        }
    }

    private static void validateStations(final List<Station> stations) {
        checkNull(stations);
        checkCountOfStations(stations);
    }

    private static void checkNull(final Object object) {
        Objects.requireNonNull(object, "[ERROR] 빈칸 입력은 허용하지 않는다.");
    }

    private static void checkCountOfStations(final List<Station> stations) {
        if (stations.size() <= 1) {
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

    @Override
    public Path find(final Station source, final Station target) {
        validateInvalidStations(source, target);

        final GraphPath<Station, DefaultWeightedEdge> graphPath = findShortestPath(source, target)
            .orElseThrow(() -> new IllegalStateException("[ERROR] 해당 경로가 존재하지 않습니다."));

        return new Path(graphPath.getVertexList(), (int) graphPath.getWeight());
    }

    private Optional<GraphPath<Station, DefaultWeightedEdge>> findShortestPath(final Station source,
                                                                               final Station target) {
        return Optional.ofNullable(shortestPath.getPath(source, target));
    }

    private void validateInvalidStations(final Station source, final Station target) {
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException("[ERROR] 경로를 찾으려면 같은 역을 입력할 수 없습니다.");
        }
    }
}
