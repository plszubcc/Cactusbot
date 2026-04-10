package com.google.inject;

public interface Provider<T> extends jakarta.inject.Provider<T> {

    T get();
}
