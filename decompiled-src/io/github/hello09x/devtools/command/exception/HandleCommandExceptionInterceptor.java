package io.github.hello09x.devtools.command.exception;

import net.kyori.adventure.audience.Audience;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.Nullable;

public class HandleCommandExceptionInterceptor implements MethodInterceptor {

    public HandleCommandExceptionInterceptor() {
        super();
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!invocation.getMethod().getReturnType().equals(Void.TYPE)) {
            return invocation.proceed();
        } else {
            Object[] args = invocation.getArguments();

            try {
                return invocation.proceed();
            } catch (CommandException commandexception) {
                Audience audience = this.findAudience(args);

                if (audience != null) {
                    audience.sendMessage(commandexception.component());
                    return null;
                } else {
                    throw commandexception;
                }
            }
        }
    }

    private @Nullable Audience findAudience(@Nullable Object @Nullable [] args) {
        if (args == null) {
            return null;
        } else {
            for (Object arg : args) {
                if (arg instanceof Audience) {
                    Audience a = (Audience) arg;

                    return a;
                }
            }

            return null;
        }
    }
}
