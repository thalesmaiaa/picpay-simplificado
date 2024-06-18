package com.example.picpay.exceptions;

public class InvalidSenderUserException extends RuntimeException {

    public InvalidSenderUserException() {
        super("Invalid Sender User type");
    }

    public InvalidSenderUserException(String message) {
        super(message);
    }

}
