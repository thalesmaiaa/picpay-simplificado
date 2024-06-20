package com.example.picpay.exceptions;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException() {
        super("Erro ao tentar realizar a transação, tente novamente mais tarde");
    }

    public InvalidTransactionException(String message) {
        super(message);
    }

}
