package com.google.inject.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.Scope;
import com.google.inject.internal.Errors;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.Elements;
import com.google.inject.spi.ModuleAnnotatedMethodScannerBinding;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ScopeBinding;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CheckReturnValue
public final class Modules {

    public static final Module EMPTY_MODULE = new Modules.EmptyModule();

    private Modules() {
        super();
    }

    public static Modules.OverriddenModuleBuilder override(Module... modules) {
        return override((Iterable) Arrays.asList(modules));
    }

    /** @deprecated */
    @Deprecated
    public static Modules.OverriddenModuleBuilder override() {
        return override((Iterable) Arrays.asList());
    }

    public static Modules.OverriddenModuleBuilder override(Iterable<? extends Module> modules) {
        return new Modules.RealOverriddenModuleBuilder(modules);
    }

    public static Module combine(Module... modules) {
        return combine((Iterable) ImmutableSet.copyOf(modules));
    }

    /** @deprecated */
    @Deprecated
    public static Module combine(Module module) {
        return module;
    }

    /** @deprecated */
    @Deprecated
    public static Module combine() {
        return Modules.EMPTY_MODULE;
    }

    public static Module combine(Iterable<? extends Module> modules) {
        return new Modules.CombinedModule(modules);
    }

    private static Module extractScanners(Iterable<Element> elements) {
        final List<ModuleAnnotatedMethodScannerBinding> scanners = Lists.newArrayList();
        ElementVisitor<Void> visitor = new DefaultElementVisitor<Void>() {
            public Void visit(ModuleAnnotatedMethodScannerBinding binding) {
                scanners.add(binding);
                return null;
            }
        };

        for (Element element : elements) {
            element.acceptVisitor(visitor);
        }

        return new AbstractModule() {
            protected void configure() {
                for (ModuleAnnotatedMethodScannerBinding scanner : scanners) {
                    scanner.applyTo(this.binder());
                }

            }
        };
    }

    public static Module requireExplicitBindingsModule() {
        return new Modules.RequireExplicitBindingsModule();
    }

    public static Module requireAtInjectOnConstructorsModule() {
        return new Modules.RequireAtInjectOnConstructorsModule();
    }

    public static Module requireExactBindingAnnotationsModule() {
        return new Modules.RequireExactBindingAnnotationsModule();
    }

    public static Module disableCircularProxiesModule() {
        return new Modules.DisableCircularProxiesModule();
    }

    private static class EmptyModule implements Module {

        private EmptyModule() {
            super();
        }

        public void configure(Binder binder) {}
    }

    private static class CombinedModule implements Module {

        final Set<Module> modulesSet;

        CombinedModule(Iterable<? extends Module> modules) {
            super();
            this.modulesSet = ImmutableSet.copyOf(modules);
        }

        public void configure(Binder binder) {
            binder = binder.skipSources(this.getClass());

            for (Module module : this.modulesSet) {
                binder.install(module);
            }

        }
    }

    private static final class RealOverriddenModuleBuilder implements Modules.OverriddenModuleBuilder {

        private final ImmutableSet<Module> baseModules;

        private RealOverriddenModuleBuilder(Iterable<? extends Module> baseModules) {
            super();
            this.baseModules = ImmutableSet.copyOf(baseModules);
        }

        public Module with(Module... overrides) {
            return this.with((Iterable) Arrays.asList(overrides));
        }

        public Module with() {
            return this.with((Iterable) Arrays.asList());
        }

        public Module with(Iterable<? extends Module> overrides) {
            return new Modules.OverrideModule(overrides, this.baseModules);
        }
    }

    static class OverrideModule extends AbstractModule {

        private final ImmutableSet<Module> overrides;
        private final ImmutableSet<Module> baseModules;

        OverrideModule(Iterable<? extends Module> overrides, ImmutableSet<Module> baseModules) {
            super();
            this.overrides = ImmutableSet.copyOf(overrides);
            this.baseModules = baseModules;
        }

