package com.usmobile.userManagement.exception;

/**
 * Exception to be thrown when a user already exists in the system.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
