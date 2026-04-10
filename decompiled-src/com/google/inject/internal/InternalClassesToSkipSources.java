package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;

public final class InternalClassesToSkipSources {

    private InternalClassesToSkipSources() {
        super();
    }

    public static ImmutableSet<Class<?>> classesToSkipSources() {
        return ImmutableSet.of(RealMapBinder.class, RealMapBinder.MultimapBinder.class, RealMultibinder.class, RealOptionalBinder.class, ProviderMethodsModule.class);
    }
}
