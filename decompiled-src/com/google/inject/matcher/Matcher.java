package com.google.inject.matcher;

public interface Matcher<T> {

    boolean matches(T t);

    default Matcher<T> and(Matcher<? super T> other) {
        return new Matchers.AndMatcher<T>(this, other);
    }

    default Matcher<T> or(Matcher<? super T> other) {
        return new Matchers.OrMatcher<T>(this, other);
    }
}
