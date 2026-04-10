package io.github.hello09x.devtools.core;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CoreModule extends AbstractModule {

    public CoreModule() {
        super();
    }

    @Provides
    @Singleton
    public Gson gson() {
        return new Gson();
    }
}
