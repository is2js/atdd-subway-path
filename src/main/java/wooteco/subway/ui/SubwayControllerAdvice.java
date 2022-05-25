package wooteco.subway.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.response.ErrorResponse;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.SectionNotFoundException;
import wooteco.subway.exception.StationDuplicateException;
import wooteco.subway.exception.StationNotFoundException;

@RestControllerAdvice(annotations = RestController.class)
public class SubwayControllerAdvice {
    //2. 공통사용을 위해 선언부를 빼놓고 -> 생성자 초기화 한다.
    //  - 필드는 현재자리에서 초기화 ㄴㄴ  필드는 no파라미터 생성자에서 초기화 or  외부주입
    //final Logger log = LoggerFactory.getLogger(getClass());
    final Logger logger;

    public SubwayControllerAdvice() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalException(Exception e) {
        //1. 특정메서드내에서 LoggerFactory의 정팩매를 통해 logger객체 log를 만들어준다.
        // - 클래스변수로 바로 선언하려고 하면 애노테이션이랑 엮여서 자동 완성 안됨.
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

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("[ERROR] 예기치 못한 에러가 발생했습니다."));
    }
}
