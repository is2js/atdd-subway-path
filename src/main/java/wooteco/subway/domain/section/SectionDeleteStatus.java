package wooteco.subway.domain.section;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public enum SectionDeleteStatus {
    DELETE_UP_STATION(SectionDeleteStatus::isFirstSection),
    DELETE_DOWN_STATION(SectionDeleteStatus::isLastSection),
    DELETE_MIDDLE(SectionDeleteStatus::isMiddleSection),
    ;

    private final BiPredicate<List<Section>, Long> condition;

    SectionDeleteStatus(final BiPredicate<List<Section>, Long> condition) {
        this.condition = condition;
    }

    public static SectionDeleteStatus from(final List<Section> sections, final Long stationId) {
        return Arrays.stream(values())
            .filter(it -> it.condition.test(sections, stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("[ERROR] 해당 구간을 삭제할 수 없습니다."));
    }

    private static boolean isFirstSection(final List<Section> sections, final Long stationId) {
        final Long upStationId = sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation().getId(), section.getDownStation().getId()))
            .distinct()
            .min(Comparator.comparing(it -> it))
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] 첫 구간을 찾을 수 없습니다."));
        return upStationId.equals(stationId);
    }

    private static boolean isLastSection(final List<Section> sections, final Long stationId) {
        final Long downStationId = sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation().getId(), section.getDownStation().getId()))
            .distinct()
            .max(Comparator.comparing(it -> it))
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] 마지막 구간을 찾을 수 없습니다."));
        return downStationId.equals(stationId);
    }

    private static boolean isMiddleSection(final List<Section> sections, final Long stationId) {
        if (isFirstSection(sections, stationId)) {
            return false;
        }
        return !isLastSection(sections, stationId);
    }
}
