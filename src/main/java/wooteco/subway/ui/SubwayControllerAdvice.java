package wooteco.subway.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.SectionNotFoundException;
import wooteco.subway.exception.StationDuplicateException;
import wooteco.subway.exception.StationNotFoundException;
import wooteco.subway.ui.dto.response.ErrorResponse;

@RestControllerAdvice(annotations = RestController.class)
public class SubwayControllerAdvice {

    final Logger logger;

    public SubwayControllerAdvice() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ErrorResponse> handleRequestInvalidException(BindException e) {
        logger.error(getDefaultMessage(e), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(getDefaultMessage(e)));
    }

    private String getDefaultMessage(final BindException e) {
        return e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = {LineNotFoundException.class, StationNotFoundException.class,
        SectionNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = {LineDuplicateException.class, StationDuplicateException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage()));
    }

//    @ExceptionHandler(value = Exception.class)
//    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
//        logger.error(e.getMessage(), e);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//            .body(new ErrorResponse("[ERROR] 예기치 못한 에러가 발생했습니다."));
//    }
}
