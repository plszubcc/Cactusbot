package org.aopalliance.aop;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AspectException extends RuntimeException {

    private String message;
    private String stackTrace;
    private Throwable t;

    public Throwable getCause() {
        return this.t;
    }

    public String toString() {
        return this.getMessage();
    }

    public String getMessage() {
        return this.message;
    }

    public void printStackTrace() {
        System.err.print(this.stackTrace);
    }

    public void printStackTrace(PrintStream printstream) {
        this.printStackTrace(new PrintWriter(printstream));
    }

    public void printStackTrace(PrintWriter printwriter) {
        printwriter.print(this.stackTrace);
    }

    public AspectException(String s) {
        super(s);
        this.message = s;
        this.stackTrace = s;
    }

    public AspectException(String s, Throwable throwable) {
        super(s + "; nested exception is " + throwable.getMessage());
        this.t = throwable;
        StringWriter stringwriter = new StringWriter();

        throwable.printStackTrace(new PrintWriter(stringwriter));
        this.stackTrace = stringwriter.toString();
    }
}
