package com.google.inject.multibindings;

import com.google.inject.spi.BindingTargetVisitor;

public interface MultibindingsTargetVisitor<T, V> extends BindingTargetVisitor<T, V> {

    V visit(MultibinderBinding<? extends T> multibinding);

    V visit(MapBinderBinding<? extends T> mapbinding);

    V visit(OptionalBinderBinding<? extends T> optionalbinding);
}
