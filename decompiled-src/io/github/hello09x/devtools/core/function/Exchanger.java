package io.github.hello09x.devtools.core.function;

@FunctionalInterface
public interface Exchanger<T, R, X extends Exception> {

    R exchange(T t0) throws X;
}
