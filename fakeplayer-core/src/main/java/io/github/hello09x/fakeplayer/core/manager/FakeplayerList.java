package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.entity.Fakeplayer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@Singleton
public class FakeplayerList {
   private final Map<String, Fakeplayer> playersByName = new HashMap<>();
   private final Map<UUID, Fakeplayer> playersByUUID = new HashMap<>();
   private final Map<String, List<Fakeplayer>> playersByCreator = new HashMap<>();

   public FakeplayerList() {
   }

   public void add(@NotNull Fakeplayer player) {
      this.playersByName.put(player.getName(), player);
      this.playersByUUID.put(player.getUUID(), player);
      this.playersByCreator.computeIfAbsent(player.getCreator().getName(), key -> new LinkedList<>()).add(player);
   }

   @Nullable
   public Fakeplayer getByName(@NotNull String name) {
      return Optional.ofNullable(this.playersByName.get(name)).map(this::checkOnline).orElse(null);
   }

   @Nullable
   public Fakeplayer getByUUID(@NotNull UUID uuid) {
      return Optional.ofNullable(this.playersByUUID.get(uuid)).map(this::checkOnline).orElse(null);
   }

   @NotNull
   @Unmodifiable
   public List<Fakeplayer> getByCreator(@NotNull String creator) {
      return Optional.ofNullable(this.playersByCreator.get(creator)).map(Collections::unmodifiableList).orElse(Collections.emptyList());
   }

   public void remove(@NotNull Fakeplayer player) {
      this.playersByName.remove(player.getName());
      this.playersByUUID.remove(player.getUUID());
      Optional.ofNullable(this.playersByCreator.get(player.getCreator().getName())).map(players -> players.remove(player));
   }

   @Nullable
   public Fakeplayer removeByUUID(@NotNull UUID uuid) {
      Fakeplayer player = this.getByUUID(uuid);
      if (player == null) {
         return null;
      } else {
         this.remove(player);
         return player;
      }
   }

   public int countByCreator(@NotNull String creator) {
      return Optional.ofNullable(this.playersByCreator.get(creator)).map(List::size).orElse(0);
   }

   @NotNull
   @Unmodifiable
   public List<Fakeplayer> getAll() {
      return List.copyOf(this.playersByUUID.values());
   }

   @Nullable
   private Fakeplayer checkOnline(@NotNull Fakeplayer player) {
      if (!player.isOnline()) {
         this.remove(player);
         return null;
      } else {
         return player;
      }
   }

   @NotNull
   public Stream<Fakeplayer> stream() {
      return this.playersByUUID.values().stream();
   }

   public int getSize() {
      return this.playersByUUID.size();
   }
}
