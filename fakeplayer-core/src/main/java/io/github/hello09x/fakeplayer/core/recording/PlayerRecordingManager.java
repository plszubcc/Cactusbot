package io.github.hello09x.fakeplayer.core.recording;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class PlayerRecordingManager implements Listener {
   private final RecordingStorage storage;
   private final FakeplayerManager fakeplayerManager;
   private final Map<UUID, PlayerRecordingManager.RecordingSession> sessions = new ConcurrentHashMap<>();

   @Inject
   public PlayerRecordingManager(RecordingStorage storage, FakeplayerManager fakeplayerManager) {
      this.storage = storage;
      this.fakeplayerManager = fakeplayerManager;
   }

   public boolean start(@NotNull Player player, @NotNull String name) {
      if (!this.fakeplayerManager.isFake(player) && !this.sessions.containsKey(player.getUniqueId()) && this.storage.isValidName(name)) {
         PlayerRecordingManager.RecordingSession session = new PlayerRecordingManager.RecordingSession(name, player, player.getLocation().clone());
         session.task = Main.getInstance().getServer().getScheduler().runTaskTimer(Main.getInstance(), () -> this.sample(session), 0L, 1L);
         this.sessions.put(player.getUniqueId(), session);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public RecordingData stop(@NotNull Player player) throws IOException {
      PlayerRecordingManager.RecordingSession session = this.sessions.remove(player.getUniqueId());
      if (session == null) {
         return null;
      } else {
         if (session.task != null) {
            session.task.cancel();
         }

         RecordingData data = new RecordingData(session.name, player.getName(), System.currentTimeMillis(), session.frames.size(), List.copyOf(session.frames));
         this.storage.save(data);
         return data;
      }
   }

   public boolean isRecording(@NotNull Player player) {
      return this.sessions.containsKey(player.getUniqueId());
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void onDamage(@NotNull EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player player && !this.fakeplayerManager.isFake(player)) {
         PlayerRecordingManager.RecordingSession session = this.sessions.get(player.getUniqueId());
         if (session != null) {
            session.pendingAttack = true;
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void onInteract(@NotNull PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (!this.fakeplayerManager.isFake(player)) {
         PlayerRecordingManager.RecordingSession session = this.sessions.get(player.getUniqueId());
         if (session != null) {
            session.pendingUse = true;
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void onInteractEntity(@NotNull PlayerInteractEntityEvent event) {
      Player player = event.getPlayer();
      if (!this.fakeplayerManager.isFake(player)) {
         PlayerRecordingManager.RecordingSession session = this.sessions.get(player.getUniqueId());
         if (session != null) {
            session.pendingUse = true;
         }
      }
   }

   @EventHandler
   public void onQuit(@NotNull PlayerQuitEvent event) {
      PlayerRecordingManager.RecordingSession session = this.sessions.remove(event.getPlayer().getUniqueId());
      if (session != null && session.task != null) {
         session.task.cancel();
      }
   }

   private void sample(@NotNull PlayerRecordingManager.RecordingSession session) {
      Player player = session.player;
      if (player.isOnline() && !this.fakeplayerManager.isFake(player)) {
         Location location = player.getLocation();
         boolean jumpedThisTick = session.lastOnGround && !player.isOnGround() && location.getY() - session.lastLocation.getY() > 0.12;
         double deltaX = location.getX() - session.lastLocation.getX();
         double deltaZ = location.getZ() - session.lastLocation.getZ();
         double yawRad = Math.toRadians(location.getYaw());
         double forwardX = -Math.sin(yawRad);
         double forwardZ = Math.cos(yawRad);
         double rightX = -forwardZ;
         float forwardInput = this.clamp((deltaX * forwardX + deltaZ * forwardZ) * 4.0);
         float strafeInput = this.clamp((deltaX * rightX + deltaZ * forwardX) * 4.0);
         Double targetDistance = null;
         Float targetAngle = null;
         Float targetPitchAngle = null;
         Player target = this.nearestTarget(player);
         if (target != null) {
            Vector playerEye = player.getEyeLocation().toVector();
            Vector targetEye = target.getEyeLocation().toVector();
            Vector delta = targetEye.clone().subtract(playerEye);
            targetDistance = delta.length();
            float desiredYaw = (float)Math.toDegrees(Math.atan2(-delta.getX(), delta.getZ()));
            double horizontal = Math.sqrt(delta.getX() * delta.getX() + delta.getZ() * delta.getZ());
            float desiredPitch = (float)Math.toDegrees(-Math.atan2(delta.getY(), horizontal));
            targetAngle = this.wrapAngle(location.getYaw() - desiredYaw);
            targetPitchAngle = this.wrapAngle(location.getPitch() - desiredPitch);
         }

         ItemStack held = player.getInventory().getItemInMainHand();
         String heldItem = held.getType() == Material.AIR ? null : held.getType().name().toLowerCase(Locale.ROOT);
         session.frames
            .add(
               new RecordedFrame(
                  session.tick++,
                  location.getX(),
                  location.getY(),
                  location.getZ(),
                  location.getYaw(),
                  location.getPitch(),
                  forwardInput,
                  strafeInput,
                  heldItem,
                  session.pendingAttack,
                  session.pendingUse,
                  session.pendingJump || jumpedThisTick,
                  player.isGliding(),
                  player.isSprinting(),
                  player.isSneaking(),
                  targetDistance,
                  targetAngle,
                  targetPitchAngle
               )
            );
         session.pendingAttack = false;
         session.pendingUse = false;
         session.pendingJump = false;
         session.lastLocation = location.clone();
         session.lastOnGround = player.isOnGround();
      } else {
         if (session.task != null) {
            session.task.cancel();
         }

         this.sessions.remove(player.getUniqueId());
      }
   }

   @Nullable
   private Player nearestTarget(@NotNull Player source) {
      Player nearest = null;
      double nearestDistance = Double.MAX_VALUE;
      Location origin = source.getLocation();

      for (Player other : source.getWorld().getPlayers()) {
         if (!other.equals(source) && !this.fakeplayerManager.isFake(other)) {
            double distance = origin.distance(other.getLocation());
            if (!(distance > 24.0) && !(distance >= nearestDistance)) {
               nearest = other;
               nearestDistance = distance;
            }
         }
      }

      return nearest;
   }

   private float clamp(double value) {
      return (float)Math.max(-1.0, Math.min(1.0, value));
   }

   private float wrapAngle(float value) {
      float wrapped = value;

      while (wrapped <= -180.0F) {
         wrapped += 360.0F;
      }

      while (wrapped > 180.0F) {
         wrapped -= 360.0F;
      }

      return wrapped;
   }

   private static final class RecordingSession {
      private final String name;
      private final Player player;
      private final ArrayList<RecordedFrame> frames = new ArrayList<>();
      private Location lastLocation;
      private BukkitTask task;
      private boolean pendingAttack;
      private boolean pendingUse;
      private boolean pendingJump;
      private boolean lastOnGround = true;
      private int tick;

      private RecordingSession(String name, Player player, Location lastLocation) {
         this.name = name;
         this.player = player;
         this.lastLocation = lastLocation;
      }
   }
}
