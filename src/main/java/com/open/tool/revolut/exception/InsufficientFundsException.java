package com.open.tool.revolut.exception;

public class InsufficientFundsException extends BaseException {

    public InsufficientFundsException(String message, int status) {
        super(message, status);
    }
}
