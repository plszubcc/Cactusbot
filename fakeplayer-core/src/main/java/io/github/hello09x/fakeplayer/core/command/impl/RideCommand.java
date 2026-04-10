package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RideCommand extends AbstractCommand {

    public RideCommand() {
        super();
    }

    public void rideAnything(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        List<Entity> entities = fake.getNearbyEntities(4.5D, 4.5D, 4.5D);

        if (!entities.isEmpty()) {
            Entity entity = (Entity) entities.stream().filter((ex) -> {
                return ex != fake;
            }).findAny().orElse((Object) null);

            if (entity != null) {
                this.bridge.fromPlayer(fake).startRiding(entity, true);
            }
        }
    }

    public void rideTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Entity entity = fake.getTargetEntity(5);

        if (entity != null) {
            this.bridge.fromPlayer(fake).startRiding(entity, true);
        }
    }

    public void rideEntity(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Entity entity = (Entity) args.get("entity");

        if (entity == fake) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.ride.entity.error.ride-self").color(NamedTextColor.RED));
        } else if (entity != null) {
            if (!entity.isDead()) {
                if (entity.getWorld() == fake.getWorld() && entity.getLocation().distance(fake.getLocation()) <= 24.0D) {
                    this.bridge.fromPlayer(fake).startRiding(entity, true);
                } else {
                    sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.ride.entity.error.too-far", Component.text(fake.getName(), NamedTextColor.WHITE)).color(NamedTextColor.RED));
                }
            }
        }
    }

    public void rideVehicle(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Entity entity = (Entity) fake.getNearbyEntities(4.5D, 4.5D, 4.5D).stream().filter((ex) -> {
            return ex instanceof RideableMinecart || ex instanceof Boat || ex instanceof AbstractHorse;
        }).findFirst().orElse((Object) null);

        if (entity != null) {
            this.bridge.fromPlayer(fake).startRiding(entity, true);
        }
    }

    public void rideMe(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);

        if (fake.getWorld().equals(sender.getWorld()) && fake.getLocation().distance(sender.getLocation()) <= 20.0D) {
            this.bridge.fromPlayer(fake).startRiding(sender, true);
        } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.ride.me.error.too-far", Component.text(fake.getName(), NamedTextColor.WHITE)).color(NamedTextColor.RED));
        }
    }

    public void stopRiding(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        this.bridge.fromPlayer(this.getFakeplayer(sender, args)).stopRiding();
    }
}
