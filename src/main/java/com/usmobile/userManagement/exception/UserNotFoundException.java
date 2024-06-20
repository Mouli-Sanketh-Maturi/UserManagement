package com.usmobile.userManagement.exception;

/**
 * Exception to be thrown when a particular user is not found
 */
public class UserNotFoundException extends RuntimeException {

        public UserNotFoundException(String message) {
            super(message);
        }

}
