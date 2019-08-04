package com.open.tool.revolut.exception;

public class BackendException extends BaseException {
    public BackendException(String message, int status) {
        super(message, status);
    }
}
