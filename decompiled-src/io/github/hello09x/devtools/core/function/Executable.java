package io.github.hello09x.devtools.core.function;

@FunctionalInterface
public interface Executable<T, X extends Throwable> {

    X execute() throws X;
}
