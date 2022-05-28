package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;
import wooteco.subway.ui.dto.request.LineRequest;
import wooteco.subway.ui.dto.request.SectionRequest;
import wooteco.subway.ui.dto.response.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineController(final LineService lineService, final StationService stationService,
                          final SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        final Line createdLine = lineService.create(lineRequest);
        // TODO: line 생성후, response하지 않더라도, 상/하행 종점정보를 이용해서 default section을 만든다.
        //  request에만 기본 상/하행 종점역이 존재하며,Line 도메인 자체에는 정보를 보유하지 않는다.
        //  -> 기본 구간을 만들고, 그 구간에서 사용된 stations들만 반환하는 식으로 변경한다.
        //  -> 현재는, request에 있는 상하행종점 역 id로 조회만해서보내준다.
        //  -> 기본 구간이 있어야, addSection이 가능한 것이었다. -> sectinoDao에 save를 추가한다.

        // default section생성도, 메인 LineSerivce내부에서 처리하게 한다. 일련의 연속된 업무처리기 때문에.
        //sectionService.create(createdLine.getId(), lineRequest);

        // TODO REFACTOR 1: controller에서 각각 모으면 -> service내부에서 dao로 처리?!
        //  그러려면, 상위도메인이 필드객체로 가지고 있어야한다 ( service내에서 모으려면 dto로 반환해야함)
        //  상위도메인이 하위도메인 or 갖다쓰는 타도메인을 필드객체로 가진다면, dto 변환 이전에 service가 dao를 통해 모아진 상태로
        //  controller에는 1개만 반환할 수 있다.
        //하위도메인을 만들기 위해선, 무조건 상위도메인의 id를 끌고와야한다.
        // -> Lineservice내부에서 sectionDao로 처리

        //TODO: station에서 불러올게 아니라, LINE -> SECTION -> 거기에 사용된 Station들을 불러오도록
        // -> LINE이 하위도메인 SECTIONS를 가지도록 리팩토링하고, -> 거기서 STATIONS들을 뽑아내도록
        // -> TODO REFACTOR 2
        final List<Station> stations = stationService.findUpAndDownStations(lineRequest);
        final LineResponse lineResponse = new LineResponse(createdLine, stations);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
            .body(lineResponse);
    }

    @GetMapping
    public List<LineResponse> findAllLine() {
        final List<Line> lines = lineService.findAll();
        return lines.stream()
            .map(line -> new LineResponse(line, sectionService.findStationsByLineId(line.getId())))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        final Line targetLine = lineService.findById(id);
        final LineResponse lineResponse = new LineResponse(targetLine,
            sectionService.findStationsByLineId(targetLine.getId()));
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent()
            .build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.ok().build();
    }
}
