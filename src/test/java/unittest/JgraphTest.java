package unittest;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

public class JgraphTest {

    @Test
    void study_library() {

//        new WeightedMultigraph<String, DefaultWeightedEdge>( DefaultWeightedEdge.class);
        final WeightedMultigraph<String, DefaultWeightedEdge> graph =
            new WeightedMultigraph<>(DefaultWeightedEdge.class);

        graph.addVertex("v1"); // 정점, 지하철역 station을 string으로 추가
        graph.addVertex("v2");
        graph.addVertex("v3");

        graph.setEdgeWeight(graph.addEdge("v1", "v2"),
            2); // 정점간에 nC2로 간선, 구간 section 정보를 edge(간선)으로 취급하는데, double 숫자형태로 가중치를 줄 수 있다.
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        System.out.println("graph = " + graph);
        /* graph
         * v1<-[2]<-v2<-[2]-v3
         *                  v3-[100]->v1
         *
         * graph = ([v1, v2, v3], [{v1,v2}, {v2,v3}, {v1,v3}])
         * */

        final DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        final List<String> shortestPath = dijkstraShortestPath.getPath("v3", "v1").getVertexList();
        // 최단 경로는 graph -> 시작정점에서 도착정점까지 정점 list로 나온다.
        System.out.println("shortestPath = " + shortestPath);
        //shortestPath = [v3, v2, v1]

        Assertions.assertThat(shortestPath.size()).isEqualTo(3);
    }
}
