package com.usmobile.userManagement.exception;

/**
 * Exception to be thrown when no cycles are found for a given line.
 */
public class NoCyclesFoundException extends RuntimeException {

    public NoCyclesFoundException(String message) {
        super(message);
    }
}
