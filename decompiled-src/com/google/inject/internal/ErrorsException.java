package com.google.inject.internal;

public class ErrorsException extends Exception {

    private final Errors errors;

    public ErrorsException(Errors errors) {
        super();
        this.errors = errors;
    }

    public Errors getErrors() {
        return this.errors;
    }
}
