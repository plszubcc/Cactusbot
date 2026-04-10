package io.github.hello09x.devtools.core.utils;

import org.bukkit.Bukkit;

public abstract class Assertion {

    public Assertion() {
        super();
    }

    public void assertPrimaryThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Not in primary thread");
        }
    }
}
