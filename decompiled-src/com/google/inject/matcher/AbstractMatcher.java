package com.google.inject.matcher;

/** @deprecated */
@Deprecated
public abstract class AbstractMatcher<T> implements Matcher<T> {

    public AbstractMatcher() {
        super();
    }

    public Matcher<T> and(Matcher<? super T> other) {
        return Matcher.super.and(other);
    }

    public Matcher<T> or(Matcher<? super T> other) {
        return Matcher.super.or(other);
    }
}
