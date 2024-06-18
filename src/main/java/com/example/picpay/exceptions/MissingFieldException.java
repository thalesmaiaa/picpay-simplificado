package com.example.picpay.exceptions;

public class MissingFieldException extends RuntimeException {

    public MissingFieldException() {
        super("Missing required fields.");
    }

    public MissingFieldException(String message) {
        super(message);
    }

}
