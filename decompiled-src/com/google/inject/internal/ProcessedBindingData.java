package com.google.inject.internal;

import com.google.common.collect.Lists;
import java.util.List;

class ProcessedBindingData {

    private final List<CreationListener> creationListeners = Lists.newArrayList();
    private final List<Runnable> uninitializedBindings = Lists.newArrayList();
    private final List<Runnable> delayedUninitializedBindings = Lists.newArrayList();

    ProcessedBindingData() {
        super();
    }

    void addCreationListener(CreationListener listener) {
        this.creationListeners.add(listener);
    }

    void addUninitializedBinding(Runnable runnable) {
        this.uninitializedBindings.add(runnable);
    }

    void addDelayedUninitializedBinding(Runnable runnable) {
        this.delayedUninitializedBindings.add(runnable);
    }

    void initializeBindings() {
        for (Runnable initializer : this.uninitializedBindings) {
            initializer.run();
        }

    }

    void runCreationListeners(Errors errors) {
        for (CreationListener creationListener : this.creationListeners) {
            creationListener.notify(errors);
        }

    }

    void initializeDelayedBindings() {
        for (Runnable initializer : this.delayedUninitializedBindings) {
            initializer.run();
        }

    }
}
