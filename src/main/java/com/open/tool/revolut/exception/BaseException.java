package com.open.tool.revolut.exception;

public abstract class BaseException extends Exception {

    private int status;

    public BaseException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
