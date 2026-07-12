package com.upload.upload_service.Exceptions;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.ServletWebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<ApiError> handleVideoNotFound(
            VideoNotFoundException exception,
            ServletWebRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            ServletWebRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));

        return error(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                request,
                fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            ServletWebRequest request) {
        return error(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or malformed",
                request,
                Map.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception,
            ServletWebRequest request) {
        return error(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type must be application/json for this endpoint",
                request,
                Map.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            ServletWebRequest request) {
        return error(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter " + exception.getName(),
                request,
                Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            ServletWebRequest request) {
        log.warn("Database constraint violation for {}", request.getRequest().getRequestURI(), exception);
        return error(
                HttpStatus.CONFLICT,
                "The video metadata conflicts with existing data",
                request,
                Map.of());
    }

    @ExceptionHandler({
            DataAccessResourceFailureException.class,
            TransientDataAccessResourceException.class
    })
    public ResponseEntity<ApiError> handleDatabaseUnavailable(
            Exception exception,
            ServletWebRequest request) {
        log.error("Database is unavailable for {}", request.getRequest().getRequestURI(), exception);
        return error(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Database is currently unavailable",
                request,
                Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception,
            ServletWebRequest request) {
        log.error("Unexpected error for {}", request.getRequest().getRequestURI(), exception);
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request,
                Map.of());
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            String message,
            ServletWebRequest request,
            Map<String, String> fieldErrors) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequest().getRequestURI(),
                fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
