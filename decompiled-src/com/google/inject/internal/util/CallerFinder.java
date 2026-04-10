package com.google.inject.internal.util;

import java.util.function.Predicate;

interface CallerFinder {

    StackTraceElement findCaller(Predicate<String> shouldBeSkipped);
}
