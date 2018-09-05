package com.wordcount.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * REST API Exception Handler. It defines exception handling for: 1. Internal
 * Server Error (500) - For any Application exceptions. 2. Bad Request Error
 * (400) - For HttpMessageNotReadableException, MethodArgumentNotValidException,
 * MethodArgumentTypeMismatchException
 *
 */
@ControllerAdvice
public class AppApiExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(AppApiExceptionHandler.class);

	@ExceptionHandler({ ApplicationException.class })
	public ResponseEntity<AppApiError> handleApplicationException(ApplicationException ex, WebRequest request) {
		logger.error("Handling {}: {}", ex.getClass().getName(), ex.getMessage());
		AppApiError apiError = new AppApiError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Application Error");
		return new ResponseEntity<AppApiError>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentNotValidException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ResponseEntity<AppApiError> handleBadRequestException(RuntimeException ex) {
		logger.error("Handling {}", ex.getClass().getName());
		AppApiError apiError = new AppApiError("Invalid Request Body / Method argument(s)",
				HttpStatus.BAD_REQUEST.value(), "Bad Request");
		return new ResponseEntity<AppApiError>(apiError, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ResponseEntity<AppApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		logger.error("Handling {}", ex.getClass().getName());
		AppApiError apiError = new AppApiError(
				ex.getName() + " parameter of type " + ex.getRequiredType().getName() + " expected",
				HttpStatus.BAD_REQUEST.value(), "Bad Request");
		return new ResponseEntity<AppApiError>(apiError, HttpStatus.BAD_REQUEST);
	}

}
