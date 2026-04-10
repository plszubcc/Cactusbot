package com.google.inject.spi;

import com.google.inject.Binder;

public interface Element {

    Object getSource();

    <T> T acceptVisitor(ElementVisitor<T> visitor);

    void applyTo(Binder binder);
}
