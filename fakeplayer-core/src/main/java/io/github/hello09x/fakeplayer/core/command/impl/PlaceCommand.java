package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.api.spi.PlaceSide;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlaceCommand extends AbstractCommand {

    public PlaceCommand() {
        super();
    }

    public void place(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        double range = this.getRange(args);

        this.bridge.placeHeldBlock(fake, range, this.getSide(args));
    }

    private double getRange(@NotNull CommandArguments args) {
        Object value = args.get("range");

        if (value instanceof Number number) {
            return number.doubleValue();
        } else {
            if (value instanceof String text) {
                try {
                    return Double.parseDouble(text);
                } catch (NumberFormatException numberformatexception) {
                    ;
                }
            }

            return 4.5D;
        }
    }

    private @NotNull PlaceSide getSide(@NotNull CommandArguments args) {
        Object value = args.get("side");

        return value != null ? PlaceSide.from(String.valueOf(value)) : PlaceSide.FRONT;
    }
}
