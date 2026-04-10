package com.google.inject.internal;

import com.google.inject.spi.DefaultElementVisitor;
import java.util.List;

abstract class AbstractProcessor extends DefaultElementVisitor<Boolean> {

    protected Errors errors;
    protected InjectorImpl injector;

    protected AbstractProcessor(Errors errors) {
        super();
        this.errors = errors;
    }

    public void process(Iterable<InjectorShell> isolatedInjectorBuilders) {
        for (InjectorShell injectorShell : isolatedInjectorBuilders) {
            this.process(injectorShell.getInjector(), injectorShell.getElements());
        }

    }

    public void process(InjectorImpl injector, List<com.google.inject.spi.Element> elements) {
        Errors errorsAnyElement = this.errors;

        this.injector = injector;

        try {
            elements.removeIf((ex) -> {
                this.errors = errorsAnyElement.withSource(ex.getSource());
                return (Boolean) ex.acceptVisitor(this);
            });
        } finally {
            this.errors = errorsAnyElement;
            this.injector = null;
        }

    }

    protected Boolean visitOther(com.google.inject.spi.Element element) {
        return false;
    }
}
