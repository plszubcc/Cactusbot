package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.internal.util.ContinuousStopwatch;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.spi.BindingSourceRestriction;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ModuleAnnotatedMethodScannerBinding;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProvisionListenerBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

final class InjectorShell {

    private final List<com.google.inject.spi.Element> elements;
    private final InjectorImpl injector;

    private InjectorShell(List<com.google.inject.spi.Element> elements, InjectorImpl injector) {
        super();
        this.elements = elements;
        this.injector = injector;
    }

    InjectorImpl getInjector() {
        return this.injector;
    }

    List<com.google.inject.spi.Element> getElements() {
        return this.elements;
    }

    private static void bindInjector(InjectorImpl injector) {
        Key<Injector> key = Key.get(Injector.class);
        InjectorShell.InjectorFactory injectorFactory = new InjectorShell.InjectorFactory(injector);

        injector.getBindingData().putBinding(key, new ProviderInstanceBindingImpl(injector, key, SourceProvider.UNKNOWN_SOURCE, injectorFactory, Scoping.UNSCOPED, injectorFactory, ImmutableSet.of()));
    }

    private static void bindLogger(InjectorImpl injector) {
        Key<Logger> key = Key.get(Logger.class);
        InjectorShell.LoggerFactory loggerFactory = new InjectorShell.LoggerFactory();

        injector.getBindingData().putBinding(key, new ProviderInstanceBindingImpl(injector, key, SourceProvider.UNKNOWN_SOURCE, loggerFactory, Scoping.UNSCOPED, loggerFactory, ImmutableSet.of()));
    }

    private static void bindStage(InjectorImpl injector, Stage stage) {
        Key<Stage> key = Key.get(Stage.class);
        InstanceBindingImpl<Stage> stageBinding = new InstanceBindingImpl<Stage>(injector, key, SourceProvider.UNKNOWN_SOURCE, new ConstantFactory(Initializables.of(stage)), ImmutableSet.of(), stage);

        injector.getBindingData().putBinding(key, stageBinding);
    }

    static class Builder {

        private final List<com.google.inject.spi.Element> elements = Lists.newArrayList();
        private final List<Module> modules = Lists.newArrayList();
        private InjectorBindingData bindingData;
        private InjectorJitBindingData jitBindingData;
        private InjectorImpl parent;
        private InjectorImpl.InjectorOptions options;
        private Stage stage;
        private PrivateElementsImpl privateElements;

        Builder() {
            super();
        }

        InjectorShell.Builder stage(Stage stage) {
            this.stage = stage;
            return this;
        }

        InjectorShell.Builder parent(InjectorImpl parent) {
            this.parent = parent;
            this.jitBindingData = new InjectorJitBindingData(Optional.of(parent.getJitBindingData()));
            this.bindingData = new InjectorBindingData(Optional.of(parent.getBindingData()));
            this.options = parent.options;
            this.stage = this.options.stage;
            return this;
        }

        InjectorShell.Builder privateElements(PrivateElements privateElements) {
            this.privateElements = (PrivateElementsImpl) privateElements;
            this.elements.addAll(privateElements.getElements());
            return this;
        }

        void addModules(Iterable<? extends Module> modules) {
            for (Module module : modules) {
                this.modules.add(module);
            }

        }

        Stage getStage() {
            return this.options.stage;
        }

        Object lock() {
            if (this.bindingData == null) {
                this.jitBindingData = new InjectorJitBindingData(Optional.empty());
                this.bindingData = new InjectorBindingData(Optional.empty());
            }

            return this.jitBindingData.lock();
        }

