package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.AnnotatedElementBuilder;
import com.google.inject.internal.AbstractBindingBuilder;
import com.google.inject.internal.BindingBuilder;
import com.google.inject.internal.ConstantBindingBuilderImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ExposureBuilder;
import com.google.inject.internal.GuiceInternal;
import com.google.inject.internal.InternalClassesToSkipSources;
import com.google.inject.internal.InternalFlags;
import com.google.inject.internal.MoreTypes;
import com.google.inject.internal.PrivateElementsImpl;
import com.google.inject.internal.ProviderMethod;
import com.google.inject.internal.ProviderMethodsModule;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.matcher.Matcher;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;

public final class Elements {

    private static final BindingTargetVisitor<Object, Object> GET_INSTANCE_VISITOR = new DefaultBindingTargetVisitor<Object, Object>() {
        public Object visit(InstanceBinding<?> binding) {
            return binding.getInstance();
        }

        protected Object visitOther(Binding<?> binding) {
            throw new IllegalArgumentException();
        }
    };

    public Elements() {
        super();
    }

    public static List<Element> getElements(Module... modules) {
        return getElements(Stage.DEVELOPMENT, (Iterable) Arrays.asList(modules));
    }

    public static List<Element> getElements(Stage stage, Module... modules) {
        return getElements(stage, (Iterable) Arrays.asList(modules));
    }

    public static List<Element> getElements(Iterable<? extends Module> modules) {
        return getElements(Stage.DEVELOPMENT, modules);
    }

    public static List<Element> getElements(Stage stage, Iterable<? extends Module> modules) {
        Elements.RecordingBinder binder = new Elements.RecordingBinder(stage);

        for (Module module : modules) {
            binder.install(module);
        }

        binder.scanForAnnotatedMethods();

        for (Elements.RecordingBinder child : binder.privateBindersForScanning) {
            child.scanForAnnotatedMethods();
        }

        binder.permitMapConstruction.finish();
        StackTraceElements.clearCache();
        return Collections.unmodifiableList(binder.elements);
    }

    public static Binder withTrustedSource(GuiceInternal guiceInternal, Binder binder, Object source) {
        Preconditions.checkNotNull(guiceInternal);
        return (Binder) (binder instanceof Elements.RecordingBinder ? ((Elements.RecordingBinder) binder).withTrustedSource(source) : binder.withSource(source));
    }

    public static Module getModule(Iterable<? extends Element> elements) {
        return new Elements.ElementsAsModule(elements);
    }

    static <T> BindingTargetVisitor<T, T> getInstanceVisitor() {
        return Elements.GET_INSTANCE_VISITOR;
    }

    private static class ElementsAsModule implements Module {

        private final Iterable<? extends Element> elements;

        ElementsAsModule(Iterable<? extends Element> elements) {
            super();
            this.elements = elements;
        }

        public void configure(Binder binder) {
            for (Element element : this.elements) {
                element.applyTo(binder);
            }

        }
    }

    private static class ModuleInfo {

        private final ModuleSource moduleSource;
        private final boolean skipScanning;

        private ModuleInfo(ModuleSource moduleSource, boolean skipScanning) {
            super();
            this.moduleSource = moduleSource;
            this.skipScanning = skipScanning;
        }
    }

    private static class RecordingBinder implements Binder, PrivateBinder {

        private static final ImmutableSet<Class<?>> CLASSES_TO_SKIP = ImmutableSet.builder().add(new Class[]{Elements.class, Elements.RecordingBinder.class, AbstractModule.class, ConstantBindingBuilderImpl.class, AbstractBindingBuilder.class, BindingBuilder.class, MapBinder.class, Multibinder.class, OptionalBinder.class}).addAll(InternalClassesToSkipSources.classesToSkipSources()).build();
        private static final SourceProvider DEFAULT_SOURCE_PROVIDER = SourceProvider.DEFAULT_INSTANCE.plusSkippedClasses((Class[]) Elements.RecordingBinder.CLASSES_TO_SKIP.toArray(new Class[0]));
        private final Stage stage;
        private final Map<Module, Elements.ModuleInfo> modules;
        private final List<Element> elements;
        private final Object source;
        private final SourceProvider sourceProvider;
        private final Set<ModuleAnnotatedMethodScanner> scanners;
        private final Elements.RecordingBinder parent;
        private final PrivateElementsImpl privateElements;
        private final List<Elements.RecordingBinder> privateBindersForScanning;
        private final BindingSourceRestriction.PermitMapConstruction permitMapConstruction;
        private ModuleSource moduleSource;
        private ModuleAnnotatedMethodScanner scannerSource;
        private ModuleAnnotatedMethodScanner currentScanner;
        private boolean trustedSource;

