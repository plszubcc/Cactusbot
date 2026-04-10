package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CritCommand extends AbstractCommand {

    public CritCommand() {
        super();
    }

    public void crit(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        Player fake = this.getFakeplayer(sender, args);

        fake.setMetadata("fakeplayer:force_crit", new FixedMetadataValue(Main.getInstance(), true));
    }
}
