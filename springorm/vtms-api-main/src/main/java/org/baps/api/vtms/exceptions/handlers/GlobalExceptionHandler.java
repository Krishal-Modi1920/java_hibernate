package org.baps.api.vtms.exceptions.handlers;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.baps.api.vtms.exceptions.AuthenticationException;
import org.baps.api.vtms.exceptions.AuthorizationException;
import org.baps.api.vtms.exceptions.DataAlreadyExistsException;
import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.exceptions.DataValidationException;
import org.baps.api.vtms.exceptions.GenericException;
import org.baps.api.vtms.exceptions.InvalidCredentialsException;
import org.baps.api.vtms.models.error.Error;
import org.baps.api.vtms.models.error.ErrorType;
import org.baps.api.vtms.models.responses.ApiErrorResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@Order(value = HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String MALFORMED_JSON_REQUEST = "Malformed JSON request";
    public static final String SERVICE_UNAVAILABLE = "Service Unavailable";

    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull final MethodArgumentNotValidException ex,
            @NonNull final HttpHeaders headers, @NonNull final HttpStatus status,
            @NonNull final WebRequest request) {

        final Stream<Error.ErrorDetail> fieldErrorStream = getStreamOfErrorDetail(
                ex.getBindingResult().getFieldErrors(), ErrorType.INVALID_FIELD);
        final Stream<Error.ErrorDetail> globalErrorStream = getStreamOfErrorDetail(
                ex.getBindingResult().getGlobalErrors(), ErrorType.INVALID_DATA);

        final ApiErrorResponse errorResponse = getApiErrorResponse(
                Stream.concat(fieldErrorStream, globalErrorStream).toList());
        return new ResponseEntity<>(errorResponse, status);
    }

    /*
     * Catch the exception for validating the request body fields. The above method is not overridden, and an
     * exception is not caught. For this reason, a new method has been created.
     * Change "HttpStatus" to "HttpStatusCode" and use the "Override" keyword.
     * Can we remove unused above method?
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull final MethodArgumentNotValidException ex,
            @NonNull final HttpHeaders headers, @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request) {
        final Stream<Error.ErrorDetail> fieldErrorStream = getStreamOfErrorDetail(
                ex.getBindingResult().getFieldErrors(), ErrorType.INVALID_FIELD);
        final Stream<Error.ErrorDetail> globalErrorStream = getStreamOfErrorDetail(
                ex.getBindingResult().getGlobalErrors(), ErrorType.INVALID_DATA);
        final ApiErrorResponse errorResponse = getApiErrorResponse(
                Stream.concat(fieldErrorStream, globalErrorStream).toList());
        return new ResponseEntity<>(errorResponse, status);
    }

    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull final HttpMessageNotReadableException ex,
            @NonNull final HttpHeaders headers, @NonNull final HttpStatus status,
            @NonNull final WebRequest request) {

        final ApiErrorResponse apiErrorResponse;

        if (ex.getCause() instanceof MismatchedInputException mismatchedInputException) {
            final int extraStringInMessage = 10;
            final String errMsg = mismatchedInputException.getOriginalMessage();

            apiErrorResponse = getApiErrorResponse(
                    Error.ErrorDetail.builder().message(errMsg.substring(0, errMsg.length() - extraStringInMessage))
                    .type(ErrorType.INVALID_DATA).build());
        } else {
            apiErrorResponse = getApiErrorResponse(
                    Error.ErrorDetail.builder().message(MALFORMED_JSON_REQUEST).type(ErrorType.INVALID_DATA).build());
        }

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /*
     * Catch the exception for requestbody. The above method is not overridden, and an
     * exception is not caught. For this reason, a new method has been created.
     * Change "HttpStatus" to "HttpStatusCode" and use the "Override" keyword.
     * Can we remove unused above method?
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull final HttpMessageNotReadableException ex,
            @NonNull final HttpHeaders headers, @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request) {
        final ApiErrorResponse apiErrorResponse;
        if (ex.getCause() instanceof MismatchedInputException mismatchedInputException) {
            final int extraStringInMessage = 10;
            final String errMsg = mismatchedInputException.getOriginalMessage();
            apiErrorResponse = getApiErrorResponse(
                    Error.ErrorDetail.builder().message(errMsg.substring(0, errMsg.length() - extraStringInMessage))
                    .type(ErrorType.INVALID_DATA).build());
        } else {
            apiErrorResponse = getApiErrorResponse(
                    Error.ErrorDetail.builder().message(MALFORMED_JSON_REQUEST).type(ErrorType.INVALID_DATA).build());
        }
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex, @NonNull final HttpHeaders headers,
            @NonNull final HttpStatus status, @NonNull final WebRequest request) {
        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(ex.getMessage()).type(ErrorType.MISSING_PARAM).build());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    /*
     * Catch the exception for request param(@RequestParam). The above method is not overridden, and an
     * exception is not caught. For this reason, a new method has been created.
     * Change "HttpStatus" to "HttpStatusCode" and use the "Override" keyword.
     * Can we remove unused above method?
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex, @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status, @NonNull final WebRequest request) {
        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(ex.getMessage()).type(ErrorType.MISSING_PARAM).build());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DataAlreadyExistsException.class})
    protected ResponseEntity<ApiErrorResponse> handleValueAlreadyPresentException(final RuntimeException ex) {
        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(ex.getMessage()).type(ErrorType.DATA_ALREADY_EXIST).build());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {DataNotFoundException.class})
    protected ResponseEntity<Object> handleRecordNotFoundException(final RuntimeException ex) {
        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(ex.getMessage()).type(ErrorType.MISSING_DATA).build());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {DataValidationException.class})
    protected ResponseEntity<Object> handleValidationFailedException(final RuntimeException ex) {
        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(ex.getMessage()).type(ErrorType.INVALID_DATA).build());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    // this will handle request body validation error and shows in response
    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            final ConstraintViolationException constraintViolationException) {

        final List<Error.ErrorDetail> errorDetails = constraintViolationException.getConstraintViolations().stream()
                .flatMap(constraintViolation -> getStreamOfErrorDetail(constraintViolation, ErrorType.INVALID_DATA))
                .toList();

        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(errorDetails);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidCredentialsException.class})
    protected ResponseEntity<ApiErrorResponse> invalidCredentialsException(final RuntimeException ex) {
        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(ex.getMessage()).type(ErrorType.INVALID_CREDENTIAL).build());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<ApiErrorResponse> handleRuntimeException(final RuntimeException ex) {
        logger.fatal("Runtime Exception=" + ex.getMessage());

        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(SERVICE_UNAVAILABLE).type(ErrorType.INTERNAL_ERROR).build());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {GenericException.class})
    protected ResponseEntity<ApiErrorResponse> handleGenericException(final RuntimeException ex) {
        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(ex.getMessage()).type(ErrorType.INTERNAL_ERROR).build());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = AuthorizationException.class)
    protected ResponseEntity<ApiErrorResponse> handleAuthorizationException(final RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(value = AuthenticationException.class)
    protected ResponseEntity<ApiErrorResponse> handleAuthenticationException(final RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    //Changed because @PreAuthorize is throw AccessDeniedException.
    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<ApiErrorResponse> handleAccessDeniedExceptionn(final RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(value = JWTDecodeException.class)
    protected ResponseEntity<ApiErrorResponse> handleJWTDecodeException(final RuntimeException ex) {
        final ApiErrorResponse apiErrorResponse = getApiErrorResponse(
                Error.ErrorDetail.builder().message(ex.getMessage()).type(ErrorType.INVALID_DATA).build());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    private Stream<Error.ErrorDetail> getStreamOfErrorDetail(final ConstraintViolation<?> constraintViolation,
            final ErrorType errorType) {
        return Stream.of(Error.ErrorDetail.builder().type(errorType).message(constraintViolation.getMessage()).build());
    }

    private Stream<Error.ErrorDetail> getStreamOfErrorDetail(final List<? extends ObjectError> objectErrors,
            final ErrorType errorType) {
        return objectErrors.stream().map(objectError -> {
            if (objectError instanceof FieldError fieldError) {
                return getFieldErrorDetail(fieldError, errorType);
            } else {
                return getObjectErrorDetail(objectError, errorType);
            }
        });
    }

    private Error.ErrorDetail getFieldErrorDetail(final FieldError fieldError, final ErrorType errorType) {
        return Error.ErrorDetail.builder().field(fieldError.getField()).type(errorType)
                .message(fieldError.getDefaultMessage()).build();
    }

    private Error.ErrorDetail getObjectErrorDetail(final ObjectError objectError, final ErrorType errorType) {
        return Error.ErrorDetail.builder().type(errorType).message(objectError.getDefaultMessage()).build();
    }

    private ApiErrorResponse getApiErrorResponse(final List<Error.ErrorDetail> errorDetails) {
        return ApiErrorResponse.builder().error(Error.builder().details(errorDetails).build()).build();
    }

    private ApiErrorResponse getApiErrorResponse(final Error.ErrorDetail errorDetail) {
        return ApiErrorResponse.builder().error(Error.builder().details(Collections.singletonList(errorDetail)).build())
                .build();
    }

    // Get statusCode from MIS' response body when MIS API returns 200 as HttpStatus
    private ErrorType getErrorTypeFromStatusCode(final int statusCode) {
        if (HttpStatus.valueOf(statusCode).is4xxClientError()) {
            return ErrorType.INVALID_DATA;
        }

        return ErrorType.INTERNAL_ERROR;
    }

}
