package name.qd.sbbet.config;

import name.qd.sbbet.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResponseEntityExceptionHandler {
	@ExceptionHandler(value = Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception e) {
		return ResponseEntity.badRequest().body(new ErrorResponse(e.toString()));
	}
}