        public void configure() {
            Binder baseBinder = this.binder();
            List<Element> baseElements = Elements.getElements(this.currentStage(), (Iterable) this.baseModules);

            if (baseElements.size() == 1) {
                Element element = (Element) Iterables.getOnlyElement(baseElements);

                if (element instanceof PrivateElements) {
                    PrivateElements privateElements = (PrivateElements) element;
                    PrivateBinder privateBinder = baseBinder.newPrivateBinder().withSource(privateElements.getSource());

                    for (Key<?> exposed : privateElements.getExposedKeys()) {
                        privateBinder.withSource(privateElements.getExposedSource(exposed)).expose(exposed);
                    }

                    baseBinder = privateBinder;
                    baseElements = privateElements.getElements();
                }
            }

            Binder binder = baseBinder.skipSources(this.getClass());
            ImmutableSet<Element> elements = ImmutableSet.copyOf(baseElements);
            Module scannersModule = Modules.extractScanners(elements);
            List<Element> overrideElements = Elements.getElements(this.currentStage(), (Iterable) ImmutableList.builder().addAll(this.overrides).add(scannersModule).build());
            final Set<Key<?>> overriddenKeys = Sets.newHashSet();
            final Map<Class<? extends Annotation>, ScopeBinding> overridesScopeAnnotations = Maps.newHashMap();

            (new Modules.ModuleWriter(binder) {
                public <T> Void visit(Binding<T> binding) {
                    overriddenKeys.add(binding.getKey());
                    return (Void) super.visit(binding);
                }

                public Void visit(ScopeBinding scopeBinding) {
                    overridesScopeAnnotations.put(scopeBinding.getAnnotationType(), scopeBinding);
                    return (Void) super.visit(scopeBinding);
                }

                public Void visit(PrivateElements privateElements) {
                    overriddenKeys.addAll(privateElements.getExposedKeys());
                    return (Void) super.visit(privateElements);
                }
            }).writeAll(overrideElements);
            final Map<Scope, List<Object>> scopeInstancesInUse = Maps.newHashMap();
            final List<ScopeBinding> scopeBindings = Lists.newArrayList();

            (new Modules.ModuleWriter(binder) {
                public <T> Void visit(Binding<T> binding) {
                    if (!overriddenKeys.remove(binding.getKey())) {
                        super.visit(binding);
                        Scope scope = OverrideModule.this.getScopeInstanceOrNull(binding);

                        if (scope != null) {
                            ((List) scopeInstancesInUse.computeIfAbsent(scope, (kx) -> {
                                return Lists.newArrayList();
                            })).add(binding.getSource());
                        }
                    }

                    return null;
                }

                void rewrite(Binder binder, PrivateElements privateElements, Set<Key<?>> keysToSkip) {
                    PrivateBinder privateBinder = binder.withSource(privateElements.getSource()).newPrivateBinder();
                    Set<Key<?>> skippedExposes = Sets.newHashSet();

                    for (Key<?> key : privateElements.getExposedKeys()) {
                        if (keysToSkip.remove(key)) {
                            skippedExposes.add(key);
                        } else {
                            privateBinder.withSource(privateElements.getExposedSource(key)).expose(key);
                        }
                    }

                    for (Element element : privateElements.getElements()) {
                        if (!(element instanceof Binding) || !skippedExposes.remove(((Binding) element).getKey())) {
                            if (element instanceof PrivateElements) {
                                this.rewrite(privateBinder, (PrivateElements) element, skippedExposes);
                            } else {
                                element.applyTo(privateBinder);
                            }
                        }
                    }

                }

                public Void visit(PrivateElements privateElements) {
                    this.rewrite(this.binder, privateElements, overriddenKeys);
                    return null;
                }

                public Void visit(ScopeBinding scopeBinding) {
                    scopeBindings.add(scopeBinding);
                    return null;
                }
            }).writeAll(elements);
            (new Modules.ModuleWriter(binder) {
                public Void visit(ScopeBinding scopeBinding) {
                    ScopeBinding overideBinding = (ScopeBinding) overridesScopeAnnotations.remove(scopeBinding.getAnnotationType());

                    if (overideBinding == null) {
                        super.visit(scopeBinding);
                    } else {
                        List<Object> usedSources = (List) scopeInstancesInUse.get(scopeBinding.getScope());

                        if (usedSources != null) {
                            StringBuilder sb = new StringBuilder("The scope for @%s is bound directly and cannot be overridden.");

                            sb.append("\n     original binding at " + Errors.convert(scopeBinding.getSource()));

                            for (Object usedSource : usedSources) {
                                sb.append("\n     bound directly at " + Errors.convert(usedSource) + "");
                            }

                            this.binder.withSource(overideBinding.getSource()).addError(sb.toString(), scopeBinding.getAnnotationType().getSimpleName());
                        }
                    }

                    return null;
                }
            }).writeAll(scopeBindings);
        }

        private Scope getScopeInstanceOrNull(Binding<?> binding) {
            return (Scope) binding.acceptScopingVisitor(new DefaultBindingScopingVisitor<Scope>() {
                public Scope visitScope(Scope scope) {
                    return scope;
                }
            });
        }
    }

    private static class ModuleWriter extends DefaultElementVisitor<Void> {

        protected final Binder binder;

        ModuleWriter(Binder binder) {
            super();
            this.binder = binder.skipSources(this.getClass());
        }

        protected Void visitOther(Element element) {
            element.applyTo(this.binder);
            return null;
        }

        void writeAll(Iterable<? extends Element> elements) {
            for (Element element : elements) {
                element.acceptVisitor(this);
            }

        }
    }

    private static final class RequireExplicitBindingsModule implements Module {

        private RequireExplicitBindingsModule() {
            super();
        }

        public void configure(Binder binder) {
            binder.requireExplicitBindings();
        }
    }

    private static final class RequireAtInjectOnConstructorsModule implements Module {

        private RequireAtInjectOnConstructorsModule() {
            super();
        }

        public void configure(Binder binder) {
            binder.requireAtInjectOnConstructors();
        }
    }

    private static final class RequireExactBindingAnnotationsModule implements Module {

        private RequireExactBindingAnnotationsModule() {
            super();
        }

        public void configure(Binder binder) {
            binder.requireExactBindingAnnotations();
        }
    }

    private static final class DisableCircularProxiesModule implements Module {

        private DisableCircularProxiesModule() {
            super();
        }

        public void configure(Binder binder) {
            binder.disableCircularProxies();
        }
    }

    public interface OverriddenModuleBuilder {

        Module with(Module... overrides);

        /** @deprecated */
        @Deprecated
        Module with();

        Module with(Iterable<? extends Module> overrides);
    }
}
