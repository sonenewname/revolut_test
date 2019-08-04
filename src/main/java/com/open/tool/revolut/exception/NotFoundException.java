package com.open.tool.revolut.exception;

public class NotFoundException extends BaseException {

    public NotFoundException(String message, int status) {
        super(message, status);
    }
}
