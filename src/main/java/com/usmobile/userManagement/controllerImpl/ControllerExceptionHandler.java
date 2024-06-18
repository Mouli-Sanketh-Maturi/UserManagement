package com.usmobile.userManagement.controllerImpl;

import com.usmobile.userManagement.exception.UserAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle exceptions thrown by controllers
 */
@RestControllerAdvice(basePackages = "com.usmobile.userManagement.controller")
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Handle invalid controller method arguments
     *
     * @param ex the exception
     * @return the problem detail
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
     * @return the problem detail
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return problemDetail;
    }

}