        private RecordingBinder(Stage stage) {
            super();
            this.moduleSource = null;
            this.scannerSource = null;
            this.currentScanner = null;
            this.trustedSource = false;
            this.stage = stage;
            this.modules = Maps.newLinkedHashMap();
            this.scanners = Sets.newLinkedHashSet();
            this.elements = Lists.newArrayList();
            this.source = null;
            this.sourceProvider = Elements.RecordingBinder.DEFAULT_SOURCE_PROVIDER;
            this.parent = null;
            this.privateElements = null;
            this.privateBindersForScanning = Lists.newArrayList();
            this.permitMapConstruction = new BindingSourceRestriction.PermitMapConstruction();
        }

        private RecordingBinder(Elements.RecordingBinder prototype, Object source, SourceProvider sourceProvider, boolean trustedSource) {
            super();
            this.moduleSource = null;
            this.scannerSource = null;
            this.currentScanner = null;
            this.trustedSource = false;
            Preconditions.checkArgument(source == null ^ sourceProvider == null);
            this.stage = prototype.stage;
            this.modules = prototype.modules;
            this.elements = prototype.elements;
            this.scanners = prototype.scanners;
            this.currentScanner = prototype.currentScanner;
            this.source = source;
            this.trustedSource = trustedSource;
            this.moduleSource = prototype.moduleSource;
            this.sourceProvider = sourceProvider;
            this.parent = prototype.parent;
            this.privateElements = prototype.privateElements;
            this.privateBindersForScanning = prototype.privateBindersForScanning;
            this.permitMapConstruction = prototype.permitMapConstruction;
            this.scannerSource = prototype.scannerSource;
        }

        private RecordingBinder(Elements.RecordingBinder parent, PrivateElementsImpl privateElements) {
            super();
            this.moduleSource = null;
            this.scannerSource = null;
            this.currentScanner = null;
            this.trustedSource = false;
            this.stage = parent.stage;
            this.modules = Maps.newLinkedHashMap();
            this.scanners = Sets.newLinkedHashSet();
            this.currentScanner = parent.currentScanner;
            this.elements = privateElements.getElementsMutable();
            this.source = parent.source;
            this.moduleSource = parent.moduleSource;
            this.sourceProvider = parent.sourceProvider;
            this.parent = parent;
            this.privateElements = privateElements;
            this.privateBindersForScanning = parent.privateBindersForScanning;
            this.permitMapConstruction = parent.permitMapConstruction;
            this.scannerSource = parent.scannerSource;
        }

        public void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors) {
            this.elements.add(new InterceptorBinding(this.getElementSource(), classMatcher, methodMatcher, interceptors));
        }

        public void bindScope(Class<? extends Annotation> annotationType, Scope scope) {
            this.elements.add(new ScopeBinding(this.getElementSource(), annotationType, scope));
        }

        public void requestInjection(Object instance) {
            Preconditions.checkNotNull(instance, "instance");
            this.requestInjection(TypeLiteral.get(instance.getClass()), instance);
        }

        public <T> void requestInjection(TypeLiteral<T> type, T instance) {
            Preconditions.checkNotNull(instance, "instance");
            this.elements.add(new InjectionRequest(this.getElementSource(), MoreTypes.canonicalizeForKey(type), instance));
        }

        public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
            MembersInjectorLookup<T> element = new MembersInjectorLookup<T>(this.getElementSource(), MoreTypes.canonicalizeForKey(typeLiteral));

