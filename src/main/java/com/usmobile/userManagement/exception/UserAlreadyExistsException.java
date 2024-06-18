package com.usmobile.userManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
