package io.github.hello09x.fakeplayer.core.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.pvp.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ArenaMatchListener implements Listener {

    private final ArenaManager arenas;

    @Inject
    public ArenaMatchListener(ArenaManager arenas) {
        super();
        this.arenas = arenas;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLethalDamage(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {
            if (player.hasMetadata("fakeplayer:arena_name")) {
                double health = player.getHealth();

                if (health > 0.0D) {
                    if (event.getFinalDamage() >= health) {
                        event.setCancelled(true);
                        if (!this.tryPopTotem(player)) {
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                this.arenas.resolveRoundLoss(player);
                            });
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("fakeplayer:arena_name")) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                try {
                    player.spigot().respawn();
                } catch (Throwable throwable) {
                    ;
                }

                this.arenas.resolveRoundLoss(player);
            }, 1L);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("fakeplayer:arena_name")) {
            this.arenas.handleParticipantQuit(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void noRegen(@NotNull EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {
            if (player.hasMetadata("fakeplayer:arena_name")) {
                event.setCancelled(true);
            }
        }
    }

    private boolean tryPopTotem(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack offhand = inventory.getItemInOffHand();

        if (this.consumeTotem(offhand)) {
            inventory.setItemInOffHand(offhand.getAmount() > 0 ? offhand : null);
            this.applyTotemEffects(player);
            return true;
        } else {
            ItemStack mainHand = inventory.getItemInMainHand();

            if (this.consumeTotem(mainHand)) {
                inventory.setItemInMainHand(mainHand.getAmount() > 0 ? mainHand : null);
                this.applyTotemEffects(player);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean consumeTotem(ItemStack stack) {
        if (stack != null && stack.getType() == Material.TOTEM_OF_UNDYING && stack.getAmount() > 0) {
            stack.setAmount(stack.getAmount() - 1);
            return true;
        } else {
            return false;
        }
    }

    private void applyTotemEffects(@NotNull Player player) {
        double maxHealth = 20.0D;
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);

        if (attr != null) {
            maxHealth = attr.getValue();
        }

        player.setHealth(Math.min(maxHealth, 1.0D));
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 800, 0));
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0.0D, 1.0D, 0.0D), 30, 0.5D, 0.8D, 0.5D, 0.2D);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0F, 1.0F);
        player.updateInventory();
    }
}
