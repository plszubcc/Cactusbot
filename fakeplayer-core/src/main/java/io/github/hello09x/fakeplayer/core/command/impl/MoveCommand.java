package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.Main;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Range;

@Singleton
public class MoveCommand extends AbstractCommand {
   private final Map<UUID, BukkitTask> stopTasks = new HashMap<>();

   public MoveCommand() {
   }

   public CommandExecutor move(@Range(from = 0L,to = 1L) float forward, @Range(from = 0L,to = 1L) float strafing) {
      return (sender, args) -> {
         Player fake = this.getFakeplayer(sender, args);
         final NMSServerPlayer handle = this.bridge.fromPlayer(fake);
         float vel = fake.isSneaking() ? 0.3F : 1.0F;
         if (forward != 0.0F) {
            handle.setZza(vel * forward);
         }

         if (strafing != 0.0F) {
            handle.setXxa(vel * strafing);
         }

         BukkitTask task = this.stopTasks.remove(fake.getUniqueId());
         if (task != null && !task.isCancelled()) {
            task.cancel();
         }

         final UUID fakeId = fake.getUniqueId();
         var stopping = new BukkitRunnable() {
            public void run() {
               handle.setXxa(0.0F);
               handle.setZza(0.0F);
               BukkitTask self = MoveCommand.this.stopTasks.get(fakeId);
               if (self != null && self.getTaskId() == this.getTaskId()) {
                  MoveCommand.this.stopTasks.remove(fakeId);
               }
            }
         };
         this.stopTasks.put(fakeId, stopping.runTaskLater(Main.getInstance(), fake.isSprinting() ? 40L : 20L));
      };
   }
}
