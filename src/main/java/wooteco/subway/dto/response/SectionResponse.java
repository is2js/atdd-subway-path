package wooteco.subway.dto.response;

import wooteco.subway.domain.section.Section;

public class SectionResponse {
    
    private Section section;

    private SectionResponse() {
    }

    public Section getSection() {
        return section;
    }
}
