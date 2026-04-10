package com.google.inject.internal.util;

import java.util.function.Predicate;

class NewThrowableFinder implements CallerFinder {

    NewThrowableFinder() {
        super();
    }

    public StackTraceElement findCaller(Predicate<String> shouldBeSkipped) {
        StackTraceElement[] stackTraceElements = (new Throwable()).getStackTrace();

        for (StackTraceElement element : stackTraceElements) {
            String className = element.getClassName();

            if (!shouldBeSkipped.test(className)) {
                return element;
            }
        }

        throw new AssertionError();
    }
}
