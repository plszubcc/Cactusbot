package com.google.inject.internal;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.ProviderLookup;

final class LookupProcessor extends AbstractProcessor {

    LookupProcessor(Errors errors) {
        super(errors);
    }

    public <T> Boolean visit(MembersInjectorLookup<T> lookup) {
        try {
            MembersInjector<T> membersInjector = this.injector.membersInjectorStore.<T>get(lookup.getType(), this.errors);

            lookup.initializeDelegate(membersInjector);
            this.injector.getBindingData().putMembersInjectorLookup(lookup);
        } catch (ErrorsException errorsexception) {
            this.errors.merge(errorsexception.getErrors());
        }

        return true;
    }

    public <T> Boolean visit(ProviderLookup<T> lookup) {
        try {
            Provider<T> provider = this.injector.<T>getProviderOrThrow(lookup.getDependency(), this.errors);

            lookup.initializeDelegate(provider);
            this.injector.getBindingData().putProviderLookup(lookup);
        } catch (ErrorsException errorsexception) {
            this.errors.merge(errorsexception.getErrors());
        }

        return true;
    }
}
