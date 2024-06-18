package com.example.picpay.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidSenderUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<ExceptionMessage> InvalidSenderHandler(InvalidSenderUserException exception) {
        ExceptionMessage messages = new ExceptionMessage(404, exception.getMessage());
        return ResponseEntity.status(400).body(messages);
    }

    @ExceptionHandler(MissingFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<ExceptionMessage> missingFieldHandler(MissingFieldException exception) {
        ExceptionMessage messages = new ExceptionMessage(400, exception.getMessage());
        return ResponseEntity.status(400).body(messages);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ResponseEntity<ExceptionMessage> userNotFoundHandler(UserNotFoundException exception) {
        ExceptionMessage messages = new ExceptionMessage(404, exception.getMessage());
        return ResponseEntity.status(404).body(messages);
    }


}
