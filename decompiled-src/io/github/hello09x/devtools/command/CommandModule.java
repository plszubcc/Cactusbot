package io.github.hello09x.devtools.command;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import io.github.hello09x.devtools.command.exception.HandleCommandException;
import io.github.hello09x.devtools.command.exception.HandleCommandExceptionInterceptor;
import org.aopalliance.intercept.MethodInterceptor;

public class CommandModule extends AbstractModule {

    public CommandModule() {
        super();
    }

    protected void configure() {
        this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(HandleCommandException.class), new MethodInterceptor[]{new HandleCommandExceptionInterceptor()});
    }
}
