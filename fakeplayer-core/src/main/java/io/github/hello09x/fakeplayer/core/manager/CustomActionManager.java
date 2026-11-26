package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.CustomActionDefinition;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.config.PresetCommandEntry;
import io.github.hello09x.fakeplayer.core.config.PresetConditions;
import io.github.hello09x.fakeplayer.core.manager.combat.CombatTargetResolver;
import io.github.hello09x.fakeplayer.core.manager.combat.PresetCommandChainManager;
import io.github.hello09x.fakeplayer.core.manager.combat.PresetConditionEvaluator;
import io.github.hello09x.fakeplayer.core.manager.combat.PresetHitTracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class CustomActionManager implements Listener {
   private final FakeplayerManager manager;
   private final FakeplayerConfig config;
   private final PresetConditionEvaluator conditionEvaluator;
   private final CombatTargetResolver targetResolver;
   private final PresetCommandChainManager commandChainManager;
   private final PresetHitTracker hitTracker;
   private final Map<UUID, Map<String, List<BukkitTask>>> runningTasks = new ConcurrentHashMap<>();

   @Inject
   public CustomActionManager(
      FakeplayerManager manager,
      FakeplayerConfig config,
      PresetConditionEvaluator conditionEvaluator,
      CombatTargetResolver targetResolver,
      PresetCommandChainManager commandChainManager,
      PresetHitTracker hitTracker
   ) {
      this.manager = manager;
      this.config = config;
      this.conditionEvaluator = conditionEvaluator;
      this.targetResolver = targetResolver;
      this.commandChainManager = commandChainManager;
      this.hitTracker = hitTracker;
   }

   public boolean execute(@NotNull String actionName, @NotNull Player bot) {
      CustomActionDefinition action = this.config.getActions().get(actionName);
      if (action == null) {
         return false;
      } else {
         this.stop(bot, actionName);
         this.schedule(bot, actionName, action);
         return true;
      }
   }

   public void stop(@NotNull Player bot, @NotNull String actionName) {
      Map<String, List<BukkitTask>> byBot = this.runningTasks.get(bot.getUniqueId());
      if (byBot != null) {
         List<BukkitTask> tasks = byBot.remove(this.normalize(actionName));
         if (tasks != null) {
            for (BukkitTask task : tasks) {
               if (task != null && !task.isCancelled()) {
                  task.cancel();
               }
            }
         }

         if (byBot.isEmpty()) {
            this.runningTasks.remove(bot.getUniqueId());
         }
      }
   }

   @EventHandler
   public void onQuit(@NotNull PlayerQuitEvent event) {
      if (!this.manager.isNotFake(event.getPlayer())) {
         this.stopAll(event.getPlayer());
      }
   }

   private void stopAll(@NotNull Player bot) {
      Map<String, List<BukkitTask>> byBot = this.runningTasks.remove(bot.getUniqueId());
      if (byBot != null) {
         for (List<BukkitTask> tasks : byBot.values()) {
            for (BukkitTask task : tasks) {
               if (task != null && !task.isCancelled()) {
                  task.cancel();
               }
            }
         }

         this.hitTracker.clear(bot);
      }
   }

   private void schedule(@NotNull Player bot, @NotNull String actionName, @NotNull CustomActionDefinition action) {
      ArrayList<BukkitTask> tasks = new ArrayList<>();
      String namespace = "action:" + this.normalize(actionName) + ":";

      for (PresetCommandEntry entry : action.commands()) {
         if (entry.repeatTicks() <= 0 && !entry.command().isBlank()) {
            PresetCommandEntry namespaced = this.namespaced(entry, namespace);
            int delay = Math.max(0, namespaced.delayTicks());
            if (delay <= 0) {
               this.dispatchActionCommand(bot, namespaced);
            } else {
               BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                  if (!this.isBotAvailable(bot)) {
                     this.stopAll(bot);
                  } else {
                     this.dispatchActionCommand(bot, namespaced);
                  }
               }, delay);
               tasks.add(task);
            }
         }
      }

      for (PresetCommandEntry entryx : action.commands()) {
         if (entryx.repeatTicks() > 0 && !entryx.command().isBlank()) {
            PresetCommandEntry namespaced = this.namespaced(entryx, namespace);
            int interval = Math.max(1, namespaced.repeatTicks());
            int initialDelay = namespaced.delayTicks() > 0 ? namespaced.delayTicks() : interval;
            long startedAt = System.currentTimeMillis();
            AtomicInteger executions = new AtomicInteger();
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
               if (!this.isBotAvailable(bot)) {
                  this.stopAll(bot);
               } else {
                  if (namespaced.stopTicks() > 0) {
                     long elapsedTicks = Math.max(0L, (System.currentTimeMillis() - startedAt) / 50L);
                     if (elapsedTicks >= namespaced.stopTicks()) {
                        return;
                     }
                  }

                  if (namespaced.limit() <= 0 || executions.get() < namespaced.limit()) {
                     if (this.dispatchActionCommand(bot, namespaced)) {
                        executions.incrementAndGet();
                     }
                  }
               }
            }, initialDelay, interval);
            tasks.add(task);
         }
      }

      this.runningTasks.computeIfAbsent(bot.getUniqueId(), ignored -> new ConcurrentHashMap<>()).put(this.normalize(actionName), tasks);
   }

   private boolean dispatchActionCommand(@NotNull Player bot, @NotNull PresetCommandEntry entry) {
      if (entry.command().isBlank()) {
         return false;
      } else if (!this.conditionEvaluator.test(bot, entry.conditions())) {
         return false;
      } else {
         Player target = this.targetResolver.resolve(bot);
         String formatted = entry.command();
         if (target != null) {
            formatted = formatted.replace("%target%", target.getName());
         }

         if (this.manager.dispatchCommand(bot, formatted)) {
            this.commandChainManager.markExecuted(bot, entry.id());
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean isBotAvailable(@NotNull Player bot) {
      return bot.isOnline() && !this.manager.isNotFake(bot);
   }

   @NotNull
   private PresetCommandEntry namespaced(@NotNull PresetCommandEntry entry, @NotNull String namespace) {
      String id = entry.id() == null ? null : namespace + entry.id();
      return new PresetCommandEntry(
         id, entry.command(), entry.repeatTicks(), entry.delayTicks(), entry.stopTicks(), entry.limit(), this.namespaced(entry.conditions(), namespace)
      );
   }

   @Nullable
   private PresetConditions namespaced(@Nullable PresetConditions conditions, @NotNull String namespace) {
      if (conditions == null) {
         return null;
      } else {
         String after = conditions.afterCommand();
         if (after != null && !after.isBlank()) {
            after = namespace + after;
         }

         return new PresetConditions(
            conditions.selfHealthMin(),
            conditions.selfHealthMax(),
            conditions.selfOnFire(),
            conditions.targetHealthMin(),
            conditions.targetHealthMax(),
            conditions.targetDistanceMin(),
            conditions.targetDistanceMax(),
            conditions.targetBlocking(),
            conditions.targetHoldingAxe(),
            conditions.targetInCobweb(),
            conditions.targetHit(),
            conditions.targetHitWithinTicks(),
            after,
            conditions.afterCommandWithinTicks()
         );
      }
   }

   @NotNull
   private String normalize(@NotNull String name) {
      return name.trim().toLowerCase(Locale.ROOT);
   }
}
