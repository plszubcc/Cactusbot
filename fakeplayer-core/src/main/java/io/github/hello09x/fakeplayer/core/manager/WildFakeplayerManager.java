package io.github.hello09x.fakeplayer.core.manager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

@Singleton
public class WildFakeplayerManager implements PluginMessageListener, Listener {
   private static final Logger log = Main.getInstance().getLogger();
   private static final boolean IS_BUNGEECORD = Bukkit.getServer().spigot().getSpigotConfig().getBoolean("settings.bungeecord", false);
   private static final String CHANNEL = "BungeeCord";
   private static final String SUB_CHANNEL = "PlayerList";
   private static final int CLEANUP_THRESHOLD = 2;
   private static final int CLEANUP_PERIOD = 6000;
   private final FakeplayerManager manager;
   private final FakeplayerConfig config;
   private final Map<String, AtomicInteger> offline = new HashMap<>();

   @Inject
   public WildFakeplayerManager(FakeplayerManager manager, FakeplayerConfig config) {
      this.manager = manager;
      this.config = config;
      Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::cleanup, 0L, 6000L);
      Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
   }

   @EventHandler
   public void handleFollowQuitingForce(PlayerQuitEvent event) {
      if (this.config.isFollowQuiting() && this.config.isFollowQuitingForce()) {
         Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (Bukkit.getPlayer(event.getPlayer().getUniqueId()) == null) {
               List<Player> targets = this.manager.getAll(event.getPlayer());
               if (!targets.isEmpty()) {
                  for (Player target : targets) {
                     this.manager.remove(target.getName(), "Creator offline");
                  }
               }
            }
         }, this.config.getFollowQuitingForceDelay() * 20);
      }
   }

   public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
      if (channel.equals("BungeeCord")) {
         ByteArrayDataInput in = ByteStreams.newDataInput(message);
         if (in.readUTF().equals("PlayerList")) {
            if (in.readUTF().equals("ALL")) {
               HashSet<String> players = new HashSet<>();
               players.addAll(Arrays.asList(in.readUTF().split(", ")));
               players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
               this.cleanup0(players);
            }
         }
      }
   }

   public void cleanup0(@NotNull Set<String> online) {
      Map<String, List<Player>> group = this.manager.getAll().stream().collect(Collectors.groupingBy(this.manager::getCreatorName));

      for (Entry<String, List<Player>> entry : group.entrySet()) {
         String creator = entry.getKey();
         if (!creator.equals("CONSOLE")) {
            List<Player> targets = entry.getValue();
            if (!targets.isEmpty() && !online.contains(creator) && this.offline.computeIfAbsent(creator, x -> new AtomicInteger()).incrementAndGet() >= 2) {
               for (Player target : targets) {
                  this.manager.remove(target.getName(), "Creator offline");
               }

               log.info("%s is offline more than %d ticks, removing %d fake players".formatted(creator, 12000, targets.size()));
            }
         }
      }

      for (String player : online) {
         this.offline.remove(player);
      }
   }

   public void cleanup() {
      if (this.config.isFollowQuiting()) {
         if (!IS_BUNGEECORD) {
            this.cleanup0(Bukkit.getOnlinePlayers().stream().<String>map(Player::getName).collect(Collectors.toSet()));
         } else {
            Player recipient = Bukkit.getServer().getOnlinePlayers().stream().filter(this.manager::isNotFake).findAny().orElse(null);
            if (recipient != null) {
               ByteArrayDataOutput out = ByteStreams.newDataOutput();
               out.writeUTF("PlayerList");
               out.writeUTF("ALL");
               recipient.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
            }
         }
      }
   }
}
