package io.mmujcinovic.vaudoiseassurances.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerConfig {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerConfig.class);

    /**
     * Handles validation errors thrown when a controller method receives invalid arguments.
     * <p>
     * This handler processes {@code MethodArgumentNotValidException} by extracting
     * field-level validation errors, logging the failure with context, and returning
     * an HTTP 400 Bad Request response containing a structured error body.
     * <p>
     * The response body includes a generic error message and a list of violations,
     * each describing the field and the corresponding validation message.
     *
     * @param exception the validation exception containing binding errors
     * @param request the HTTP request that triggered the exception
     * @param handlerMethod the controller method where the validation failed
     * @return a {@code ResponseEntity} with HTTP 400 status and a body describing the validation issues
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request,
            HandlerMethod handlerMethod) {
        HttpStatus badRequestStatus = HttpStatus.BAD_REQUEST;
        List<Map<String, String>> failedFieldList = exception.getBindingResult()
                .getAllErrors().stream().map(error -> {
                    String name = (error instanceof FieldError fieldError)
                            ? fieldError.getField()
                            : error.getObjectName();
                    return Map.of("field", name,
                            "message",
                            (null == error.getDefaultMessage())
                                    ? "Validation failed" : error.getDefaultMessage());
                }).toList();
        logger.warn(
                "{} at [{} {}] in [{}#{}]. {}: {}",
                badRequestStatus.getReasonPhrase(),
                request.getMethod(),
                request.getRequestURI(),
                handlerMethod.getBeanType().getSimpleName(),
                handlerMethod.getMethod().getName(),
                "Validation failed for arguments",
                failedFieldList.stream().map(failedField -> "[" + failedField.get("field") + "] " + failedField.get("message")).collect(Collectors.joining(", ")),
                exception
        );
        Map<String, Object> bodyMap = this.bodyMap(badRequestStatus.value(),
                badRequestStatus.getReasonPhrase(), "Validation failed");
        bodyMap.put("violations", failedFieldList);
        return ResponseEntity.badRequest().body(bodyMap);
    }

    /**
     * Handles application-specific {@code BadRequestException}.
     * <p>
     * Logs request and handler context together with the technical message, then
     * returns an HTTP 400 Bad Request with a structured error body containing a
     * user-facing message.
     *
     * @param exception the thrown {@code BadRequestException}
     * @param request the HTTP request that triggered the exception
     * @param handlerMethod the controller method where the exception occurred
     * @return a {@code ResponseEntity} with HTTP 400 and a structured error body
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(
            BadRequestException exception,
            HttpServletRequest request,
            HandlerMethod handlerMethod) {
        HttpStatus badRequestStatus = HttpStatus.BAD_REQUEST;
        logger.info(
                badRequestStatus.getReasonPhrase().concat(" at [{} {}] in [{}#{}]. {}"),
                request.getMethod(),
                request.getRequestURI(),
                handlerMethod.getBeanType().getSimpleName(),
                handlerMethod.getMethod().getName(),
                exception.getMessage(),
                exception
        );
        return ResponseEntity.badRequest()
                .body(this.bodyMap(badRequestStatus.value(),
                        badRequestStatus.getReasonPhrase(),
                        exception.getUserMessage()));
    }

    /**
     * Builds a structured error response body with the given attributes.
     * <p>
     * The returned map preserves insertion order and contains three keys:
     * <ul>
     *   <li>{@code status} – the HTTP status code</li>
     *   <li>{@code error} – the error label or reason phrase</li>
     *   <li>{@code message} – a human-readable description</li>
     * </ul>
     *
     * @param status the HTTP status code
     * @param error the short error label or reason
     * @param message a descriptive message intended for the client
     * @return a {@code Map} containing the structured error response
     */
    private Map<String, Object> bodyMap(int status, String error, String message) {
        return new LinkedHashMap<>() { {
            put("status", status);
            put("error", error);
            put("message", message);
        } };
    }
}
