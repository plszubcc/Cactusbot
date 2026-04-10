package com.google.inject.internal;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.UntargettedBinding;
import java.util.Objects;

class UntargettedBindingProcessor extends AbstractBindingProcessor {

    UntargettedBindingProcessor(Errors errors, ProcessedBindingData processedBindingData) {
        super(errors, processedBindingData);
    }

    public <T> Boolean visit(Binding<T> binding) {
        return (Boolean) binding.acceptTargetVisitor(new AbstractBindingProcessor.Processor<T, Boolean>((BindingImpl) binding) {
            public Boolean visit(UntargettedBinding<? extends T> untargetted) {
                this.prepareBinding();
                if (this.key.getAnnotationType() != null) {
                    UntargettedBindingProcessor.this.errors.missingImplementationWithHint(this.key, UntargettedBindingProcessor.this.injector);
                    UntargettedBindingProcessor.this.putBinding(UntargettedBindingProcessor.this.invalidBinding(UntargettedBindingProcessor.this.injector, this.key, this.source));
                    return true;
                } else {
                    try {
                        InjectorImpl injectorimpl = UntargettedBindingProcessor.this.injector;
                        Key key = this.key;
                        Scoping scoping = this.scoping;
                        Object object = this.source;
                        Errors errors = UntargettedBindingProcessor.this.errors;
                        ProcessedBindingData processedbindingdata = UntargettedBindingProcessor.this.processedBindingData;

                        Objects.requireNonNull(UntargettedBindingProcessor.this.processedBindingData);
                        BindingImpl<T> binding = injectorimpl.<T>createUninitializedBinding(key, scoping, object, errors, false, processedbindingdata::addCreationListener);

                        this.scheduleInitialization(binding);
                        UntargettedBindingProcessor.this.putBinding(binding);
                    } catch (ErrorsException errorsexception) {
                        UntargettedBindingProcessor.this.errors.merge(errorsexception.getErrors());
                        UntargettedBindingProcessor.this.putBinding(UntargettedBindingProcessor.this.invalidBinding(UntargettedBindingProcessor.this.injector, this.key, this.source));
                    }

                    return true;
                }
            }

            protected Boolean visitOther(Binding<? extends T> binding) {
                return false;
            }
        });
    }
}
