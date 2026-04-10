package io.github.hello09x.fakeplayer.core;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.hello09x.devtools.command.CommandModule;
import io.github.hello09x.devtools.core.TranslationModule;
import io.github.hello09x.devtools.core.translation.TranslationConfig;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.Exceptions;
import io.github.hello09x.devtools.database.DatabaseModule;
import io.github.hello09x.fakeplayer.core.command.CommandRegistry;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.listener.ArenaMatchListener;
import io.github.hello09x.fakeplayer.core.listener.FakeplayerLifecycleListener;
import io.github.hello09x.fakeplayer.core.listener.FakeplayerListener;
import io.github.hello09x.fakeplayer.core.listener.PlayerListener;
import io.github.hello09x.fakeplayer.core.manager.AutoTotemManager;
import io.github.hello09x.fakeplayer.core.manager.BotSoundManager;
import io.github.hello09x.fakeplayer.core.manager.CustomActionManager;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerAutofishManager;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerReplenishManager;
import io.github.hello09x.fakeplayer.core.manager.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.combat.PresetHitTracker;
import io.github.hello09x.fakeplayer.core.manager.invsee.InvseeManager;
import io.github.hello09x.fakeplayer.core.placeholder.FakeplayerPlaceholderExpansion;
import io.github.hello09x.fakeplayer.core.recording.PlayerRecordingManager;
import io.github.hello09x.fakeplayer.core.recording.RecordingReplayManager;
import io.github.hello09x.fakeplayer.core.util.update.Release;
import io.github.hello09x.fakeplayer.core.util.update.UpdateChecker;
import io.papermc.paper.plugin.configuration.PluginMeta;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import lombok.Generated;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public final class Main extends JavaPlugin {

    private static Main instance;
    private Injector injector;
    private long loadAt;

    public Main() {
        super();
    }

    public void onLoad() {
        this.loadAt = System.currentTimeMillis();
        Main.instance = this;
    }

    public void onEnable() {
        try {
            this.injector = Guice.createInjector(new FakeplayerModule(), new CommandModule(), new DatabaseModule(), new TranslationModule(new TranslationConfig("message/message", TranslatorUtils.getDefaultLocale(getInstance()))));
            ((CommandRegistry) this.injector.getInstance(CommandRegistry.class)).register();
            Messenger messenger = this.getServer().getMessenger();

            messenger.registerIncomingPluginChannel(this, "BungeeCord", (PluginMessageListener) this.injector.getInstance(WildFakeplayerManager.class));
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
            PluginManager manager = this.getServer().getPluginManager();

            manager.registerEvents((Listener) this.injector.getInstance(PlayerListener.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(FakeplayerLifecycleListener.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(FakeplayerListener.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(ArenaMatchListener.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(FakeplayerAutofishManager.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(AutoTotemManager.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(FakeplayerReplenishManager.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(PresetHitTracker.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(BotSoundManager.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(CustomActionManager.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(PlayerRecordingManager.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(RecordingReplayManager.class), this);
            manager.registerEvents((Listener) this.injector.getInstance(InvseeManager.class), this);
            FakeplayerPlaceholderExpansion placeholderExpansion = (FakeplayerPlaceholderExpansion) this.injector.getInstance(FakeplayerPlaceholderExpansion.class);

            if (placeholderExpansion != null && placeholderExpansion.register()) {
                this.getServer().getPluginManager().registerEvents(placeholderExpansion, this);
                this.getLogger().info("Successfully registered PlaceholderExpansion");
            }

            if (((FakeplayerConfig) this.injector.getInstance(FakeplayerConfig.class)).isCheckForUpdates()) {
                this.checkForUpdatesAsync();
            }

            MessageUtils.init(getInstance());
            this.getLogger().info("Registered CactusBot arena list/delete commands");
            this.getLogger().info("Crystal commands disabled");
            this.getLogger().info("Enabled in %d ms".formatted(System.currentTimeMillis() - this.loadAt));
        } catch (Throwable throwable) {
            this.getLogger().severe("Startup failure: " + throwable);
            throwable.printStackTrace();
            throw throwable;
        }
    }

    public void checkForUpdatesAsync() {
        CompletableFuture.runAsync(() -> {
            PluginMeta meta = this.getPluginMeta();
            UpdateChecker checker = new UpdateChecker("tanyaofei", "minecraft-fakeplayer");

            try {
                Release release = checker.getLastRelease();
                String current = meta.getVersion();
                String other = release.getTagName();

                if (other.charAt(0) == 'v') {
                    other = other.substring(1);
                }

                if (UpdateChecker.isNew(current, other)) {
                    Logger log = this.getLogger();

                    log.info("New version: " + release.getTagName());
                    log.info("Address: " + meta.getWebsite());
                    log.info("Update Log");

                    for (String line : release.getBody().split("\n")) {
                        log.info("\t" + line);
                    }
                }
            } catch (Throwable throwable) {
                this.getLogger().warning("Error on checking for updates: " + throwable.getMessage());
            }

        });
    }

    public void onDisable() {
        Exceptions.suppress(this, () -> {
            Messenger messenger = this.getServer().getMessenger();

            messenger.unregisterIncomingPluginChannel(this);
            messenger.unregisterOutgoingPluginChannel(this);
        });
    }

    public static @NotNull Injector getInjector() {
        return Main.instance.injector;
    }

    @Generated
    public static Main getInstance() {
        return Main.instance;
    }
}
