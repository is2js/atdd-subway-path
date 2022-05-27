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

//1. 생성자주입으로 재료를 받아 상황을 만들고, method로 역할 수행할 객체 class를 만든다. -> test를 만들어서, 생성자 + method를 모두 개발한다.
public class ShortestPathFinder {

    // 5. 결국 학습테스트에서 만든 [최단경로 메서드 호출 직전]까지의 객체를 상황객체의 필드(상태값)로 가지고 있게 된다.
    //   상황을 여기까지 만들어놓고, 상태값으로 가지고 있겠다는 뜻이다.
    //   -> 마치 의도해서 라이브러리를 매핑해놓은 것 같지만.. 그 말은 [래핑클래스를 만들어 생성자주입으로 재료를 받아] -> [조회인자 + 메서드로 역할 수행] 직전까지의 상황을 -> [객체]로 만들어놓는 것과 같다.
    //   -> 그래놓고, [상황에 맞는 객체]. 학습테스트 최종 응답값을 주는 메서드( 필요인자-조회용인자 )를 메서드로 수행한다.

    private final ShortestPathAlgorithm<Long, DefaultWeightedEdge> shortestPath;

    private ShortestPathFinder(
        final ShortestPathAlgorithm<Long, DefaultWeightedEdge> shortestPath) {
        // 4. 라이브러리 구상체 객체를 정팩매의 [인자=우항]로 넘겨주면 -> 기본생성자를 생성하면서 -> [파라미터=변수]추상체로 받으라고 목록을 뿌려준다.
        // 아직.. 잘 모르지만, 추상체로 생성자 파라미터를 지정해줬다. 물논 최단경로를 뱉어내는 필요메서드.getPath~를  물려준  추상체인지 확인했다.

        // 4-2. 정펙매 -> 기본생성자로 넘겨준다면
        //   (1) 기본생성자 private 시키기
        //   (2) 기본생성자에서 검증하기? 정펙매에서 해야할까? -> 로직을 받아주는 곳이 정펙매이고 private으로 잠궜다면
        //    -> private으로 잠궛으니 유일 통로는 public 정팩매 -> 유일 통로라면, 검증을 몰아줘도 된다.
        this.shortestPath = shortestPath;
    }

    public static ShortestPathFinder of(final List<Long> stations, final List<Section> sections) {

        //4-3. 정펙매로 인해 private기본생성자는 호출안된다고 가정하고, 유일통로로서 + 외부의 재료들을 받는 곳으로 검증을 시행해줌
        validateStations(stations);
        validateSections(sections);

        //2. 정점을 "v1", "v2" 대신 station 객체를 Type으로 준다.
        // 간선정보인 section에서 앞/뒤 정점을 뽑아줘야하는데, 그게 upStation, downStation이고, weight는 distance다.
        // -> 나는 section에 station 상위도메인객체 대신 id를 저장해놔서, dao가 없는 domain에선 up/downStation이 아닌 id밖에 못뽑느다.
        // --> 정점을 station객체 대신 --> stationId인 Long type으로 주자.
//        final WeightedMultigraph<Station, DefaultWeightedEdge> graph =
        final WeightedMultigraph<Long, DefaultWeightedEdge> graph =
            new WeightedMultigraph<>(DefaultWeightedEdge.class);

        addVertex(stations, graph);
        addEdge(sections, graph);

        // 3. 제네릭은 graph와 동일하게 받더라.
        // 객체를 뽑았으면 바로 .find(source, target) 하고 싶지만
        // -> 생성자에서는 상황만 만들고, 메서드에서 인자를 받아 역할을 수행한다.
        // -> 생성자에서 재료를 정리했다면, 필드로 가지고 있는다! -> 상황을 만든 것 == 재료들 정리후 필드로 가지고 있는 것

        // + 정팩메에서는 정리후 필드로 못넣는다(static)상태 -> return 기본생성자에 [정리한 필드용 재료]를 변수로 뽑지말고 생성자에 건네준다.
        //final DijkstraShortestPath<Long, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
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
        if (sections.size() < 1) {
            throw new IllegalArgumentException("[ERROR] 구간이 비어서 경로를 만들 수 없습니다.");
        }
    }

    public Path find(final Long source, final Long target) {
        // 외부 인자는 일단 검사한다.
        validateStationIds(source, target);

        // 주입받아 생성한 호출직전의 객체 필드를 사용해서 역할을 수행한다.
        // - 문서를 읽djT을 때, null가능성이 있으면 Optional로 받는다.?
        final Optional<GraphPath<Long, DefaultWeightedEdge>> graphPathOrEmpty = Optional.ofNullable(
            shortestPath.getPath(source, target));
        final GraphPath<Long, DefaultWeightedEdge> graphPath = graphPathOrEmpty.orElseThrow(
            () -> new IllegalStateException("[ERROR] 해당 경로가 존재하지 않습니다."));

        // 같이 다니는 것이 발생하면 도메인으로 묶어서 응답한다.
        return new Path(graphPath.getVertexList(), (int) graphPath.getWeight());
    }

    private void validateStationIds(final Long source, final Long target) {
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException("[ERROR] 경로를 찾으려면 같은 역을 입력할 수 없습니다.");
        }
    }
}
