package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.utils.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SleepCommand extends AbstractCommand {

    public SleepCommand() {
        super();
    }

    public void sleep(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);
        Block bed = BlockUtils.getNearbyBlock(fake.getLocation(), 4, (blockx) -> {
            BlockData patt0$temp = blockx.getBlockData();

            if (!(patt0$temp instanceof Bed data)) {
                return false;
            } else {
                return !data.isOccupied() && data.getPart() == Part.HEAD;
            }
        });

        if (bed != null) {
            fake.sleep(bed.getLocation(), false);
        }
    }

    public void wakeup(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player target = this.getFakeplayer(sender, args);

        if (target.isSleeping()) {
            target.wakeup(true);
        }
    }
}
