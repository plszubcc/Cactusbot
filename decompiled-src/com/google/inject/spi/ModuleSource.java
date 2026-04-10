package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;

final class ModuleSource {

    private final String moduleClassName;
    private final ModuleSource parent;
    private final BindingSourceRestriction.PermitMap permitMap;

    ModuleSource(Class<?> moduleClass, BindingSourceRestriction.PermitMap permitMap) {
        this((ModuleSource) null, moduleClass, permitMap);
    }

    private ModuleSource(@Nullable ModuleSource parent, Class<?> moduleClass, BindingSourceRestriction.PermitMap permitMap) {
        super();
        Preconditions.checkNotNull(moduleClass, "module cannot be null.");
        this.parent = parent;
        this.moduleClassName = moduleClass.getName();
        this.permitMap = permitMap;
    }

    String getModuleClassName() {
        return this.moduleClassName;
    }

    ModuleSource createChild(Class<?> moduleClass) {
        return new ModuleSource(this, moduleClass, this.permitMap);
    }

    ModuleSource getParent() {
        return this.parent;
    }

    List<String> getModuleClassNames() {
        ImmutableList.Builder<String> classNames = ImmutableList.builder();

        for (ModuleSource current = this; current != null; current = current.parent) {
            String className = current.moduleClassName;

            classNames.add(className);
        }

        return classNames.build();
    }

    int size() {
        return this.parent == null ? 1 : this.parent.size() + 1;
    }

    BindingSourceRestriction.PermitMap getPermitMap() {
        return this.permitMap;
    }
}
