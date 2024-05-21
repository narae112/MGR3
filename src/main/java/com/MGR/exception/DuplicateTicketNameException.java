package com.MGR.exception;

public class DuplicateTicketNameException extends RuntimeException {
    public DuplicateTicketNameException(String message) {
        super(message);
    }
}