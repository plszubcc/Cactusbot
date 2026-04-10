package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Stage;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.StaticInjectionRequest;
import java.util.List;
import java.util.Set;

final class InjectionRequestProcessor extends AbstractProcessor {

    private final List<InjectionRequestProcessor.StaticInjection> staticInjections = Lists.newArrayList();
    private final Initializer initializer;

    InjectionRequestProcessor(Errors errors, Initializer initializer) {
        super(errors);
        this.initializer = initializer;
    }

    public Boolean visit(StaticInjectionRequest request) {
        this.staticInjections.add(new InjectionRequestProcessor.StaticInjection(this.injector, request));
        this.injector.getBindingData().putStaticInjectionRequest(request);
        return true;
    }

    public Boolean visit(InjectionRequest<?> request) {
        Set<InjectionPoint> injectionPoints;

        try {
            injectionPoints = request.getInjectionPoints();
        } catch (ConfigurationException configurationexception) {
            this.errors.merge(configurationexception.getErrorMessages());
            injectionPoints = (Set) configurationexception.getPartialValue();
        }

        this.requestInjection(request, injectionPoints, this.errors);
        this.injector.getBindingData().putInjectionRequest(new InjectionRequest(request.getSource(), request.getType(), (Object) null));
        return true;
    }

    private <T> void requestInjection(InjectionRequest<T> request, Set<InjectionPoint> injectionPoints, Errors errors) {
        this.initializer.requestInjection(this.injector, request.getType(), request.getInstance(), (Binding) null, request.getSource(), injectionPoints, errors);
    }

    void validate() {
        for (InjectionRequestProcessor.StaticInjection staticInjection : this.staticInjections) {
            staticInjection.validate();
        }

    }

    void injectMembers() {
        for (InjectionRequestProcessor.StaticInjection staticInjection : this.staticInjections) {
            staticInjection.injectMembers();
        }

    }

    private class StaticInjection {

        final InjectorImpl injector;
        final Object source;
        final StaticInjectionRequest request;
        ImmutableList<SingleMemberInjector> memberInjectors;

        public StaticInjection(InjectorImpl injector, StaticInjectionRequest request) {
            super();
            this.injector = injector;
            this.source = request.getSource();
            this.request = request;
        }

        void validate() {
            Errors errorsForMember = InjectionRequestProcessor.this.errors.withSource(this.source);

            Set<InjectionPoint> injectionPoints;

            try {
                injectionPoints = this.request.getInjectionPoints();
            } catch (ConfigurationException configurationexception) {
                errorsForMember.merge(configurationexception.getErrorMessages());
                injectionPoints = (Set) configurationexception.getPartialValue();
            }

            if (injectionPoints != null) {
                this.memberInjectors = this.injector.membersInjectorStore.getInjectors(injectionPoints, errorsForMember);
            } else {
                this.memberInjectors = ImmutableList.of();
            }

            InjectionRequestProcessor.this.errors.merge(errorsForMember);
        }

        void injectMembers() {
            InternalContext context = this.injector.enterContext();

            try {
                boolean isStageTool = this.injector.options.stage == Stage.TOOL;
                UnmodifiableIterator unmodifiableiterator = this.memberInjectors.iterator();

                while (unmodifiableiterator.hasNext()) {
                    SingleMemberInjector memberInjector = (SingleMemberInjector) unmodifiableiterator.next();

                    if (!isStageTool || memberInjector.getInjectionPoint().isToolable()) {
                        try {
                            memberInjector.inject(context, (Object) null);
                        } catch (InternalProvisionException internalprovisionexception) {
                            InjectionRequestProcessor.this.errors.merge(internalprovisionexception);
                        }
                    }
                }
            } finally {
                context.close();
            }

        }
    }
}
