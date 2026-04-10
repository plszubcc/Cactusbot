package io.github.hello09x.fakeplayer.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.translation.PluginTranslator;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerList;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.manager.invsee.InvseeManager;
import io.github.hello09x.fakeplayer.core.manager.invsee.OpenInvInvseeManagerImpl;
import io.github.hello09x.fakeplayer.core.manager.invsee.SimpleInvseeManagerImpl;
import io.github.hello09x.fakeplayer.core.placeholder.FakeplayerPlaceholderExpansion;
import io.github.hello09x.fakeplayer.core.placeholder.FakeplayerPlaceholderExpansionImpl;
import io.github.hello09x.fakeplayer.core.util.ClassUtils;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeplayerModule extends AbstractModule {

    private static final Logger log = Main.getInstance().getLogger();

    public FakeplayerModule() {
        super();
    }

    protected void configure() {
        super.bind(Plugin.class).toInstance(Main.getInstance());
    }

    @Provides
    @Singleton
    public @NotNull InvseeManager invseeManager(FakeplayerConfig config, FakeplayerManager fakeplayerManager, FakeplayerList fakeplayerList) {
        Object object;

        switch (config.getInvseeImplement()) {
            case SIMPLE:
                object = new SimpleInvseeManagerImpl(fakeplayerManager, fakeplayerList);
                break;
            case AUTO:
                if (Bukkit.getPluginManager().isPluginEnabled("OpenInv") && ClassUtils.isClassExists("com.lishid.openinv.IOpenInv")) {
                    FakeplayerModule.log.info("Using OpenInv as invsee implement");
                    object = new OpenInvInvseeManagerImpl(fakeplayerManager, fakeplayerList);
                } else {
                    FakeplayerModule.log.info("Using simple invsee implement");
                    object = new SimpleInvseeManagerImpl(fakeplayerManager, fakeplayerList);
                }
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return (InvseeManager) object;
    }

    @Provides
    @Singleton
    private @NotNull NMSBridge nmsBridge() {
        NMSBridge bridge = (NMSBridge) ServiceLoader.load(NMSBridge.class, NMSBridge.class.getClassLoader()).stream().map(Provider::get).filter(NMSBridge::isSupported).findAny().orElse((Object) null);

        return bridge;
    }

    @Singleton
    @Provides
    private @Nullable FakeplayerPlaceholderExpansion fakeplayerPlaceholderExpansion(FakeplayerManager fakeplayerManager, ActionManager actionManager, PluginTranslator translator) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && ClassUtils.isClassExists("me.clip.placeholderapi.expansion.PlaceholderExpansion") ? new FakeplayerPlaceholderExpansionImpl(fakeplayerManager, actionManager, translator) : null;
    }
}
