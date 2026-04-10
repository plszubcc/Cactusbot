package com.google.inject.spi;

import com.google.inject.Provider;

public interface ProviderWithExtensionVisitor<T> extends Provider<T> {

    <B, V> V acceptExtensionVisitor(BindingTargetVisitor<B, V> visitor, ProviderInstanceBinding<? extends B> binding);
}
