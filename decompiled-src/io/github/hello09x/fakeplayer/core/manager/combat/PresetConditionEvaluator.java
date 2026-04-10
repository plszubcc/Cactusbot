package io.github.hello09x.fakeplayer.core.manager.combat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.config.PresetConditions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PresetConditionEvaluator {

    private final CombatTargetResolver targetResolver;
    private final PresetCommandChainManager commandChainManager;
    private final PresetHitTracker hitTracker;

    @Inject
    public PresetConditionEvaluator(CombatTargetResolver targetResolver, PresetCommandChainManager commandChainManager, PresetHitTracker hitTracker) {
        super();
        this.targetResolver = targetResolver;
        this.commandChainManager = commandChainManager;
        this.hitTracker = hitTracker;
    }

    public boolean test(@NotNull Player bot, PresetConditions conditions) {
        if (conditions != null && !conditions.isEmpty()) {
            if (!this.within(bot.getHealth(), conditions.selfHealthMin(), conditions.selfHealthMax())) {
                return false;
            } else if (conditions.selfOnFire() != null && conditions.selfOnFire() != bot.getFireTicks() > 0) {
                return false;
            } else if (!this.commandChainManager.wasExecutedRecently(bot, conditions.afterCommand(), conditions.afterCommandWithinTicks())) {
                return false;
            } else {
                Player target = this.targetResolver.resolve(bot);
                boolean needsTarget = conditions.targetHealthMin() != null || conditions.targetHealthMax() != null || conditions.targetDistanceMin() != null || conditions.targetDistanceMax() != null || conditions.targetBlocking() != null || conditions.targetHoldingAxe() != null || conditions.targetInCobweb() != null || conditions.targetHit() != null || conditions.targetHitWithinTicks() != null;

                if (!needsTarget) {
                    return true;
                } else if (target == null) {
                    return false;
                } else if (!this.within(target.getHealth(), conditions.targetHealthMin(), conditions.targetHealthMax())) {
                    return false;
                } else {
                    double distance = this.targetResolver.distance(bot, target);

                    if (!this.within(distance, conditions.targetDistanceMin(), conditions.targetDistanceMax())) {
                        return false;
                    } else if (conditions.targetBlocking() != null && conditions.targetBlocking() != target.isBlocking()) {
                        return false;
                    } else if (conditions.targetHoldingAxe() != null && conditions.targetHoldingAxe() != this.isHoldingAxe(target)) {
                        return false;
                    } else if (conditions.targetInCobweb() != null && conditions.targetInCobweb() != this.isInCobweb(target)) {
                        return false;
                    } else {
                        if (conditions.targetHit() != null) {
                            int withinTicks = conditions.targetHitWithinTicks() == null ? 10 : Math.max(1, conditions.targetHitWithinTicks());
                            boolean hit = this.hitTracker.targetHitRecently(bot, target, withinTicks);

                            if (conditions.targetHit() && !hit) {
                                return false;
                            }

                            if (!conditions.targetHit() && hit) {
                                return false;
                            }
                        }

                        return true;
                    }
                }
            }
        } else {
            return true;
        }
    }

    private boolean isHoldingAxe(@NotNull Player target) {
        return this.isAxe(target.getInventory().getItemInMainHand().getType()) || this.isAxe(target.getInventory().getItemInOffHand().getType());
    }

    private boolean isAxe(@NotNull Material material) {
        return material.name().endsWith("_AXE");
    }

    private boolean isInCobweb(@NotNull Player target) {
        Material feet = target.getLocation().getBlock().getType();
        Material head = target.getEyeLocation().getBlock().getType();

        return feet == Material.COBWEB || head == Material.COBWEB;
    }

    private boolean within(double value, Double min, Double max) {
        return min != null && value < min ? false : max == null || value <= max;
    }
}
