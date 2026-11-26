package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.command.exception.CommandException;
import io.github.hello09x.devtools.core.utils.Exceptions;
import io.github.hello09x.devtools.core.utils.MetadataUtils;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.entity.Fakeplayer;
import io.github.hello09x.fakeplayer.core.entity.SpawnOption;
import io.github.hello09x.fakeplayer.core.manager.feature.FakeplayerFeatureManager;
import io.github.hello09x.fakeplayer.core.manager.feature.FeatureInstance;
import io.github.hello09x.fakeplayer.core.manager.naming.NameManager;
import io.github.hello09x.fakeplayer.core.manager.naming.SequenceName;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import io.github.hello09x.fakeplayer.core.util.AddressUtils;
import io.github.hello09x.fakeplayer.core.util.Commands;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;
import lombok.Generated;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender.Spigot;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class FakeplayerManager {
   public static final String REMOVAL_REASON_PREFIX = "[fakeplayer] ";
   private static final Logger log = Main.getInstance().getLogger();
   private final NameManager nameManager;
   private final FakeplayerList playerList;
   private final FakeplayerFeatureManager featureManager;
   private final NMSBridge nms;
   private final FakeplayerConfig config;
   private final ScheduledExecutorService lagMonitor;
   private int laglevel = 0;

   @Inject
   public FakeplayerManager(NameManager nameManager, FakeplayerList playerList, FakeplayerFeatureManager featureManager, NMSBridge nms, FakeplayerConfig config) {
      this.nameManager = nameManager;
      this.playerList = playerList;
      this.featureManager = featureManager;
      this.nms = nms;
      this.config = config;
      this.lagMonitor = Executors.newSingleThreadScheduledExecutor();
      this.lagMonitor
         .scheduleWithFixedDelay(
            () -> {
               if (Bukkit.getServer().getTPS()[0] < config.getKaleTps()) {
                  Bukkit.getScheduler()
                     .runTask(
                        Main.getInstance(),
                        () -> {
                           this.laglevel = Math.min(this.laglevel + 1, this.config.getPlayerLimit());

                           for (Player player : Bukkit.getOnlinePlayers()) {
                              if (!this.isFake(player)) {
                                 List<Player> fakeplayerlist = this.getAll(player);
                                 if (fakeplayerlist.size() > this.config.getPlayerLimit() - this.laglevel) {
                                    for (int i = fakeplayerlist.size() - 1; i >= this.config.getPlayerLimit() - this.laglevel; i--) {
                                       this.remove(fakeplayerlist.get(i).getName(), "Server lag");
                                    }
                                 }
                              }
                           }

                           Bukkit.broadcast(
                              ((TextComponent)Component.text("Server lag! Current fakeplayer limits: ").color(NamedTextColor.GOLD))
                                 .append(Component.text(this.config.getPlayerLimit() - this.laglevel).color(NamedTextColor.RED))
                           );
                        }
                     );
               } else {
                  if (this.laglevel > 0) {
                     Bukkit.broadcast(
                        ((TextComponent)Component.text("Fakeplayer restrictions removed! Current limits: ").color(NamedTextColor.GREEN))
                           .append(Component.text(this.config.getPlayerLimit() - this.laglevel + 1).color(NamedTextColor.AQUA))
                     );
                  }

                  this.laglevel = Math.max(this.laglevel - 1, 0);
               }
            },
            0L,
            60L,
            TimeUnit.SECONDS
         );
   }

   @NotNull
   public CompletableFuture<Player> spawnAsync(@NotNull CommandSender creator, @Nullable String name, @NotNull Location spawnAt, long lifespan) {
      this.checkLimit(creator);
      SequenceName sn = name == null ? this.nameManager.getRegularName(creator) : this.nameManager.getSpecifiedName(name);
      log.info("UUID of fake player %s is %s".formatted(sn.name(), sn.uuid()));
      Fakeplayer fp = new Fakeplayer(creator, AddressUtils.getAddress(creator), sn, lifespan);
      Player target = fp.getPlayer();
      this.playerList.add(fp);
      this.dispatchCommandsEarly(fp, this.config.getPreSpawnCommands());
      return CompletableFuture.<SpawnOption>supplyAsync(
            () -> {
               Map<Feature, FeatureInstance> configs = this.featureManager.getFeatures(creator);
               return new SpawnOption(
                  spawnAt,
                  configs.get(Feature.invulnerable).asBoolean(),
                  configs.get(Feature.collidable).asBoolean(),
                  configs.get(Feature.look_at_entity).asBoolean(),
                  configs.get(Feature.pickup_items).asBoolean(),
                  configs.get(Feature.skin).asBoolean(),
                  configs.get(Feature.replenish).asBoolean(),
                  configs.get(Feature.autofish).asBoolean(),
                  configs.get(Feature.wolverine).asBoolean()
               );
            }
         )
         .thenComposeAsync(fp::spawnAsync)
         .thenApply(ignored -> target);
   }

   @Nullable
   public Player get(@NotNull CommandSender creator, @NotNull String name) {
      return Optional.ofNullable(this.playerList.getByName(name)).filter(p -> p.isCreatedBy(creator)).map(Fakeplayer::getPlayer).orElse(null);
   }

   @Nullable
   public Player get(@NotNull String name) {
      return Optional.ofNullable(this.playerList.getByName(name)).map(Fakeplayer::getPlayer).orElse(null);
   }

   @Nullable
   public String getCreatorName(@NotNull Player target) {
      return Optional.ofNullable(this.playerList.getByUUID(target.getUniqueId())).map(Fakeplayer::getCreator).<String>map(CommandSender::getName).orElse(null);
   }

   @Nullable
   public CommandSender getCreator(@NotNull Player target) {
      return Optional.ofNullable(this.playerList.getByUUID(target.getUniqueId()))
         .map(Fakeplayer::getCreator)
         .map(creator -> (CommandSender)(creator instanceof Player p ? Bukkit.getPlayer(p.getUniqueId()) : creator))
         .orElse(null);
   }

   public boolean remove(@NotNull String name, @Nullable String reason) {
      return this.remove(name, reason == null ? null : Component.text(reason));
   }

   public boolean remove(@NotNull String name, @Nullable Component reason) {
      Player target = this.get(name);
      if (target == null) {
         return false;
      } else {
         target.kick(
            Component.textOfChildren(new ComponentLike[]{Component.text("[fakeplayer] "), (ComponentLike)(reason == null ? Component.text("removed") : reason)})
         );
         return true;
      }
   }

   public int removeAll(@Nullable String reason) {
      List<Player> targets = this.getAll();

      for (Player target : targets) {
         target.kick(Component.text("[fakeplayer] " + (reason == null ? "removed" : reason)));
      }

      return targets.size();
   }

   @NotNull
   public List<Player> getAll() {
      return this.getAll((Predicate<Player>)null);
   }

   @NotNull
   public List<Player> getAll(@Nullable Predicate<Player> predicate) {
      Stream<Player> stream = this.playerList.getAll().stream().map(Fakeplayer::getPlayer);
      if (predicate != null) {
         stream = stream.filter(predicate);
      }

      return stream.toList();
   }

   public void cleanup(@NotNull Player target) {
      Fakeplayer fakeplayer = this.playerList.removeByUUID(target.getUniqueId());
      if (fakeplayer != null) {
         this.nameManager.unregister(fakeplayer.getSequenceName());
         if (this.config.isDropInventoryOnQuiting()) {
            this.nms.createAction(fakeplayer.getPlayer(), ActionType.DROP_INVENTORY, ActionSetting.once()).tick();
         }
      }
   }

   @NotNull
   public List<Player> getAll(@NotNull CommandSender creator) {
      return this.getAll(creator, null);
   }

   @NotNull
   public List<Player> getAll(@NotNull CommandSender creator, @Nullable Predicate<Player> predicate) {
      Stream<Player> stream = this.playerList.getByCreator(creator.getName()).stream().map(Fakeplayer::getPlayer);
      if (predicate != null) {
         stream = stream.filter(predicate);
      }

      return stream.toList();
   }

   public int getSize() {
      return this.playerList.getSize();
   }

   public boolean isFake(@NotNull Player target) {
      return this.playerList.getByUUID(target.getUniqueId()) != null;
   }

   public boolean isNotFake(@NotNull Player target) {
      return this.playerList.getByUUID(target.getUniqueId()) == null;
   }

   public long countByAddress(@NotNull String address) {
      return this.playerList.stream().filter(p -> p.getCreatorIp().equals(address)).count();
   }

   public int countByCreator(@NotNull CommandSender creator) {
      return this.playerList.countByCreator(creator.getName());
   }

   public void setSelection(@NotNull Player creator, @Nullable Player target) {
      if (target == null) {
         creator.removeMetadata("fakeplayer:selection", Main.getInstance());
      } else if (this.isFake(target)) {
         creator.setMetadata("fakeplayer:selection", new FixedMetadataValue(Main.getInstance(), target.getUniqueId()));
      }
   }

   @Nullable
   public Player getSelection(@NotNull CommandSender creator) {
      if (creator instanceof Player p) {
         if (!p.hasMetadata("fakeplayer:selection")) {
            return null;
         } else {
            UUID uuid = MetadataUtils.find(Main.getInstance(), p, "fakeplayer:selection", UUID.class)
               .<Object>map(MetadataValue::value)
               .map(UUID.class::cast)
               .orElse(null);
            if (uuid == null) {
               return null;
            } else {
               Player target = Optional.ofNullable(this.playerList.getByUUID(uuid)).map(Fakeplayer::getPlayer).orElse(null);
               if (target == null) {
                  this.setSelection(p, null);
               }

               return target;
            }
         }
      } else {
         return null;
      }
   }

   public void issueCommands(@NotNull Player target, @NotNull List<String> commands) {
      if (!commands.isEmpty()) {
         if (!this.isNotFake(target)) {
            String p = target.getName();
            String u = target.getUniqueId().toString();
            String c = Objects.requireNonNull(this.getCreatorName(target));

            for (String cmd : Commands.formatCommands(commands, "%p", p, "%u", u, "%c", c)) {
               if (!target.performCommand(cmd)) {
                  log.warning(target.getName() + " failed to execute command: " + cmd);
               } else if (!this.config.isSilentCommands()) {
                  log.info(target.getName() + " issued command: " + cmd);
               }
            }
         }
      }
   }

   public void dispatchCommandsEarly(@NotNull Fakeplayer fp, @NotNull List<String> commands) {
      if (!commands.isEmpty()) {
         Server server = Bukkit.getServer();
         CommandSender sender = this.getDispatchSender();
         String p = fp.getName();
         String u = fp.getUUID().toString();
         String c = fp.getCreator().getName();

         for (String cmd : Commands.formatCommands(commands, "%p", p, "%u", u, "%c", c)) {
            if (!server.dispatchCommand(sender, cmd)) {
               log.warning("Failed to execute command for %s: ".formatted(p) + cmd);
            } else if (!this.config.isSilentCommands()) {
               log.info("Dispatched command: " + cmd);
            }
         }
      }
   }

   public void dispatchCommands(@NotNull FakeplayerManager.DispatchCommandArgs args, @NotNull List<String> commands) {
      if (!commands.isEmpty()) {
         Server server = Bukkit.getServer();
         CommandSender sender = this.getDispatchSender();
         String p = args.fakeplayerName;
         String u = args.fakeplayerUUID;
         String c = args.creatorName;

         for (String cmd : Commands.formatCommands(commands, "%p", p, "%u", u, "%c", c)) {
            if (!server.dispatchCommand(sender, cmd)) {
               log.warning("Failed to execute command for %s: ".formatted(p) + cmd);
            } else if (!this.config.isSilentCommands()) {
               log.info("Dispatched command: " + cmd);
            }
         }
      }
   }

   public void dispatchCommands(@NotNull Player player, @NotNull List<String> commands) {
      this.dispatchCommands(
         new FakeplayerManager.DispatchCommandArgs(player.getName(), player.getUniqueId().toString(), Objects.requireNonNull(this.getCreatorName(player))),
         commands
      );
   }

   public boolean dispatchCommand(@NotNull Player player, @NotNull String command) {
      CommandSender sender = this.getDispatchSender();
      List<String> formatted = Commands.formatCommands(
         List.of(command), "%p", player.getName(), "%u", player.getUniqueId().toString(), "%c", Objects.requireNonNull(this.getCreatorName(player))
      );
      if (formatted.isEmpty()) {
         return false;
      } else {
         String cmd = formatted.get(0);
         boolean success = Bukkit.getServer().dispatchCommand(sender, cmd);
         if (!success) {
            log.warning("Failed to execute command for %s: ".formatted(player.getName()) + cmd);
         } else if (!this.config.isSilentCommands()) {
            log.info("Dispatched command: " + cmd);
         }

         return success;
      }
   }

   @NotNull
   private CommandSender getDispatchSender() {
      ConsoleCommandSender console = Bukkit.getConsoleSender();
      return (CommandSender)(!this.config.isSilentCommands() ? console : new FakeplayerManager.SilentConsoleCommandSender(console));
   }

   private void checkLimit(@NotNull CommandSender creator) throws CommandException {
      if (!creator.isOp()) {
         if (this.playerList.getSize() >= this.config.getServerLimit()) {
            throw new CommandException(Component.translatable("fakeplayer.command.spawn.error.server-limit"));
         } else if (this.playerList.getByCreator(creator.getName()).size() >= this.config.getPlayerLimit() - this.laglevel) {
            throw new CommandException(Component.translatable("fakeplayer.command.spawn.error.player-limit"));
         } else if (this.config.isDetectIp() && this.countByAddress(AddressUtils.getAddress(creator)) >= this.config.getPlayerLimit() - this.laglevel) {
            throw new CommandException(Component.translatable("fakeplayer.command.spawn.error.ip-limit"));
         }
      }
   }

   public void onDisable() {
      Exceptions.suppress(Main.getInstance(), () -> this.removeAll("Plugin disabled"));
      Exceptions.suppress(Main.getInstance(), this.lagMonitor::shutdownNow);
   }

   public static class DispatchCommandArgs {
      public String fakeplayerName;
      public String fakeplayerUUID;
      public String creatorName;

      @Generated
      public DispatchCommandArgs(String fakeplayerName, String fakeplayerUUID, String creatorName) {
         this.fakeplayerName = fakeplayerName;
         this.fakeplayerUUID = fakeplayerUUID;
         this.creatorName = creatorName;
      }
   }

   private static final class SilentConsoleCommandSender implements ConsoleCommandSender {
      private final ConsoleCommandSender delegate;

      private SilentConsoleCommandSender(@NotNull ConsoleCommandSender delegate) {
         this.delegate = delegate;
      }

      public void sendMessage(String message) {
      }

      public void sendMessage(String... messages) {
      }

      public void sendMessage(UUID sender, String message) {
      }

      public void sendMessage(UUID sender, String... messages) {
      }

      @NotNull
      public Server getServer() {
         return this.delegate.getServer();
      }

      @NotNull
      public String getName() {
         return this.delegate.getName();
      }

      @NotNull
      public Spigot spigot() {
         return this.delegate.spigot();
      }

      public boolean isPermissionSet(@NotNull String name) {
         return this.delegate.isPermissionSet(name);
      }

      public boolean isPermissionSet(@NotNull Permission perm) {
         return this.delegate.isPermissionSet(perm);
      }

      public boolean hasPermission(@NotNull String name) {
         return this.delegate.hasPermission(name);
      }

      public boolean hasPermission(@NotNull Permission perm) {
         return this.delegate.hasPermission(perm);
      }

      @NotNull
      public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
         return this.delegate.addAttachment(plugin, name, value);
      }

      @NotNull
      public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
         return this.delegate.addAttachment(plugin);
      }

      @Nullable
      public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
         return this.delegate.addAttachment(plugin, name, value, ticks);
      }

      @Nullable
      public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
         return this.delegate.addAttachment(plugin, ticks);
      }

      public void removeAttachment(@NotNull PermissionAttachment attachment) {
         this.delegate.removeAttachment(attachment);
      }

      public void recalculatePermissions() {
         this.delegate.recalculatePermissions();
      }

      @NotNull
      public Set<PermissionAttachmentInfo> getEffectivePermissions() {
         return this.delegate.getEffectivePermissions();
      }

      public boolean isOp() {
         return this.delegate.isOp();
      }

      public void setOp(boolean value) {
         this.delegate.setOp(value);
      }

      public boolean isConversing() {
         return this.delegate.isConversing();
      }

      public void acceptConversationInput(@NotNull String input) {
         this.delegate.acceptConversationInput(input);
      }

      public boolean beginConversation(@NotNull Conversation conversation) {
         return this.delegate.beginConversation(conversation);
      }

      public void abandonConversation(@NotNull Conversation conversation) {
         this.delegate.abandonConversation(conversation);
      }

      public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
         this.delegate.abandonConversation(conversation, details);
      }

      public void sendRawMessage(@NotNull String message) {
      }

      public void sendRawMessage(UUID sender, @NotNull String message) {
      }

      @NotNull
      public Component name() {
         return this.delegate.name();
      }

      public void sendActionBar(@NotNull Component message) {
      }

      public void sendPlayerListHeader(@NotNull Component header) {
      }

      public void sendPlayerListFooter(@NotNull Component footer) {
      }

      public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
      }

      public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
      }

      public void sendMessage(@NotNull Component message) {
      }

      public void sendMessage(@NotNull Identity source, @NotNull Component message) {
      }
   }
}
