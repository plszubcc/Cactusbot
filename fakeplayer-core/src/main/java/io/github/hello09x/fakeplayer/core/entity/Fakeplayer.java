package io.github.hello09x.fakeplayer.core.entity;

import com.coderxi.plugin.utils.translation.MessageUtils;
import io.github.hello09x.devtools.command.exception.CommandException;
import io.github.hello09x.devtools.core.utils.EntityUtils;
import io.github.hello09x.devtools.core.utils.SchedulerUtils;
import io.github.hello09x.devtools.core.utils.WorldUtils;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.config.PreventKicking;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerAutofishManager;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerReplenishManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.manager.naming.SequenceName;
import io.github.hello09x.fakeplayer.core.util.Attributes;
import io.github.hello09x.fakeplayer.core.util.InternalAddressGenerator;
import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class Fakeplayer {
   private static final InternalAddressGenerator ipGen = new InternalAddressGenerator();
   private static final FakeplayerConfig config = (FakeplayerConfig)Main.getInjector().getInstance(FakeplayerConfig.class);
   private static final NMSBridge bridge = (NMSBridge)Main.getInjector().getInstance(NMSBridge.class);
   private static final FakeplayerReplenishManager replenishManager = (FakeplayerReplenishManager)Main.getInjector()
      .getInstance(FakeplayerReplenishManager.class);
   private static final FakeplayerAutofishManager autofishManager = (FakeplayerAutofishManager)Main.getInjector().getInstance(FakeplayerAutofishManager.class);
   private static final ActionManager actionManager = (ActionManager)Main.getInjector().getInstance(ActionManager.class);
   @NotNull
   private final CommandSender creator;
   @NotNull
   private final NMSServerPlayer handle;
   @NotNull
   private final Player player;
   @NotNull
   private final String creatorIp;
   @NotNull
   private final SequenceName sequenceName;
   @NotNull
   private final FakeplayerTicker ticker;
   @NotNull
   private final String name;
   @NotNull
   private final UUID uuid;
   @UnknownNullability
   private NMSNetwork network;

   public Fakeplayer(@NotNull CommandSender creator, @NotNull String creatorIp, @NotNull SequenceName sequenceName, long lifespan) {
      this.name = sequenceName.name();
      this.uuid = sequenceName.uuid();
      this.creator = creator;
      this.creatorIp = creatorIp;
      this.sequenceName = sequenceName;
      this.handle = bridge.fromServer(Bukkit.getServer()).newPlayer(this.uuid, this.name);
      this.player = this.handle.getPlayer();
      this.ticker = new FakeplayerTicker(this, lifespan);
      this.player.setPersistent(config.isPersistData());
      this.player.setSleepingIgnored(true);
      this.handle.setPlayBefore();
      this.handle.disableAdvancements(Main.getInstance());
   }

   public CompletableFuture<Void> spawnAsync(@NotNull SpawnOption option) {
      InetAddress address = ipGen.next();
      this.player.setMetadata("fakeplayer:spawned_at", new FixedMetadataValue(Main.getInstance(), Bukkit.getCurrentTick()));
      return SchedulerUtils.runTaskAsynchronously(
            Main.getInstance(),
            () -> {
               AsyncPlayerPreLoginEvent event = this.callPreLoginEvent(address);
               if (event.getLoginResult() != Result.ALLOWED) {
                  throw new CommandException(
                     Component.translatable(
                           "fakeplayer.command.spawn.error.disallowed",
                           new ComponentLike[]{Component.text(this.player.getName(), NamedTextColor.WHITE), event.kickMessage()}
                        )
                        .color(NamedTextColor.RED)
                  );
               }
            }
         )
         .thenComposeAsync(
            nul -> SchedulerUtils.runTask(
               Main.getInstance(),
               () -> {
                  PlayerLoginEvent event = this.callLoginEvent(address);
                  if (event.getResult() != org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED
                     && config.getPreventKicking().ordinal() < PreventKicking.ON_SPAWNING.ordinal()) {
                     throw new CommandException(
                        Component.translatable(
                           "fakeplayer.command.spawn.error.disallowed",
                           NamedTextColor.RED,
                           new ComponentLike[]{Component.text(this.player.getName(), NamedTextColor.WHITE), event.kickMessage()}
                        )
                     );
                  } else {
                     if (config.isDropInventoryOnQuiting()) {
                        this.player.getInventory().clear();
                     }

                     this.player.setInvulnerable(option.invulnerable());
                     this.player.setCollidable(option.collidable());
                     this.player.setCanPickupItems(option.pickupItems());
                     if (option.lookAtEntity()) {
                        actionManager.setAction(this.player, ActionType.LOOK_AT_NEAREST_ENTITY, ActionSetting.continuous());
                     }

                     if (option.replenish()) {
                        replenishManager.setReplenish(this.player, true);
                     }

                     if (option.autofish()) {
                        autofishManager.setAutofish(this.player, true);
                     }

                     this.network = bridge.createNetwork(address);
                     this.network.placeNewPlayer(Bukkit.getServer(), this.player);
                     this.player
                        .setHealth(Optional.ofNullable(this.player.getAttribute(Attributes.maxHealth())).<Double>map(AttributeInstance::getValue).orElse(20.0));
                     this.player.setFoodLevel(20);
                     this.setupName();
                     this.handle.setupClientOptions();
                     this.teleportToSpawnpoint(option.spawnAt().clone());
                     this.ticker.runTaskTimer(Main.getInstance(), 0L, 1L);
                  }
               }
            )
         );
   }

   private void teleportToSpawnpoint(@NotNull Location to) {
      Location from = this.player.getLocation();
      if (from.getWorld().equals(to.getWorld())) {
         World otherWorld = WorldUtils.getOtherWorld(from.getWorld());
         if (otherWorld == null || !this.player.teleport(otherWorld.getSpawnLocation())) {
            this.creator
               .sendMessage(
                  MessageUtils.translatableWithPrefix(
                        "fakeplayer.command.spawn.error.no-mob-spawning-ability", Component.text(this.player.getName(), NamedTextColor.WHITE)
                     )
                     .color(NamedTextColor.GRAY)
               );
         }
      }

      Bukkit.getScheduler()
         .runTask(
            Main.getInstance(),
            () -> {
               if (!EntityUtils.teleportAndSound(this.player, to)) {
                  this.creator
                     .sendMessage(
                        MessageUtils.translatableWithPrefix(
                              "fakeplayer.command.spawn.error.teleport-failed", Component.text(this.player.getName(), NamedTextColor.WHITE)
                           )
                           .color(NamedTextColor.GRAY)
                     );
               }
            }
         );
   }

   public boolean isOnline() {
      return this.player.isOnline();
   }

   public int getTickCount() {
      return this.handle.getTickCount();
   }

   @NotNull
   private AsyncPlayerPreLoginEvent callPreLoginEvent(@NotNull InetAddress address) {
      AsyncPlayerPreLoginEvent event = new AsyncPlayerPreLoginEvent(this.name, address, this.uuid);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   @NotNull
   private PlayerLoginEvent callLoginEvent(@NotNull InetAddress address) {
      PlayerLoginEvent event = new PlayerLoginEvent(this.player, address.getHostAddress(), address);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public boolean isCreatedBy(@NotNull CommandSender sender) {
      return this.creator instanceof Player pc && sender instanceof Player ps
         ? pc.getUniqueId().equals(ps.getUniqueId())
         : this.creator.getClass() == sender.getClass() && this.creator.getName().equals(sender.getName());
   }

   @NotNull
   public UUID getUUID() {
      return this.uuid;
   }

   private void setupName() {
      TextComponent displayName = Component.text(
         this.player.getName(), config.getNameStyleColor(), config.getNameStyleDecorations().toArray(new TextDecoration[0])
      );
      this.player.playerListName(displayName);
      this.player.displayName(displayName);
      this.player.customName(displayName);
   }

   @NotNull
   @Generated
   public CommandSender getCreator() {
      return this.creator;
   }

   @NotNull
   @Generated
   public NMSServerPlayer getHandle() {
      return this.handle;
   }

   @NotNull
   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @NotNull
   @Generated
   public String getCreatorIp() {
      return this.creatorIp;
   }

   @NotNull
   @Generated
   public SequenceName getSequenceName() {
      return this.sequenceName;
   }

   @NotNull
   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   @UnknownNullability
   public NMSNetwork getNetwork() {
      return this.network;
   }
}
