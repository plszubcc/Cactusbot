package com.google.inject.spi;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import java.util.Set;

public final class InjectionRequest<T> implements Element {

    private final Object source;
    private final TypeLiteral<T> type;
    private final T instance;

    public InjectionRequest(Object source, TypeLiteral<T> type, T instance) {
        super();
        this.source = Preconditions.checkNotNull(source, "source");
        this.type = (TypeLiteral) Preconditions.checkNotNull(type, "type");
        this.instance = instance;
    }

    public Object getSource() {
        return this.source;
    }

    public T getInstance() {
        return this.instance;
    }

    public TypeLiteral<T> getType() {
        return this.type;
    }

    public Set<InjectionPoint> getInjectionPoints() throws ConfigurationException {
        return InjectionPoint.forInstanceMethodsAndFields(this.type);
    }

    public <R> R acceptVisitor(ElementVisitor<R> visitor) {
        return (R) visitor.visit(this);
    }

    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).requestInjection(this.type, this.instance);
    }

    public boolean equals(Object obj) {
        return obj instanceof InjectionRequest && Objects.equal(((InjectionRequest) obj).instance, this.instance) && ((InjectionRequest) obj).type.equals(this.type) && ((InjectionRequest) obj).source.equals(this.source);
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.type, this.source});
    }
}
