package wooteco.subway.ui.dto.response;

public class ErrorResponse {

    private String message;

    private ErrorResponse() {
    }

    public ErrorResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
