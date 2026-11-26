package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.common.base.Throwables;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.command.exception.CommandException;
import io.github.hello09x.devtools.command.exception.HandleCommandException;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.PresetDefinition;
import io.github.hello09x.fakeplayer.core.preset.PresetInventoryCodec;
import io.github.hello09x.fakeplayer.core.preset.PresetInventorySnapshot;
import io.github.hello09x.fakeplayer.core.util.Attributes;
import io.github.hello09x.fakeplayer.core.util.Mth;
import io.github.hello09x.fakeplayer.core.util.SkinRestorerCompat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SpawnCommand extends AbstractCommand {
   private static final DateTimeFormatter REMOVE_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
   private static final BukkitScheduler scheduler = Bukkit.getScheduler();

   public SpawnCommand() {
   }

   private static String toLocationString(@NotNull Location location) {
      return location.getWorld().getName()
         + ": "
         + StringUtils.joinWith(", ", new Object[]{Mth.floor(location.getX(), 0.5), Mth.floor(location.getY(), 0.5), Mth.floor(location.getZ(), 0.5)});
   }

   @HandleCommandException
   public void spawn(@NotNull CommandSender sender, @NotNull CommandArguments args) {
      List<String> argv = this.collectArgs(args);
      if (sender instanceof Player player && (argv.size() == 2 || argv.size() == 3)) {
         String presetName = argv.get(0);
         if (this.config.getPresets().containsKey(presetName)) {
            this.spawnWithPreset(player, presetName, argv.get(1), argv.size() == 3 ? argv.get(2) : null);
            return;
         }
      }

      this.spawnNormal(sender, argv);
   }

   @NotNull
   private List<String> collectArgs(@NotNull CommandArguments args) {
      ArrayList<String> argv = new ArrayList<>(5);

      for (String key : new String[]{"arg1", "arg2", "arg3", "arg4", "arg5"}) {
         String v = (String)args.get(key);
         if (v != null) {
            argv.add(v);
         }
      }

      return argv;
   }

   private void spawnWithPreset(@NotNull Player creator, @NotNull String presetName, @NotNull String botName, String skinPlayerName) {
      if (!botName.isBlank() && !creator.hasPermission("cactusbot.command.spawn.name")) {
         creator.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.cmd.error.no-permission").color(NamedTextColor.RED));
      } else {
         PresetDefinition preset = this.config.getPresets().get(presetName);
         if (preset == null) {
            creator.sendMessage(
               MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.not-found", Component.text(presetName, NamedTextColor.WHITE))
                  .color(NamedTextColor.RED)
            );
         } else {
            Location spawnpoint = creator.getLocation().clone();
            LocalDateTime removedAt = Optional.ofNullable(this.config.getLifespan()).map(lifespan -> LocalDateTime.now().plus(lifespan)).orElse(null);
            Long lifespanMs = Optional.ofNullable(this.config.getLifespan()).map(Duration::toMillis).orElse(-1L);
            this.manager
               .spawnAsync(creator, botName.isBlank() ? null : botName, spawnpoint, lifespanMs)
               .thenAcceptAsync(fake -> {
                  if (fake != null) {
                     scheduler.runTask(Main.getInstance(), () -> {
                        fake.setMetadata("fakeplayer:preset", new FixedMetadataValue(Main.getInstance(), presetName));
                        PresetInventorySnapshot snapshot = PresetInventoryCodec.decode(preset.inventoryBase64());
                        if (snapshot != null) {
                           PlayerInventory inv = fake.getInventory();
                           inv.setStorageContents(snapshot.storageContents());
                           inv.setArmorContents(snapshot.armorContents());
                           inv.setItemInOffHand(snapshot.offhand());
                           inv.setHeldItemSlot(Math.max(0, Math.min(8, snapshot.heldSlot())));
                           fake.updateInventory();
                        }

                        Double reach = preset.reach();
                        if (reach != null && reach > 0.0) {
                           fake.setMetadata("fakeplayer:preset_reach", new FixedMetadataValue(Main.getInstance(), reach));

                           try {
                              AttributeInstance attr = fake.getAttribute(Attributes.playerEntityInteractionRange());
                              if (attr != null) {
                                 attr.setBaseValue(reach);
                              }
                           } catch (Throwable var6x) {
                           }
                        }
                     });
                     if (skinPlayerName != null && !skinPlayerName.isBlank()) {
                        this.applySkinOverride(fake, skinPlayerName);
                     }

                     this.sendSpawnSuccess(creator, fake.getName(), spawnpoint, removedAt);
                  }
               })
               .exceptionally(
                  e -> {
                     if (Throwables.getRootCause(e) instanceof CommandException ce) {
                        scheduler.runTask(Main.getInstance(), () -> creator.sendMessage(ce.component()));
                     } else {
                        scheduler.runTask(
                           Main.getInstance(),
                           () -> creator.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.spawn.error.unknown", NamedTextColor.RED))
                        );
                        log.severe(Throwables.getStackTraceAsString(e));
                     }

                     return null;
                  }
               );
         }
      }
   }

   private void applySkinOverride(@NotNull Player fake, @NotNull String skinName) {
      this.scheduleApplySkin(fake, skinName, 2L);
      this.scheduleApplySkin(fake, skinName, 25L);
   }

   private void scheduleApplySkin(@NotNull Player fake, @NotNull String skinName, long delayTicks) {
      Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
         if (fake.isOnline() && !this.manager.isNotFake(fake)) {
            if (SkinRestorerCompat.isAvailable()) {
               SkinRestorerCompat.applySkinByNameAsync(Main.getInstance(), fake, skinName);
            }
         }
      }, delayTicks);
   }

   private void spawnNormal(@NotNull CommandSender sender, @NotNull List<String> argv) {
      String name = null;
      Location spawnpoint;
      if (argv.isEmpty()) {
         spawnpoint = sender instanceof Player p ? p.getLocation().clone() : ((World)Bukkit.getWorlds().get(0)).getSpawnLocation().clone();
      } else if (argv.size() == 1) {
         name = argv.get(0);
         if (!name.isBlank() && sender instanceof Player p && !p.hasPermission("cactusbot.command.spawn.name")) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.cmd.error.no-permission").color(NamedTextColor.RED));
            return;
         }

         spawnpoint = sender instanceof Player p ? p.getLocation().clone() : ((World)Bukkit.getWorlds().get(0)).getSpawnLocation().clone();
      } else {
         if (argv.size() != 5) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.spawn.error.invalid-arguments").color(NamedTextColor.RED));
            return;
         }

         if (sender instanceof Player p && !p.hasPermission("cactusbot.command.spawn.location")) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.cmd.error.no-permission").color(NamedTextColor.RED));
            return;
         }

         name = argv.get(0);
         if (!name.isBlank() && sender instanceof Player p && !p.hasPermission("cactusbot.command.spawn.name")) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.cmd.error.no-permission").color(NamedTextColor.RED));
            return;
         }

         World world = Bukkit.getWorld(argv.get(1));
         if (world == null) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.spawn.error.invalid-arguments").color(NamedTextColor.RED));
            return;
         }

         try {
            double x = Double.parseDouble(argv.get(2));
            double y = Double.parseDouble(argv.get(3));
            double z = Double.parseDouble(argv.get(4));
            spawnpoint = new Location(world, x, y, z);
         } catch (NumberFormatException var12) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.spawn.error.invalid-arguments").color(NamedTextColor.RED));
            return;
         }
      }

      LocalDateTime removedAt = Optional.ofNullable(this.config.getLifespan()).map(lifespan -> LocalDateTime.now().plus(lifespan)).orElse(null);
      Long lifespanMs = Optional.ofNullable(this.config.getLifespan()).map(Duration::toMillis).orElse(-1L);
      this.manager
         .spawnAsync(sender, name != null && name.isBlank() ? null : name, spawnpoint, lifespanMs)
         .thenAcceptAsync(player -> {
            if (player != null) {
               this.sendSpawnSuccess(sender, player.getName(), spawnpoint, removedAt);
            }
         })
         .exceptionally(
            e -> {
               if (Throwables.getRootCause(e) instanceof CommandException ce) {
                  scheduler.runTask(Main.getInstance(), () -> sender.sendMessage(ce.component()));
               } else {
                  scheduler.runTask(
                     Main.getInstance(),
                     () -> sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.spawn.error.unknown", NamedTextColor.RED))
                  );
                  log.severe(Throwables.getStackTraceAsString(e));
               }

               return null;
            }
         );
   }

   private void sendSpawnSuccess(@NotNull CommandSender sender, @NotNull String fakeName, @NotNull Location spawnpoint, LocalDateTime removedAt) {
      Component message;
      if (removedAt == null) {
         message = MessageUtils.translatableWithPrefix(
               "fakeplayer.command.spawn.success.without-lifespan",
               Component.text(fakeName, NamedTextColor.WHITE),
               Component.text(toLocationString(spawnpoint), NamedTextColor.WHITE)
            )
            .color(NamedTextColor.GRAY);
      } else {
         message = MessageUtils.translatableWithPrefix(
               "fakeplayer.command.spawn.success.with-lifespan",
               Component.text(fakeName, NamedTextColor.WHITE),
               Component.text(toLocationString(spawnpoint), NamedTextColor.WHITE),
               Component.text(REMOVE_AT_FORMATTER.format(removedAt))
            )
            .color(NamedTextColor.GRAY);
      }

      scheduler.runTask(Main.getInstance(), () -> {
         sender.sendMessage(message);
         if (sender instanceof Player p && this.manager.countByCreator(sender) == 1) {
            p.updateCommands();
         }
      });
   }
}
