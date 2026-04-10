package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.wrappers.Rotation;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.util.Mth;
import io.papermc.paper.entity.LookAnchor;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RotationCommand extends AbstractCommand {

    public RotationCommand() {
        super();
    }

    public void lookAt(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Location location = (Location) Objects.requireNonNull((Location) args.get("location"));

        fake.lookAt(location, LookAnchor.EYES);
    }

    public void lookEntity(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Entity entity = (Entity) Objects.requireNonNull((Entity) args.get("entity"));

        if (Objects.equals(fake.getWorld(), entity.getWorld())) {
            fake.lookAt(entity, LookAnchor.EYES, LookAnchor.EYES);
        }
    }

    public CommandExecutor look(@NotNull Direction direction) {
        return (senderx, argsx) -> {
            Player fake = this.getFakeplayer(senderx, argsx);

            this.look(fake, direction);
        };
    }

    public void lookMe(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);

        if (Objects.equals(fake.getWorld(), sender.getWorld())) {
            fake.lookAt(sender.getEyeLocation(), LookAnchor.EYES);
        }
    }

    private void look(@NotNull Player fake, @NotNull Direction direction) {
        switch (direction) {
            case NORTH:
                this.look(fake, 180.0F, 0.0F);
                break;
            case SOUTH:
                this.look(fake, 0.0F, 0.0F);
                break;
            case EAST:
                this.look(fake, -90.0F, 0.0F);
                break;
            case WEST:
                this.look(fake, 90.0F, 0.0F);
                break;
            case UP:
                this.look(fake, fake.getLocation().getYaw(), -90.0F);
                break;
            case DOWN:
                this.look(fake, fake.getLocation().getYaw(), 90.0F);
        }

    }

    private void look(@NotNull Player fake, float yaw, float pitch) {
        NMSServerPlayer handle = this.bridge.fromPlayer(fake);

        handle.setYRot(yaw % 360.0F);
        handle.setXRot(Mth.clamp(pitch, -90.0F, 90.0F));
    }

    public CommandExecutor turn(float yaw, float pitch) {
        return (senderx, argsx) -> {
            Player fake = this.getFakeplayer(senderx, argsx);

            this.turn(fake, yaw, pitch);
        };
    }

    public void turnTo(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Rotation rotation = (Rotation) Objects.requireNonNull((Rotation) args.get("rotation"));

        this.turn(fake, rotation.getYaw(), rotation.getPitch());
    }

    private void turn(@NotNull Player fake, float yaw, float pitch) {
        Location pos = fake.getLocation();

        this.look(fake, pos.getYaw() + yaw, pos.getPitch() + pitch);
    }
}
