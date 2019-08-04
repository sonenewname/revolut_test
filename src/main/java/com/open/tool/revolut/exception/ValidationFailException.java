package com.open.tool.revolut.exception;

public class ValidationFailException extends BaseException {


    public ValidationFailException(String message, int status) {
        super(message, status);
    }
}