        List<InjectorShell> build(Initializer initializer, ProcessedBindingData processedBindingData, ContinuousStopwatch stopwatch, Errors errors) {
            Preconditions.checkState(this.stage != null, "Stage not initialized");
            Preconditions.checkState(this.privateElements == null || this.parent != null, "PrivateElements with no parent");
            Preconditions.checkState(this.bindingData != null, "no binding data. Did you remember to lock() ?");
            Preconditions.checkState(this.privateElements == null && this.elements.isEmpty() || this.modules.isEmpty(), "The shell is either built from modules (root) or from PrivateElements (children).");
            if (this.parent == null) {
                this.modules.add(0, new InjectorShell.RootModule());
            } else {
                this.modules.add(0, new InjectorShell.InheritedScannersModule(this.parent.getBindingData()));
            }

            this.elements.addAll(Elements.getElements(this.stage, (Iterable) this.modules));
            if (this.privateElements == null) {
                this.elements.addAll(BindingSourceRestriction.check(GuiceInternal.GUICE_INTERNAL, this.elements));
            }

            InjectorOptionsProcessor optionsProcessor = new InjectorOptionsProcessor(errors);

            optionsProcessor.process((InjectorImpl) null, this.elements);
            this.options = optionsProcessor.getOptions(this.stage, this.options);
            InjectorImpl injector = new InjectorImpl(this.parent, this.bindingData, this.jitBindingData, this.options);

            if (this.privateElements != null) {
                this.privateElements.initInjector(injector);
            }

            if (this.parent == null) {
                TypeConverterBindingProcessor.prepareBuiltInConverters(injector);
            }

            stopwatch.resetAndLog("Module execution");
            (new MessageProcessor(errors)).process(injector, this.elements);
            (new InterceptorBindingProcessor(errors)).process(injector, this.elements);
            stopwatch.resetAndLog("Interceptors creation");
            (new ListenerBindingProcessor(errors)).process(injector, this.elements);
            List<TypeListenerBinding> typeListenerBindings = injector.getBindingData().getTypeListenerBindings();

            injector.membersInjectorStore = new MembersInjectorStore(injector, typeListenerBindings);
            List<ProvisionListenerBinding> provisionListenerBindings = injector.getBindingData().getProvisionListenerBindings();

            injector.provisionListenerStore = new ProvisionListenerCallbackStore(provisionListenerBindings);
            stopwatch.resetAndLog("TypeListeners & ProvisionListener creation");
            (new ScopeBindingProcessor(errors)).process(injector, this.elements);
            stopwatch.resetAndLog("Scopes creation");
            (new TypeConverterBindingProcessor(errors)).process(injector, this.elements);
            stopwatch.resetAndLog("Converters creation");
            InjectorShell.bindStage(injector, this.stage);
            InjectorShell.bindInjector(injector);
            InjectorShell.bindLogger(injector);
            (new BindingProcessor(errors, initializer, processedBindingData)).process(injector, this.elements);
            (new UntargettedBindingProcessor(errors, processedBindingData)).process(injector, this.elements);
            stopwatch.resetAndLog("Binding creation");
            (new ModuleAnnotatedMethodScannerProcessor(errors)).process(injector, this.elements);
            stopwatch.resetAndLog("Module annotated method scanners creation");
            List<InjectorShell> injectorShells = Lists.newArrayList();

            injectorShells.add(new InjectorShell(this.elements, injector));
            PrivateElementProcessor processor = new PrivateElementProcessor(errors);

            processor.process(injector, this.elements);

            for (InjectorShell.Builder builder : processor.getInjectorShellBuilders()) {
                injectorShells.addAll(builder.build(initializer, processedBindingData, stopwatch, errors));
            }

            stopwatch.resetAndLog("Private environment creation");
            return injectorShells;
        }
    }

    private static class InjectorFactory implements InternalFactory<Injector>, Provider<Injector> {

        private final Injector injector;

        private InjectorFactory(Injector injector) {
            super();
            this.injector = injector;
        }

        public Injector get(InternalContext context, Dependency<?> dependency, boolean linked) {
            return this.injector;
        }

        public Injector get() {
            return this.injector;
        }

        public String toString() {
            return "Provider<Injector>";
        }
    }

    private static class LoggerFactory implements InternalFactory<Logger>, Provider<Logger> {

        private LoggerFactory() {
            super();
        }

        public Logger get(InternalContext context, Dependency<?> dependency, boolean linked) {
            InjectionPoint injectionPoint = dependency.getInjectionPoint();

            return injectionPoint == null ? Logger.getAnonymousLogger() : Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
        }

        public Logger get() {
            return Logger.getAnonymousLogger();
        }

        public String toString() {
            return "Provider<Logger>";
        }
    }

    private static class RootModule implements Module {

        private RootModule() {
            super();
        }

        public void configure(Binder binder) {
            binder = binder.withSource(SourceProvider.UNKNOWN_SOURCE);
            binder.bindScope(Singleton.class, Scopes.SINGLETON);
            binder.bindScope(jakarta.inject.Singleton.class, Scopes.SINGLETON);
        }
    }

    private static class InheritedScannersModule implements Module {

        private final InjectorBindingData bindingData;

        InheritedScannersModule(InjectorBindingData bindingData) {
            super();
            this.bindingData = bindingData;
        }

        public void configure(Binder binder) {
            UnmodifiableIterator unmodifiableiterator = this.bindingData.getScannerBindings().iterator();

            while (unmodifiableiterator.hasNext()) {
                ModuleAnnotatedMethodScannerBinding binding = (ModuleAnnotatedMethodScannerBinding) unmodifiableiterator.next();

                binding.applyTo(binder);
            }

        }
    }
}
