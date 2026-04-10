package com.google.inject.spi;

import com.google.inject.TypeLiteral;

public interface TypeListener {

    <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter);
}
