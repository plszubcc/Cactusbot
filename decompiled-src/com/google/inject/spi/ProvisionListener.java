package com.google.inject.spi;

import com.google.inject.Binding;

public interface ProvisionListener {

    <T> void onProvision(ProvisionListener.ProvisionInvocation<T> provision);

    public abstract static class ProvisionInvocation<T> {

        public ProvisionInvocation() {
            super();
        }

        public abstract Binding<T> getBinding();

        public abstract T provision();
    }
}
