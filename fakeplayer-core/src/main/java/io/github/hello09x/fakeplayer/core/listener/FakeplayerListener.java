package io.github.hello09x.fakeplayer.core.listener;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.devtools.core.utils.Exceptions;
import io.github.hello09x.devtools.core.utils.MetadataUtils;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.FakeplayerProfileRepository;
import io.github.hello09x.fakeplayer.core.repository.UsedIdRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

@Singleton
public class FakeplayerListener implements Listener {

    private static final Logger log = Main.getInstance().getLogger();
    private final FakeplayerManager manager;
    private final UsedIdRepository usedIdRepository;
    private final FakeplayerProfileRepository profileRepository;
    private final FakeplayerConfig config;

    @Inject
    public FakeplayerListener(FakeplayerManager manager, UsedIdRepository usedIdRepository, FakeplayerProfileRepository profileRepository, FakeplayerConfig config) {
        super();
        this.manager = manager;
        this.usedIdRepository = usedIdRepository;
        this.profileRepository = profileRepository;
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void disallowUsedUUIDLogin(@NotNull PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!player.hasMetadata("fakeplayer:spawned_at")) {
            if (this.usedIdRepository.contains(player.getUniqueId()) || this.profileRepository.existsByUUID(player.getUniqueId())) {
                event.disallow(Result.KICK_OTHER, Component.textOfChildren(new ComponentLike[]{Component.translatable("fakeplayer.listener.login.deny-used-uuid", NamedTextColor.RED), Component.newline(), Component.newline(), Component.text("<<---- fakeplayer ---->>", NamedTextColor.GRAY)}));
                FakeplayerListener.log.info("%s(%s) was disallowed to login because his UUID was used by [Fakeplayer]".formatted(player.getName(), player.getUniqueId()));
            }

        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void preventKicking(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (!this.manager.isNotFake(event.getPlayer())) {
            if (!ComponentUtils.toString(event.reason()).startsWith("[fakeplayer] ")) {
                switch (this.config.getPreventKicking()) {
                    case ON_SPAWNING:
                        Integer spawnAt = (Integer) MetadataUtils.find(Main.getInstance(), player, "fakeplayer:spawned_at", Integer.class).map(MetadataValue::asInt).orElse((Object) null);

                        if (spawnAt != null && Bukkit.getCurrentTick() - spawnAt < 20) {
                            event.setCancelled(true);
                            FakeplayerListener.log.warning(String.format("Canceled kicking fake player '%s' on spawning due to your configuration", player.getName()));
                        }
                        break;
                    case ALWAYS:
                        event.setCancelled(true);
                        FakeplayerListener.log.warning(String.format("Canceled kicking fake player '%s' due to your configuration", player.getName()));
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void kickOrNotifyOnDead(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (!this.manager.isNotFake(player)) {
            if (!this.config.isKickOnDead()) {
                CommandSender creator = this.manager.getCreator(player);

                if (creator != null) {
                    creator.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.listener.death.notify", Component.text(player.getName(), NamedTextColor.GOLD), Component.text("/cb respawn", NamedTextColor.DARK_GREEN, new TextDecoration[]{TextDecoration.UNDERLINED}).clickEvent(ClickEvent.runCommand("/cb respawn " + player.getName()))).color(NamedTextColor.RED));
                }

            } else {
                Optional optional = Optional.ofNullable(player.getAttribute(Attribute.MAX_HEALTH)).map(AttributeInstance::getValue);

                Objects.requireNonNull(player);
                optional.ifPresent(player::setHealth);
                event.setCancelled(true);
                this.manager.remove(event.getPlayer().getName(), event.deathMessage());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void cleanup(@NotNull PlayerQuitEvent event) {
        Player target = event.getPlayer();

        if (!this.manager.isNotFake(target)) {
            try {
                CommandSender commandsender = this.manager.getCreator(target);

                if (commandsender instanceof Player) {
                    Player creator = (Player) commandsender;

                    if (this.manager.countByCreator(creator) == 1) {
                        BukkitScheduler bukkitscheduler = Bukkit.getScheduler();
                        Main main = Main.getInstance();

                        Objects.requireNonNull(creator);
                        bukkitscheduler.runTaskLater(main, creator::updateCommands, 1L);
                    }
                }
            } finally {
                this.manager.cleanup(target);
            }

        }
    }

    @EventHandler
    public void onPluginDisable(@NotNull PluginDisableEvent event) {
        if (event.getPlugin() == Main.getInstance()) {
            Main main = Main.getInstance();
            FakeplayerManager fakeplayermanager = this.manager;

            Objects.requireNonNull(this.manager);
            Exceptions.suppress(main, fakeplayermanager::onDisable);
            main = Main.getInstance();
            UsedIdRepository usedidrepository = this.usedIdRepository;

            Objects.requireNonNull(this.usedIdRepository);
            Exceptions.suppress(main, usedidrepository::onDisable);
        }

    }
}
