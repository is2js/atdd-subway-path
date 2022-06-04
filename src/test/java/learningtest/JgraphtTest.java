package learningtest;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

public class JgraphtTest {

    @Test
    void study_library() {
        final WeightedMultigraph<String, DefaultWeightedEdge> graph =
            new WeightedMultigraph<>(DefaultWeightedEdge.class);

        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");

        graph.setEdgeWeight(graph.addEdge("v1", "v2"),
            2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        final DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        final List<String> shortestPath = dijkstraShortestPath.getPath("v3", "v1").getVertexList();
        final List edgeList = dijkstraShortestPath.getPath("v3", "v1").getEdgeList();

        Assertions.assertThat(shortestPath.size()).isEqualTo(3);
    }
}
