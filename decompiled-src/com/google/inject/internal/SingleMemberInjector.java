package com.google.inject.internal;

import com.google.inject.spi.InjectionPoint;

interface SingleMemberInjector {

    void inject(InternalContext context, Object o) throws InternalProvisionException;

    InjectionPoint getInjectionPoint();
}
