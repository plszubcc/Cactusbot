package com.google.inject.internal;

import com.google.common.base.Stopwatch;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.ContinuousStopwatch;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.TypeConverterBinding;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InternalInjectorCreator {

    private final ContinuousStopwatch stopwatch = new ContinuousStopwatch(Stopwatch.createUnstarted());
    private final Errors errors = new Errors();
    private final Initializer initializer = new Initializer();
    private final ProcessedBindingData processedBindingData;
    private final InjectionRequestProcessor injectionRequestProcessor;
    private final InjectorShell.Builder shellBuilder = new InjectorShell.Builder();
    private List<InjectorShell> shells;

    public InternalInjectorCreator() {
        super();
        this.injectionRequestProcessor = new InjectionRequestProcessor(this.errors, this.initializer);
        this.processedBindingData = new ProcessedBindingData();
    }

    public InternalInjectorCreator stage(Stage stage) {
        this.shellBuilder.stage(stage);
        return this;
    }

    public InternalInjectorCreator parentInjector(InjectorImpl parent) {
        this.shellBuilder.parent(parent);
        return this;
    }

    public InternalInjectorCreator addModules(Iterable<? extends Module> modules) {
        this.shellBuilder.addModules(modules);
        return this;
    }

    public Injector build() {
        if (this.shellBuilder == null) {
            throw new AssertionError("Already built, builders are not reusable.");
        } else {
            synchronized (this.shellBuilder.lock()) {
                this.shells = this.shellBuilder.build(this.initializer, this.processedBindingData, this.stopwatch, this.errors);
                this.stopwatch.resetAndLog("Injector construction");
                this.initializeStatically();
            }

            this.injectDynamically();
            return (Injector) (this.shellBuilder.getStage() == Stage.TOOL ? new InternalInjectorCreator.ToolStageInjector(this.primaryInjector()) : this.primaryInjector());
        }
    }

    private void initializeStatically() {
        this.processedBindingData.initializeBindings();
        this.stopwatch.resetAndLog("Binding initialization");

        for (InjectorShell shell : this.shells) {
            shell.getInjector().getBindingData().indexBindingsByType();
        }

        this.stopwatch.resetAndLog("Binding indexing");
        this.injectionRequestProcessor.process(this.shells);
        this.stopwatch.resetAndLog("Collecting injection requests");
        this.processedBindingData.runCreationListeners(this.errors);
        this.stopwatch.resetAndLog("Binding validation");
        this.injectionRequestProcessor.validate();
        this.stopwatch.resetAndLog("Static validation");
        this.initializer.validateOustandingInjections(this.errors);
        this.stopwatch.resetAndLog("Instance member validation");
        (new LookupProcessor(this.errors)).process(this.shells);

        for (InjectorShell shell : this.shells) {
            ((DeferredLookups) shell.getInjector().lookups).initialize(this.errors);
        }

        this.stopwatch.resetAndLog("Provider verification");
        this.processedBindingData.initializeDelayedBindings();
        this.stopwatch.resetAndLog("Delayed Binding initialization");

        for (InjectorShell shell : this.shells) {
            if (!shell.getElements().isEmpty()) {
                throw new AssertionError("Failed to execute " + shell.getElements());
            }
        }

        this.errors.throwCreationExceptionIfErrorsExist();
    }

    private Injector primaryInjector() {
        return ((InjectorShell) this.shells.get(0)).getInjector();
    }

    private void injectDynamically() {
        this.injectionRequestProcessor.injectMembers();
        this.stopwatch.resetAndLog("Static member injection");
        this.initializer.injectAll(this.errors);
        this.stopwatch.resetAndLog("Instance injection");
        this.errors.throwCreationExceptionIfErrorsExist();
        if (this.shellBuilder.getStage() != Stage.TOOL) {
            for (InjectorShell shell : this.shells) {
                this.loadEagerSingletons(shell.getInjector(), this.shellBuilder.getStage(), this.errors);
            }

            this.stopwatch.resetAndLog("Preloading singletons");
        }

        this.errors.throwCreationExceptionIfErrorsExist();
    }

    void loadEagerSingletons(InjectorImpl injector, Stage stage, Errors errors) {
        List<BindingImpl<?>> candidateBindings = new ArrayList();
        Collection<BindingImpl<?>> bindingsAtThisLevel = injector.getBindingData().getExplicitBindingsThisLevel().values();

        candidateBindings.addAll(bindingsAtThisLevel);
        synchronized (injector.getJitBindingData().lock()) {
            candidateBindings.addAll(injector.getJitBindingData().getJitBindings().values());
        }

        InternalContext context = injector.enterContext();

        try {
            for (BindingImpl<?> binding : candidateBindings) {
                if (this.isEagerSingleton(injector, binding, stage)) {
                    Dependency<?> dependency = Dependency.get(binding.getKey());

                    try {
                        binding.getInternalFactory().get(context, dependency, false);
                    } catch (InternalProvisionException internalprovisionexception) {
                        errors.withSource(dependency).merge(internalprovisionexception);
                    }
                }
            }
        } finally {
            context.close();
        }

    }

    private boolean isEagerSingleton(InjectorImpl injector, BindingImpl<?> binding, Stage stage) {
        if (binding.getScoping().isEagerSingleton(stage)) {
            return true;
        } else if (!(binding instanceof LinkedBindingImpl)) {
            return false;
        } else {
            Key<?> linkedBinding = ((LinkedBindingImpl) binding).getLinkedKey();

            return binding.getScoping().isNoScope() && this.isEagerSingleton(injector, injector.getBinding(linkedBinding), stage);
        }
    }

    static class ToolStageInjector implements Injector {

        private final Injector delegateInjector;

        ToolStageInjector(Injector delegateInjector) {
            super();
            this.delegateInjector = delegateInjector;
        }

        public void injectMembers(Object o) {
            throw new UnsupportedOperationException("Injector.injectMembers(Object) is not supported in Stage.TOOL");
        }

        public Map<Key<?>, Binding<?>> getBindings() {
            return this.delegateInjector.getBindings();
        }

        public Map<Key<?>, Binding<?>> getAllBindings() {
            return this.delegateInjector.getAllBindings();
        }

        public <T> Binding<T> getBinding(Key<T> key) {
            return this.delegateInjector.getBinding(key);
        }

        public <T> Binding<T> getBinding(Class<T> type) {
            return this.delegateInjector.getBinding(type);
        }

        public <T> Binding<T> getExistingBinding(Key<T> key) {
            return this.delegateInjector.<T>getExistingBinding(key);
        }

        public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
            return this.delegateInjector.findBindingsByType(type);
        }

        public Injector getParent() {
            return this.delegateInjector.getParent();
        }

        public Injector createChildInjector(Iterable<? extends Module> modules) {
            return this.delegateInjector.createChildInjector(modules);
        }

        public Injector createChildInjector(Module... modules) {
            return this.delegateInjector.createChildInjector(modules);
        }

        public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
            return this.delegateInjector.getScopeBindings();
        }

        public Set<TypeConverterBinding> getTypeConverterBindings() {
            return this.delegateInjector.getTypeConverterBindings();
        }

        public List<com.google.inject.spi.Element> getElements() {
            return this.delegateInjector.getElements();
        }

        public Map<TypeLiteral<?>, List<InjectionPoint>> getAllMembersInjectorInjectionPoints() {
            return this.delegateInjector.getAllMembersInjectorInjectionPoints();
        }

        public <T> Provider<T> getProvider(Key<T> key) {
            throw new UnsupportedOperationException("Injector.getProvider(Key<T>) is not supported in Stage.TOOL");
        }

        public <T> Provider<T> getProvider(Class<T> type) {
            throw new UnsupportedOperationException("Injector.getProvider(Class<T>) is not supported in Stage.TOOL");
        }

        public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
            throw new UnsupportedOperationException("Injector.getMembersInjector(TypeLiteral<T>) is not supported in Stage.TOOL");
        }

        public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
            throw new UnsupportedOperationException("Injector.getMembersInjector(Class<T>) is not supported in Stage.TOOL");
        }

        public <T> T getInstance(Key<T> key) {
            throw new UnsupportedOperationException("Injector.getInstance(Key<T>) is not supported in Stage.TOOL");
        }

        public <T> T getInstance(Class<T> type) {
            throw new UnsupportedOperationException("Injector.getInstance(Class<T>) is not supported in Stage.TOOL");
        }
    }
}
