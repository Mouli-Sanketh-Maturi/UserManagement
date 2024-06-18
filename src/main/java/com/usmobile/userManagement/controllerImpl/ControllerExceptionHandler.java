package com.usmobile.userManagement.controllerImpl;

import com.usmobile.userManagement.exception.NoCyclesFoundException;
import com.usmobile.userManagement.exception.UserAlreadyExistsException;
import com.usmobile.userManagement.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle exceptions thrown by controllers
 */
@RestControllerAdvice(basePackages = "com.usmobile.userManagement.controllerImpl")
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Handle invalid controller method arguments
     *
     * @param ex the exception
     * @return BAD_REQUEST problem detail
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ProblemDetail handleValidationExceptions(BindException ex) {
        logger.error("Validation failed", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            logger.error("Validation error: {} - {}", fieldName, errorMessage);
            errors.put(fieldName, errorMessage);
        });
        problemDetail.setProperties(errors);
        return problemDetail;
    }

    /**
     * Handle User Already Exists Exception
     *
     * @param ex the exception
     * @return BAD_REQUEST problem detail
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.error("User already exists", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return problemDetail;
    }

    /**
     * Handle missing required request parameter
     *
     * @param ex the exception
     * @return BAD_REQUEST problem detail
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleValidationExceptions(MissingServletRequestParameterException ex) {
        logger.error("Missing required Servlet request parameter", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return problemDetail;
    }

    /**
     * Handle No Cycles Found Exception
     *
     * @param ex the exception
     * @return NOT_FOUND problem detail
     */
    @ExceptionHandler(NoCyclesFoundException.class)
    public ProblemDetail handleNoCurrentCycleException(NoCyclesFoundException ex) {
        logger.error("No cycles found", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return problemDetail;
    }

    /**
     * Handle User Not Found Exception
     *
     * @param ex the exception
     * @return NOT_FOUND problem detail
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return problemDetail;
    }

    /**
     * Handle Any Exception
     *
     * @param ex the exception
     * @return INTERNAL_SERVER_ERROR problem detail
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAnyException(Exception ex) {
        logger.error("Internal server error", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error, please try again later");
        return problemDetail;
    }

}
