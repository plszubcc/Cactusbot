package com.google.inject.spi;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import java.util.Set;

public final class StaticInjectionRequest implements Element {

    private final Object source;
    private final Class<?> type;

    StaticInjectionRequest(Object source, Class<?> type) {
        super();
        this.source = Preconditions.checkNotNull(source, "source");
        this.type = (Class) Preconditions.checkNotNull(type, "type");
    }

    public Object getSource() {
        return this.source;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Set<InjectionPoint> getInjectionPoints() throws ConfigurationException {
        return InjectionPoint.forStaticMethodsAndFields(this.type);
    }

    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).requestStaticInjection(this.type);
    }

    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return (T) visitor.visit(this);
    }

    public boolean equals(Object obj) {
        return obj instanceof StaticInjectionRequest && ((StaticInjectionRequest) obj).source.equals(this.source) && ((StaticInjectionRequest) obj).type.equals(this.type);
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.source, this.type});
    }
}
