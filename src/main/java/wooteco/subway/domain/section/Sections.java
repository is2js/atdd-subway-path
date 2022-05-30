package wooteco.subway.domain.section;

import static wooteco.subway.domain.section.SectionAddStatus.ADD_MIDDLE_FROM_UP_STATION;
import static wooteco.subway.domain.section.SectionDeleteStatus.DELETE_DOWN_STATION;
import static wooteco.subway.domain.section.SectionDeleteStatus.DELETE_MIDDLE;
import static wooteco.subway.domain.section.SectionDeleteStatus.DELETE_UP_STATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wooteco.subway.exception.SectionNotFoundException;

public class Sections {

    private static final String ERROR_ALREADY_CONTAIN = "[ERROR] 추가할 구간 속 지하철역이 기존 구간에 이미 존재합니다.";
    private static final String ERROR_INVALID_DISTANCE = "[ERROR] 기존 구간보다 긴 구간을 추가할 순 없습니다.";
    private static final String ERROR_NO_STATION = "[ERROR] 해당 종점을 가지는 구간이 존재 하지 않습니다.";
    private static final String ERROR_FIRST_SECTION_NOT_FOUND = "[ERROR] 첫번째 구간을 찾을 수 없습니다.";

    private final List<Section> value;

    public Sections(final List<Section> sections) {
        this.value = new ArrayList<>(sections);
    }

    public Optional<Section> addSection(final Section section) {
        final SectionAddStatus sectionAddStatus = extractAddSectionStatus(section);
        if (sectionAddStatus.hasMiddleSection()) {
            return Optional.ofNullable(addMiddleSection(section, sectionAddStatus));
        }
        value.add(section);
        return Optional.empty();
    }

    private SectionAddStatus extractAddSectionStatus(final Section section) {
        validateDuplicateSection(section);
        return SectionAddStatus.from(value, section);
    }

    private void validateDuplicateSection(final Section otherSection) {
        if (checkDuplicateSection(otherSection)) {
            throw new IllegalStateException(ERROR_ALREADY_CONTAIN);
        }
    }

    private boolean checkDuplicateSection(final Section otherSection) {
        return value.stream()
            .anyMatch(section -> section.isSameUpStation(otherSection)
                && section.isSameDownStation(otherSection));
    }

    private Section addMiddleSection(final Section section, final SectionAddStatus sectionAddStatus) {
        if (sectionAddStatus == ADD_MIDDLE_FROM_UP_STATION) {
            return addMiddleSectionFromUpStation(section);
        }
        return addMiddleSectionFromDownStation(section);
    }

    private Section addMiddleSectionFromUpStation(final Section section) {
        final Section sameUpStationSection = getSameConditionStationSection(section::isSameUpStation);
        checkDistance(section, sameUpStationSection);
        return section.createMiddleToDownSection(sameUpStationSection);
    }

    private Section addMiddleSectionFromDownStation(final Section section) {
        final Section sameDownStationSection = getSameConditionStationSection(section::isSameDownStation);
        checkDistance(section, sameDownStationSection);
        return section.createUpToMiddleSection(sameDownStationSection);
    }

    public List<Section> getConnectedSections() {
        final Section firstSection = findFirstSection();
        return concatSections(firstSection, findTheRestSections(firstSection));
    }

    private List<Section> concatSections(final Section firstSection, final List<Section> theRestSections) {
        return Stream.concat(Stream.of(firstSection), theRestSections.stream())
            .collect(Collectors.toList());
    }

