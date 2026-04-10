package com.google.inject.internal;

import com.google.inject.spi.InterceptorBinding;

final class InterceptorBindingProcessor extends AbstractProcessor {

    InterceptorBindingProcessor(Errors errors) {
        super(errors);
    }

    public Boolean visit(InterceptorBinding command) {
        if (InternalFlags.isBytecodeGenEnabled()) {
            this.injector.getBindingData().addInterceptorBinding(command);
        } else {
            this.errors.aopDisabled(command);
        }

        return true;
    }
}
