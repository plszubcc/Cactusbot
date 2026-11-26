package io.github.hello09x.fakeplayer.core.manager.naming;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.naming.exception.IllegalCustomNameException;
import io.github.hello09x.fakeplayer.core.repository.FakeplayerProfileRepository;
import io.github.hello09x.fakeplayer.core.repository.UsedIdRepository;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class NameManager {
   private static final Logger log = Main.getInstance().getLogger();
   private static final int MAX_LENGTH = 16;
   private static final int MIN_LENGTH = 3;
   private final UsedIdRepository legacyUsedIdRepository;
   private final FakeplayerProfileRepository profileRepository;
   private final FakeplayerConfig config;
   private final Map<String, NameSource> nameSources = new HashMap<>();
   private final String serverId;

   @Inject
   public NameManager(UsedIdRepository legacyUsedIdRepository, FakeplayerProfileRepository profileRepository, FakeplayerConfig config) {
      this.legacyUsedIdRepository = legacyUsedIdRepository;
      this.profileRepository = profileRepository;
      this.config = config;
      File file = new File(Main.getInstance().getDataFolder(), "serverid");
      this.serverId = Optional.ofNullable(loadServerId(file)).orElseGet(() -> {
         String uuid = UUID.randomUUID().toString();

         try {
            try (FileWriter out = new FileWriter(file, StandardCharsets.UTF_8)) {
               IOUtils.write(uuid, out);
            }

            return uuid;
         } catch (IOException var7) {
            throw new UncheckedIOException(var7);
         }
      });
   }

   @Nullable
   private static String loadServerId(@NotNull File file) {
      if (!file.exists()) {
         return null;
      } else {
         String serverId;
         try (FileReader in = new FileReader(file, StandardCharsets.UTF_8)) {
            serverId = IOUtils.toString(in);
         } catch (IOException var7) {
            throw new UncheckedIOException(var7);
         }

         return serverId.isBlank() ? null : serverId;
      }
   }

   @NotNull
   private UUID getUUIDFromName(@NotNull String name) {
      UUID uuid = this.profileRepository.selectUUIDByName(name);
      if (uuid != null) {
         return uuid;
      } else {
         String base = this.serverId + ":" + name;
         UUID legacyUUID = UUID.nameUUIDFromBytes(base.getBytes(StandardCharsets.UTF_8));
         if (this.legacyUsedIdRepository.contains(legacyUUID)) {
            this.profileRepository.insert(name, legacyUUID);
            this.legacyUsedIdRepository.remove(legacyUUID);
            return legacyUUID;
         } else {
            for (int i = 0; i < 10; i++) {
               legacyUUID = UUID.randomUUID();
               if (!this.legacyUsedIdRepository.contains(legacyUUID)
                  && !this.profileRepository.existsByUUID(legacyUUID)
                  && !Bukkit.getOfflinePlayer(legacyUUID).hasPlayedBefore()) {
                  this.profileRepository.insert(name, legacyUUID);
                  return legacyUUID;
               }
            }

            throw new IllegalStateException("Failed to generate uuid for cactus bot '%s' after 10 attempts".formatted(name));
         }
      }
   }

   @NotNull
   public SequenceName getSpecifiedName(@NotNull String name) {
      if (StringUtils.isNotBlank(this.config.getNamePrefix())) {
         name = this.config.getNamePrefix().trim() + name;
      }

      if (name.startsWith("-")) {
         throw new IllegalCustomNameException(
            Component.translatable("fakeplayer.spawn.error.name.start-with-illegal-character", new ComponentLike[]{Component.text("-", NamedTextColor.WHITE)})
               .color(NamedTextColor.RED)
         );
      } else if (name.length() > 16) {
         throw new IllegalCustomNameException(
            Component.translatable("fakeplayer.spawn.error.name.too-long", new ComponentLike[]{Component.text(16, NamedTextColor.WHITE)})
               .color(NamedTextColor.RED)
         );
      } else if (name.length() < 3) {
         throw new IllegalCustomNameException(
            Component.translatable("fakeplayer.spawn.error.name.too-short", new ComponentLike[]{Component.text(3, NamedTextColor.WHITE)})
               .color(NamedTextColor.RED)
         );
      } else if (!this.config.getNamePattern().asPredicate().test(name)) {
         throw new IllegalCustomNameException(Component.translatable("fakeplayer.spawn.error.name.invalid", NamedTextColor.RED));
      } else {
         Player player = Bukkit.getPlayerExact(name);
         if (player != null) {
            throw new IllegalCustomNameException(
               player.isDead()
                  ? Component.translatable(
                        "fakeplayer.spawn.error.name.online-dead",
                        new ComponentLike[]{Component.text(name, NamedTextColor.GOLD), Component.text("/cb respawn", NamedTextColor.DARK_GREEN)}
                     )
                     .color(NamedTextColor.RED)
                  : Component.translatable("fakeplayer.spawn.error.name.online", new ComponentLike[]{Component.text(name, NamedTextColor.GOLD)})
                     .color(NamedTextColor.RED)
            );
         } else {
            return new SequenceName("custom", 0, this.getUUIDFromName(name), name);
         }
      }
   }

   @NotNull
   public SequenceName getRegularName(@NotNull CommandSender creator) {
      String source = this.config.getNameTemplate();
      if (source.isBlank()) {
         source = creator.getName();
      }

      source = source.replace("%c", creator.getName());
      if (StringUtils.isNotBlank(this.config.getNamePrefix())) {
         source = this.config.getNamePrefix().trim() + source;
      }

      for (int i = 0; i < 10; i++) {
         int seq = this.nameSources.computeIfAbsent(source, ignored -> new NameSource(this.config.getPlayerLimit())).pop();
         String suffix = "_" + (seq + 1);
         String name;
         if (source.length() + suffix.length() > 16) {
            name = source.substring(0, 16 - suffix.length()) + suffix;
         } else {
            name = source + suffix;
         }

         if (Bukkit.getPlayerExact(name) == null) {
            return new SequenceName(source, seq, this.getUUIDFromName(name), name);
         }
      }

      for (int i = 0; i < 10; i++) {
         String namex = RandomStringUtils.randomAlphanumeric(16);
         if (Bukkit.getPlayerExact(namex) == null) {
            log.warning("Failed to generate a regular name for cactus bot after 10 attempts, using a random name as fallback: " + namex);
            return new SequenceName("random", 0, this.getUUIDFromName(namex), namex);
         }
      }

      throw new IllegalStateException("Failed to generate a name for cactus bot based on creator '%s'".formatted(creator.getName()));
   }

   public void unregister(@NotNull String group, int sequence) {
      Optional.ofNullable(this.nameSources.get(group)).ifPresent(ns -> ns.push(sequence));
   }

   public void unregister(@NotNull SequenceName sn) {
      this.unregister(sn.group(), sn.sequence());
   }
}