    private Section findFirstSection() {
        return value.stream()
            .filter(this::isFirstSection)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(ERROR_FIRST_SECTION_NOT_FOUND));
    }

    private boolean isFirstSection(final Section section) {
        return value.stream()
            .noneMatch(it -> it.isSameOppositeStation(section));
    }

    private List<Section> findTheRestSections(Section previousSection) {
        final List<Section> sections = new ArrayList<>();

        while (findNextSection(previousSection).isPresent()) {
            final Section nextSection = findNextSection(previousSection).get();
            sections.add(nextSection);
            previousSection = nextSection;
        }
        return sections;
    }

    private Optional<Section> findNextSection(final Section previousSection) {
        return value.stream()
            .filter(previousSection::isConnected)
            .findFirst();
    }

    private void checkDistance(final Section section, final Section sameStandardStationSection) {
        if (section.getDistance() >= sameStandardStationSection.getDistance()) {
            throw new IllegalStateException(ERROR_INVALID_DISTANCE);
        }
    }

    private Section getSameConditionStationSection(final Predicate<Section> sectionPredicate) {
        return value.stream()
            .filter(sectionPredicate)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(ERROR_NO_STATION));
    }

    public List<Long> getUniqueStationIds() {
        return value.stream()
            .flatMap(it -> Stream.of(it.getUpStation().getId(), it.getDownStation().getId()))
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public Long deleteSectionByStationId(final Long stationId) {
        final SectionDeleteStatus deleteSectionStatus = getDeleteSectionStatus(stationId);
        if (deleteSectionStatus == DELETE_MIDDLE) {
            return deleteMiddle(stationId);
        }
        if (deleteSectionStatus == DELETE_UP_STATION) {
            return deleteUpStation(stationId);
        }
        if (deleteSectionStatus == DELETE_DOWN_STATION) {
            return deleteDownStation(stationId);
        }
        throw new IllegalStateException("[ERROR] 해당 구간을 삭제할 수 없습니다.");
    }

    private SectionDeleteStatus getDeleteSectionStatus(final Long stationId) {
        validateDeleteSection(stationId);
        return SectionDeleteStatus.from(value, stationId);
    }

    private void validateDeleteSection(final Long stationId) {
        checkExistingStationId(stationId);
        checkOnlyDefaultSection();
    }

    private void checkExistingStationId(final Long stationId) {
        if (!containsStationId(stationId)) {
            throw new SectionNotFoundException("[ERROR] 해당 이름의 지하철역이 구간내 존재하지 않습니다.");
        }
    }

    public boolean containsStationId(final Long stationId) {
        return getUniqueStationIds().contains(stationId);
    }

    private void checkOnlyDefaultSection() {
        if (getUniqueStationIds().size() == 2) {
            throw new IllegalStateException("[ERROR] 역 2개의 기본 구간만 존재하므로 더이상 구간 삭제할 수 없습니다.");
        }
    }

    private Long deleteMiddle(final Long stationId) {
        final Section upToMiddleSection = findSectionByCondition(
            it -> Objects.equals(it.getDownStation().getId(), stationId));
        final Section middleToDownSection = findSectionByCondition(
            it -> Objects.equals(it.getUpStation().getId(), stationId));
        value.removeIf(it -> it.equals(upToMiddleSection));
        final Section updated = upToMiddleSection.createUpToDownSection(middleToDownSection);
        value.add(updated);
        value.removeIf(it -> it.equals(middleToDownSection));
        return middleToDownSection.getId();
    }

    private Long deleteUpStation(final Long stationId) {
        final Section firstSection = findSectionByCondition(it -> Objects.equals(it.getUpStation().getId(), stationId));
        value.removeIf(it -> it.equals(firstSection));
        return firstSection.getId();
    }

    private Long deleteDownStation(final Long stationId) {
        final Section lastSection = findSectionByCondition(
            it -> Objects.equals(it.getDownStation().getId(), stationId));
        value.removeIf(it -> it.equals(lastSection));
        return lastSection.getId();
    }

    public boolean isMiddleDelete(final Long stationId) {
        return getDeleteSectionStatus(stationId) == DELETE_MIDDLE;
    }

    public Section getUpdatedSection(final Sections sections) {
        return value.stream()
            .filter(section -> !sections.value.contains(section))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("[ERROR] update할 구간이 존재하지 않습니다."));
    }

    private Section findSectionByCondition(final Predicate<Section> sectionPredicate) {
        return value.stream()
            .filter(sectionPredicate)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("[ERROR] 해당하는 구간이 없습니다."));
    }

    public List<Section> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Sections{" +
            "value=" + value +
            '}';
    }
}
