package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.preset.PresetInventoryCodec;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PresetCommand extends AbstractCommand {
   public PresetCommand() {
   }

   public void create(@NotNull CommandSender sender, @NotNull CommandArguments args) {
      String name = ((String)args.get("preset")).trim();
      if (name.isBlank()) {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.invalid-name").color(NamedTextColor.RED));
      } else {
         File configFile = this.getConfigFile();
         YamlConfiguration yml = YamlConfiguration.loadConfiguration(configFile);
         if (yml.getConfigurationSection("presets") == null) {
            yml.createSection("presets");
         }

         if (yml.getConfigurationSection("presets." + name) != null) {
            sender.sendMessage(
               MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.already-exists", Component.text(name, NamedTextColor.WHITE))
                  .color(NamedTextColor.RED)
            );
         } else {
            yml.createSection("presets." + name);
            yml.set("presets." + name + ".commands", List.of());

            try {
               yml.save(configFile);
            } catch (Exception var7) {
               sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.save-failed").color(NamedTextColor.RED));
               return;
            }

            this.config.reload();
            sender.sendMessage(
               MessageUtils.translatableWithPrefix("fakeplayer.command.preset.create.success", Component.text(name, NamedTextColor.WHITE))
                  .color(NamedTextColor.GRAY)
            );
         }
      }
   }

   public void delete(@NotNull CommandSender sender, @NotNull CommandArguments args) {
      String name = ((String)args.get("preset")).trim();
      if (name.isBlank()) {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.invalid-name").color(NamedTextColor.RED));
      } else {
         File configFile = this.getConfigFile();
         YamlConfiguration yml = YamlConfiguration.loadConfiguration(configFile);
         if (yml.getConfigurationSection("presets." + name) == null) {
            sender.sendMessage(
               MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.not-found", Component.text(name, NamedTextColor.WHITE))
                  .color(NamedTextColor.RED)
            );
         } else {
            yml.set("presets." + name, null);

            try {
               yml.save(configFile);
            } catch (Exception var7) {
               sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.save-failed").color(NamedTextColor.RED));
               return;
            }

            this.config.reload();
            sender.sendMessage(
               MessageUtils.translatableWithPrefix("fakeplayer.command.preset.delete.success", Component.text(name, NamedTextColor.WHITE))
                  .color(NamedTextColor.GRAY)
            );
         }
      }
   }

   public void list(@NotNull CommandSender sender, @NotNull CommandArguments args) {
      List<String> names = this.config.getPresets().keySet().stream().sorted().collect(Collectors.toList());
      if (names.isEmpty()) {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.list.empty").color(NamedTextColor.GRAY));
      } else {
         Component message = MessageUtils.translatableWithPrefix(
               "fakeplayer.command.preset.list.success", Component.text(String.join(", ", names), NamedTextColor.WHITE)
            )
            .color(NamedTextColor.GRAY);
         sender.sendMessage(message);
      }
   }

   public void inventorySet(@NotNull CommandSender sender, @NotNull CommandArguments args) {
      if (sender instanceof Player player) {
         String name = ((String)args.get("preset")).trim();
         String action = ((String)args.get("action")).trim();
         if (!action.equalsIgnoreCase("set")) {
            sender.sendMessage(
               MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.invalid-action", Component.text(action, NamedTextColor.WHITE))
                  .color(NamedTextColor.RED)
            );
         } else if (!this.config.getPresets().containsKey(name)) {
            sender.sendMessage(
               MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.not-found", Component.text(name, NamedTextColor.WHITE))
                  .color(NamedTextColor.RED)
            );
         } else {
            String encoded = PresetInventoryCodec.encode(PresetInventoryCodec.capture(player));
            if (encoded == null) {
               sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.save-failed").color(NamedTextColor.RED));
            } else {
               File configFile = this.getConfigFile();
               YamlConfiguration yml = YamlConfiguration.loadConfiguration(configFile);
               yml.set("presets." + name + ".inventory", encoded);

               try {
                  yml.save(configFile);
               } catch (Exception var10) {
                  sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.save-failed").color(NamedTextColor.RED));
                  return;
               }

               this.config.reload();
               sender.sendMessage(
                  MessageUtils.translatableWithPrefix("fakeplayer.command.preset.inventory.set.success", Component.text(name, NamedTextColor.WHITE))
                     .color(NamedTextColor.GRAY)
               );
            }
         }
      } else {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.preset.error.player-only").color(NamedTextColor.RED));
      }
   }

   @NotNull
   private File getConfigFile() {
      Main plugin = Main.getInstance();
      File folder = plugin.getDataFolder();
      if (!folder.exists()) {
         folder.mkdirs();
      }

      return new File(folder, "config.yml");
   }
}
