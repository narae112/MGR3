package com.MGR.exception;

public class DuplicateCouponNameException extends RuntimeException{
    public DuplicateCouponNameException(String message) {
        super(message);
    }
}