            this.elements.add(element);
            return element.getMembersInjector();
        }

        public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
            return this.getMembersInjector(TypeLiteral.get(type));
        }

        public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener) {
            this.elements.add(new TypeListenerBinding(this.getElementSource(), listener, typeMatcher));
        }

        public void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listeners) {
            this.elements.add(new ProvisionListenerBinding(this.getElementSource(), bindingMatcher, listeners));
        }

        public void requestStaticInjection(Class<?>... types) {
            for (Class<?> type : types) {
                this.elements.add(new StaticInjectionRequest(this.getElementSource(), type));
            }

        }

        void scanForAnnotatedMethods() {
            Iterable<ModuleAnnotatedMethodScanner> scanners = this.getAllScanners();

            for (Map.Entry<Module, Elements.ModuleInfo> entry : Maps.newLinkedHashMap(this.modules).entrySet()) {
                Module module = (Module) entry.getKey();
                Elements.ModuleInfo info = (Elements.ModuleInfo) entry.getValue();

                if (!info.skipScanning) {
                    for (ModuleAnnotatedMethodScanner scanner : scanners) {
                        this.currentScanner = scanner;
                        this.moduleSource = ((Elements.ModuleInfo) entry.getValue()).moduleSource;
                        this.permitMapConstruction.restoreCurrentModulePermits(this.moduleSource);

                        try {
                            this.install(ProviderMethodsModule.forModule(module, scanner));
                        } catch (RuntimeException runtimeexception) {
                            Collection<Message> messages = Errors.getMessagesFromThrowable(runtimeexception);

                            if (!messages.isEmpty()) {
                                this.elements.addAll(messages);
                            } else {
                                this.addError((Throwable) runtimeexception);
                            }
                        }
                    }
                }
            }

            this.moduleSource = null;
        }

        public void install(Module module) {
            if (!this.modules.containsKey(module)) {
                boolean customScanner = false;
                Class<?> newModuleClass = null;
                Elements.RecordingBinder binder = this;

                if (module instanceof ProviderMethodsModule) {
                    ProviderMethodsModule providerMethodsModule = (ProviderMethodsModule) module;

                    if (!providerMethodsModule.isScanningBuiltInProvidesMethods()) {
                        this.scannerSource = providerMethodsModule.getScanner();
                        customScanner = true;
                    }

                    Class<?> delegateClass = providerMethodsModule.getDelegateModuleClass();

                    if (this.moduleSource == null || !this.moduleSource.getModuleClassName().equals(delegateClass.getName())) {
                        newModuleClass = delegateClass;
                    }
                } else {
                    if (this.moduleScanning()) {
                        this.forbidNestedScannerMethods(module);
                    }

                    newModuleClass = module.getClass();
                }

                if (newModuleClass != null) {
                    this.moduleSource = this.getModuleSource(newModuleClass);
                    this.permitMapConstruction.pushModule(newModuleClass, this.moduleSource);
                }

                boolean skipScanning = false;

                if (module instanceof PrivateModule) {
                    binder = (Elements.RecordingBinder) this.newPrivateBinder();
                    binder.modules.put(module, new Elements.ModuleInfo(this.moduleSource, false));
                    skipScanning = true;
                }

                this.modules.put(module, new Elements.ModuleInfo(this.moduleSource, skipScanning));

                try {
                    module.configure(binder);
                } catch (RuntimeException runtimeexception) {
                    Collection<Message> messages = Errors.getMessagesFromThrowable(runtimeexception);

                    if (!messages.isEmpty()) {
                        this.elements.addAll(messages);
                    } else {
                        this.addError((Throwable) runtimeexception);
                    }
                }

                binder.install(ProviderMethodsModule.forModule(module));
                if (newModuleClass != null) {
                    this.moduleSource = this.moduleSource.getParent();
                    this.permitMapConstruction.popModule();
                }

                if (customScanner) {
                    this.scannerSource = null;
                }

            }
        }

        private void forbidNestedScannerMethods(Module module) {
            for (ModuleAnnotatedMethodScanner scanner : this.getAllScanners()) {
                ProviderMethodsModule providerMethodsModule = (ProviderMethodsModule) ProviderMethodsModule.forModule(module, scanner);

                for (ProviderMethod<?> method : providerMethodsModule.getProviderMethods(this)) {
                    this.addError("Scanner %s is installing a module with %s method. Installing modules with custom provides methods from a ModuleAnnotatedMethodScanner is not supported.", this.currentScanner, method.getAnnotation().annotationType().getCanonicalName());
                }
            }

        }

        private Iterable<ModuleAnnotatedMethodScanner> getAllScanners() {
            return (Iterable<ModuleAnnotatedMethodScanner>) (this.privateElements == null ? this.scanners : Iterables.concat(this.scanners, this.parent.getAllScanners()));
        }

        public Stage currentStage() {
            return this.stage;
        }

        public void addError(String message, Object... arguments) {
            this.elements.add(new Message(this.getElementSource(), Errors.format(message, arguments)));
        }

        public void addError(Throwable t) {
            String message = "An exception was caught and reported. Message: " + t.getMessage();

            this.elements.add(new Message(ImmutableList.of(this.getElementSource()), message, t));
        }

        public void addError(Message message) {
            this.elements.add(message);
        }

        public <T> AnnotatedBindingBuilder<T> bind(Key<T> key) {
            BindingBuilder<T> builder = new BindingBuilder<T>(this, this.elements, this.getElementSource(), MoreTypes.canonicalizeKey(key));

            return builder;
        }

        public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
            return this.bind(Key.get(typeLiteral));
        }

        public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
            return this.bind(Key.get(type));
        }

        public AnnotatedConstantBindingBuilder bindConstant() {
            return new ConstantBindingBuilderImpl(this, this.elements, this.getElementSource());
        }

        public <T> Provider<T> getProvider(Key<T> key) {
            return this.getProvider(Dependency.get(key));
        }

        public <T> Provider<T> getProvider(Dependency<T> dependency) {
            ProviderLookup<T> element = new ProviderLookup<T>(this.getElementSource(), dependency);

            this.elements.add(element);
            return element.getProvider();
        }

        public <T> Provider<T> getProvider(Class<T> type) {
            return this.getProvider(Key.get(type));
        }

        public void convertToTypes(Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter) {
            this.elements.add(new TypeConverterBinding(this.getElementSource(), typeMatcher, converter));
        }

        public Elements.RecordingBinder withSource(Object source) {
            return source == this.source ? this : new Elements.RecordingBinder(this, source, (SourceProvider) null, false);
        }

        public Elements.RecordingBinder withTrustedSource(Object source) {
            return source == this.source ? this : new Elements.RecordingBinder(this, source, (SourceProvider) null, true);
        }

        public Elements.RecordingBinder skipSources(Class<?>... classesToSkip) {
            if (this.source != null) {
                return this;
            } else {
                SourceProvider newSourceProvider = this.sourceProvider.plusSkippedClasses(classesToSkip);

                return new Elements.RecordingBinder(this, (Object) null, newSourceProvider, false);
            }
        }

        public PrivateBinder newPrivateBinder() {
            PrivateElementsImpl privateElements = new PrivateElementsImpl(this.getElementSource());
            Elements.RecordingBinder binder = new Elements.RecordingBinder(this, privateElements);

            this.elements.add(privateElements);
            if (!this.moduleScanning()) {
                this.privateBindersForScanning.add(binder);
            }

            return binder;
        }

        public void disableCircularProxies() {
            this.elements.add(new DisableCircularProxiesOption(this.getElementSource()));
        }

        public void requireExplicitBindings() {
            this.elements.add(new RequireExplicitBindingsOption(this.getElementSource()));
        }

        public void requireAtInjectOnConstructors() {
            this.elements.add(new RequireAtInjectOnConstructorsOption(this.getElementSource()));
        }

        public void requireExactBindingAnnotations() {
            this.elements.add(new RequireExactBindingAnnotationsOption(this.getElementSource()));
        }

        public void scanModulesForAnnotatedMethods(ModuleAnnotatedMethodScanner scanner) {
            if (this.moduleScanning()) {
                this.addError("Attempting to register ModuleAnnotatedMethodScanner %s from scanner %s. Scanners are not allowed to register other scanners.", this.currentScanner, scanner);
            } else {
                this.scanners.add(scanner);
                this.elements.add(new ModuleAnnotatedMethodScannerBinding(this.getElementSource(), scanner));
            }
        }

        public void expose(Key<?> key) {
            this.exposeInternal(key);
        }

        public AnnotatedElementBuilder expose(Class<?> type) {
            return this.exposeInternal(Key.get(type));
        }

        public AnnotatedElementBuilder expose(TypeLiteral<?> type) {
            return this.exposeInternal(Key.get(type));
        }

        private <T> AnnotatedElementBuilder exposeInternal(Key<T> key) {
            if (this.privateElements == null) {
                this.addError("Cannot expose %s on a standard binder. Exposed bindings are only applicable to private binders.", key);
                return new AnnotatedElementBuilder() {
                    public void annotatedWith(Class<? extends Annotation> annotationType) {}

                    public void annotatedWith(Annotation annotation) {}
                };
            } else {
                ExposureBuilder<T> builder = new ExposureBuilder<T>(this, this.getElementSource(), MoreTypes.canonicalizeKey(key));

                this.privateElements.addExposureBuilder(builder);
                return builder;
            }
        }

        private ModuleSource getModuleSource(Class<?> module) {
            return this.moduleSource == null ? new ModuleSource(module, this.permitMapConstruction.getPermitMap()) : this.moduleSource.createChild(module);
        }

        private ElementSource getElementSource() {
            ElementSource originalSource = null;
            Object declaringSource = this.source;

            if (declaringSource instanceof ElementSource) {
                originalSource = (ElementSource) declaringSource;
                declaringSource = originalSource.getDeclaringSource();
            }

            if (declaringSource == null) {
                InternalFlags.IncludeStackTraceOption stackTraceOption = InternalFlags.getIncludeStackTraceOption();

                if (stackTraceOption == InternalFlags.IncludeStackTraceOption.ONLY_FOR_DECLARING_SOURCE) {
                    StackTraceElement callingSource = this.sourceProvider.getCaller();

                    if (callingSource.getClassName().equals("com.google.inject.internal.InjectorShell$Builder") && callingSource.getMethodName().equals("build")) {
                        declaringSource = SourceProvider.UNKNOWN_SOURCE;
                    } else {
                        declaringSource = callingSource;
                    }
                } else {
                    declaringSource = this.sourceProvider.getFromClassNames(this.moduleSource.getModuleClassNames());
                }
            }

            return new ElementSource(originalSource, this.trustedSource, declaringSource, this.moduleSource, this.scannerSource);
        }

        private boolean moduleScanning() {
            return this.currentScanner != null;
        }

        public String toString() {
            return "Binder";
        }
    }
}
